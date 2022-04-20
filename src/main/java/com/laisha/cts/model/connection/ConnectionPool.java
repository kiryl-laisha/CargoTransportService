package com.laisha.cts.model.connection;

import com.laisha.cts.exception.ConnectionCreatorException;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ConnectionPool {

    private static final Logger logger = LogManager.getLogger();
    private static final ConnectionCreator creator = ConnectionCreator.getInstance();
    private static final AtomicBoolean isInstanceCreated = new AtomicBoolean(false);

    private static final String DB_PROPERTY_POOL_CAPACITY = "pool_capacity";
    private static final String DB_PROPERTY_QUANTITY_OF_EXTRA_ATTEMPTS = "extra_attempt_quantity";
    private static final String INTEGER_REGEXP = "\\d+";
    private static final int DEFAULT_CONNECTION_POOL_CAPACITY = 10;
    private static final int DEFAULT_QUANTITY_OF_EXTRA_ATTEMPTS_CREATE_CONNECTION = 10;
    private static final int QUANTITY_OF_EXTRA_ATTEMPTS_CREATE_CONNECTION;
    static final int CONNECTION_POOL_CAPACITY;

    private static Lock lock = new ReentrantLock();
    private static Condition condition = lock.newCondition();
    private static ConnectionPool instance;
    private final BlockingQueue<ProxyConnection> freeConnections;
    private final BlockingQueue<ProxyConnection> usedConnections;

    static {
        String connectionPoolCapacity = ConnectionCreator.databaseProperties
                .getProperty(DB_PROPERTY_POOL_CAPACITY);
        if (connectionPoolCapacity != null && connectionPoolCapacity.matches(INTEGER_REGEXP)) {
            CONNECTION_POOL_CAPACITY = Integer.parseInt(connectionPoolCapacity);
        } else {
            CONNECTION_POOL_CAPACITY = DEFAULT_CONNECTION_POOL_CAPACITY;
        }
        String extraAttemptsQuantity = ConnectionCreator.databaseProperties.
                getProperty(DB_PROPERTY_QUANTITY_OF_EXTRA_ATTEMPTS);
        if (extraAttemptsQuantity != null && extraAttemptsQuantity.matches(INTEGER_REGEXP)) {
            QUANTITY_OF_EXTRA_ATTEMPTS_CREATE_CONNECTION = Integer.parseInt(extraAttemptsQuantity);
        } else {
            QUANTITY_OF_EXTRA_ATTEMPTS_CREATE_CONNECTION = DEFAULT_QUANTITY_OF_EXTRA_ATTEMPTS_CREATE_CONNECTION;
        }

    }

    private ConnectionPool() {

        freeConnections = new LinkedBlockingQueue<>(CONNECTION_POOL_CAPACITY);
        usedConnections = new LinkedBlockingQueue<>(CONNECTION_POOL_CAPACITY);
        Connection connection;
        ProxyConnection proxyConnection;
        for (int i = 0; i < CONNECTION_POOL_CAPACITY; i++) {
            try {
                connection = creator.createConnection();
                proxyConnection = new ProxyConnection(connection);
                freeConnections.add(proxyConnection);
            } catch (ConnectionCreatorException e) {
                logger.log(Level.WARN, "Connection was not created.", e);
            }
        }
        logger.log(Level.INFO, "{} connections has been created while pool was created " +
                "by straight way.", freeConnections.size());
        int i = 0;
        while (freeConnections.size() != CONNECTION_POOL_CAPACITY) {
            try {
                connection = creator.createConnection();
                proxyConnection = new ProxyConnection(connection);
                freeConnections.add(proxyConnection);
            } catch (ConnectionCreatorException e) {
                logger.log(Level.WARN, "Extra connection has not been created.", e);
                i++;
                if (i == QUANTITY_OF_EXTRA_ATTEMPTS_CREATE_CONNECTION) {
                    logger.log(Level.FATAL, "Specified quantity of connections \"{}\" has not been" +
                            " created, application could not work.", CONNECTION_POOL_CAPACITY);
                    throw new RuntimeException("Specified quantity of connections has not been " +
                            "created, application could not work.");
                }
            }
        }
        ConnectionPoolCapacityTask.startPoolCapacityTask(freeConnections, usedConnections);
        //TODO add control of connection state?
    }

    public static ConnectionPool getInstance() {

        if (!isInstanceCreated.get()) {
            lock.lock();
            try {
                if (instance == null) {
                    instance = new ConnectionPool();
                    isInstanceCreated.set(true);
                }
            } finally {
                lock.unlock();
            }
        }
        return instance;
    }

    public Connection takeConnection() {

        lock.lock();
        ProxyConnection connection = null;
        try {
            while (freeConnections.isEmpty()) {
                condition.await();
            }
            connection = freeConnections.take();
            usedConnections.put(connection);
        } catch (InterruptedException e) {
            logger.log(Level.WARN, "Thread was interrupted while waiting of " +
                    "available connection.", e);
            Thread.currentThread().interrupt();
        } finally {
            lock.unlock();
        }
        return connection;
    }

    public boolean releaseConnection(Connection connection) {

        boolean isReleased = false;
        if (connection instanceof ProxyConnection) {
            lock.lock();
            try {
                usedConnections.remove((ProxyConnection) connection);
                freeConnections.put((ProxyConnection) connection);
                condition.signal();
                isReleased = true;
            } catch (InterruptedException e) {
                logger.log(Level.WARN, "Thread was interrupted while waiting of " +
                        "release of connection.", e);
                Thread.currentThread().interrupt();
            }
        } else {
            logger.log(Level.WARN, "Specified connection is not instance of ProxyConnection.");
        }
        return isReleased;
    }

    public void destroyConnectionPool() {

        for (int i = 0; i < CONNECTION_POOL_CAPACITY; i++) {
            try {
                freeConnections.take().trueClose();
            } catch (InterruptedException e) {
                logger.log(Level.WARN, "Thread was interrupted while waiting of " +
                        "available connection.", e);
                Thread.currentThread().interrupt();
            }
        }
        ConnectionPoolCapacityTask.terminatePoolCapacityTask();
        deregisterDrivers();
    }

    private void deregisterDrivers() {

        DriverManager.getDrivers().asIterator().forEachRemaining(driver -> {
            try {
                DriverManager.deregisterDriver(driver);
                logger.log(Level.DEBUG, "Driver was removed from driver list.");
            } catch (SQLException e) {
                logger.log(Level.ERROR, "Database access error occurred when " +
                        "driver was removed from driver list.");
            }
        });
    }
}
