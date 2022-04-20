package com.laisha.cts.connection;

import com.laisha.cts.exception.ConnectionCreatorException;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.BlockingQueue;

import static com.laisha.cts.connection.ConnectionCreator.databaseProperties;

public class ConnectionPoolCapacityTask extends TimerTask {

    private static final Logger logger = LogManager.getLogger();
    private static final ConnectionCreator creator = ConnectionCreator.getInstance();
    private static final Timer timer = new Timer();
    private static final String DB_PROPERTY_POOL_CAPACITY_TASK_TIME_DELAY =
            "task_time_delay";
    private static final String DB_PROPERTY_POOL_CAPACITY_TASK_TIME_INTERVAL =
            "task_time_interval";
    private static final String DB_PROPERTY_QUANTITY_OF_ATTEMPTS_CREATE_CONNECTION =
            "task_attempt_quantity";
    private static final String INTEGER_REGEXP = "\\d+";
    private static final int DEFAULT_POOL_CAPACITY_TASK_TIME_DELAY = 5000;
    private static final int DEFAULT_POOL_CAPACITY_TASK_TIME_INTERVAL = 5000;
    private static final int DEFAULT_QUANTITY_OF_ATTEMPTS_CREATE_CONNECTION = 5;
    private static final int POOL_CAPACITY_TASK_TIME_DELAY;
    private static final int POOL_CAPACITY_TASK_TIME_INTERVAL;
    private static final int QUANTITY_OF_ATTEMPTS_CREATE_CONNECTION;
    private static BlockingQueue<ProxyConnection> freeConnections;
    private static BlockingQueue<ProxyConnection> usedConnections;


    static {

        String attemptsQuantity = databaseProperties.
                getProperty(DB_PROPERTY_QUANTITY_OF_ATTEMPTS_CREATE_CONNECTION);
        if (attemptsQuantity != null && attemptsQuantity.matches(INTEGER_REGEXP)) {
            QUANTITY_OF_ATTEMPTS_CREATE_CONNECTION = Integer.parseInt(attemptsQuantity);
        } else {
            QUANTITY_OF_ATTEMPTS_CREATE_CONNECTION = DEFAULT_QUANTITY_OF_ATTEMPTS_CREATE_CONNECTION;
        }
        String timeDelay = databaseProperties.
                getProperty(DB_PROPERTY_POOL_CAPACITY_TASK_TIME_DELAY);
        if (timeDelay != null && timeDelay.matches(INTEGER_REGEXP)) {
            POOL_CAPACITY_TASK_TIME_DELAY = Integer.parseInt(timeDelay);
        } else {
            POOL_CAPACITY_TASK_TIME_DELAY = DEFAULT_POOL_CAPACITY_TASK_TIME_DELAY;
        }
        String timeInterval = databaseProperties.
                getProperty(DB_PROPERTY_POOL_CAPACITY_TASK_TIME_INTERVAL);
        if (timeInterval != null && timeInterval.matches(INTEGER_REGEXP)) {
            POOL_CAPACITY_TASK_TIME_INTERVAL = Integer.parseInt(timeInterval);
        } else {
            POOL_CAPACITY_TASK_TIME_INTERVAL = DEFAULT_POOL_CAPACITY_TASK_TIME_INTERVAL;
        }
    }

    private ConnectionPoolCapacityTask() {
    }

    @Override
    public void run() {

        Connection connection;
        ProxyConnection proxyConnection;
        int attemptCount = 0;
        while (ConnectionPool.CONNECTION_POOL_CAPACITY !=
                freeConnections.size() + usedConnections.size() &&
                attemptCount <= QUANTITY_OF_ATTEMPTS_CREATE_CONNECTION) {
            try {
                connection = creator.createConnection();
                proxyConnection = new ProxyConnection(connection);
                freeConnections.add(proxyConnection);
            } catch (ConnectionCreatorException e) {
                logger.log(Level.WARN, "Extra connection has not been created.", e);
                attemptCount++;
                //TODO what to do if the pool isn't recovered?
            }
        }
    }

    static void startPoolCapacityTask(BlockingQueue<ProxyConnection> freeConnections,
                                      BlockingQueue<ProxyConnection> usedConnections) {

        ConnectionPoolCapacityTask.freeConnections = freeConnections;
        ConnectionPoolCapacityTask.usedConnections = usedConnections;
        ConnectionPoolCapacityTask capacityTask = new ConnectionPoolCapacityTask();
        timer.schedule(capacityTask,
                POOL_CAPACITY_TASK_TIME_DELAY,
                POOL_CAPACITY_TASK_TIME_INTERVAL);
    }

    static void terminatePoolCapacityTask() {
        timer.cancel();
    }
}

