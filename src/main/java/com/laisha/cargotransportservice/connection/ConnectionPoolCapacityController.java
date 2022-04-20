package com.laisha.cargotransportservice.connection;

import com.laisha.cargotransportservice.exception.ConnectionCreatorException;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.util.TimerTask;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.locks.Lock;

public class ConnectionPoolCapacityController extends TimerTask {

    private static final Logger logger = LogManager.getLogger();
    private static final ConnectionCreator creator = ConnectionCreator.getInstance();
    private final BlockingQueue<ProxyConnection> freeConnections;
    private final BlockingQueue<ProxyConnection> usedConnections;
    private final Lock lock;

    ConnectionPoolCapacityController(Lock lock,
                                     BlockingQueue<ProxyConnection> freeConnections,
                                     BlockingQueue<ProxyConnection> usedConnections) {
        this.lock = lock;
        this.freeConnections = freeConnections;
        this.usedConnections = usedConnections;
    }

    @Override
    public void run() {

        lock.lock();
        Connection connection;
        ProxyConnection proxyConnection;
        try {
            int i = 0;
            while (ConnectionPool.CONNECTION_POOL_CAPACITY !=
                    freeConnections.size() + usedConnections.size() &&
                    i <= ConnectionPool.QUANTITY_OF_EXTRA_ATTEMPTS_OF_CONNECTION_CREATION) {
                try {
                    connection = creator.createConnection();
                    proxyConnection = new ProxyConnection(connection);
                    freeConnections.add(proxyConnection);
                } catch (ConnectionCreatorException e) {
                    logger.log(Level.WARN, "Extra connection has not been created.", e);
                    i++;
                    //TODO what to do if the pool isn't recovered?
                }
            }
        } finally {
            lock.unlock();
        }
    }
}

