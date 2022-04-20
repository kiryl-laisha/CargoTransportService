package com.laisha.cargotransportservice.connection;

import com.laisha.cargotransportservice.exception.ConnectionCreatorException;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class ConnectionCreator {

    private static final Logger logger = LogManager.getLogger();

    private static final String DATABASE_PROPERTY_FILE = "config/database.properties";
    private static final String DATABASE_PROPERTY_DRIVER = "driver";
    private static final String DATABASE_PROPERTY_URL = "url";

    private static final ConnectionCreator instance = new ConnectionCreator();
    private static final String URL;
    static final Properties databaseProperties = new Properties();

    static {
        String driverName = null;
        try (InputStream inputStream = ConnectionCreator.class.getClassLoader()
                .getResourceAsStream(DATABASE_PROPERTY_FILE)) {
            databaseProperties.load(inputStream);
            driverName = databaseProperties.getProperty(DATABASE_PROPERTY_DRIVER);
            Class.forName(driverName);
            URL = databaseProperties.getProperty(DATABASE_PROPERTY_URL);
        } catch (IOException e) {
            logger.log(Level.FATAL, "The properties file \"{}\" could not be loaded. {}.", driverName, e);
            throw new RuntimeException("The properties file " + driverName + " could not be loaded. ", e);
        } catch (ClassNotFoundException e) {
            logger.log(Level.FATAL, "The driver \"{}\" registration is failed. {}.", driverName, e);
            throw new RuntimeException("The driver " + driverName + " registration is failed. ", e);
        }
    }

    private ConnectionCreator() {
    }

    public static ConnectionCreator getInstance() {
        return instance;
    }

    public Connection createConnection() throws ConnectionCreatorException {

        Connection connection;
        try {
            connection = DriverManager.getConnection(URL, databaseProperties);
        } catch (SQLException e) {
            throw new ConnectionCreatorException("Connection was not created.", e);
        }
        return connection;
    }
}
