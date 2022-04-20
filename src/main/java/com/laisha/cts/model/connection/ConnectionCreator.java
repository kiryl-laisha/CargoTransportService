package com.laisha.cts.model.connection;

import com.laisha.cts.exception.ConnectionCreatorException;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

class ConnectionCreator {

    private static final Logger logger = LogManager.getLogger();

    private static final String DB_PROPERTY_FILE = "config/database.properties";
    private static final String DB_PROPERTY_DRIVER = "driver";
    private static final String DB_PROPERTY_URL = "url";

    private static final ConnectionCreator instance = new ConnectionCreator();
    private static final String URL;
    static final Properties databaseProperties = new Properties();

    static {
        String driverName = null;
        try (InputStream inputStream = ConnectionCreator.class.getClassLoader()
                .getResourceAsStream(DB_PROPERTY_FILE)) {
            databaseProperties.load(inputStream);
            driverName = databaseProperties.getProperty(DB_PROPERTY_DRIVER);
            Class.forName(driverName);
            URL = databaseProperties.getProperty(DB_PROPERTY_URL);
        } catch (IOException e) {
            logger.log(Level.FATAL, "The file with properties \"{}\" could not be " +
                    "loaded. {}.", DB_PROPERTY_FILE, e);
            throw new ExceptionInInitializerError("The file with properties \""
                    + DB_PROPERTY_FILE + "\" could not be loaded. ");
        } catch (ClassNotFoundException e) {
            logger.log(Level.FATAL, "The driver \"{}\" registration was failed. {}", driverName, e);
            throw new ExceptionInInitializerError("The driver \"" + driverName + " " +
                    "\" registration was failed. ");
        }
    }

    private ConnectionCreator() {
    }

    static ConnectionCreator getInstance() {
        return instance;
    }

    Connection createConnection() throws ConnectionCreatorException {

        Connection connection;
        try {
            connection = DriverManager.getConnection(URL, databaseProperties);
        } catch (SQLException e) {
            throw new ConnectionCreatorException("Connection was not created.", e);
        }
        return connection;
    }
}
