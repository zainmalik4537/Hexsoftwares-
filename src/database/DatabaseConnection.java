package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Logger;
import java.util.logging.Level;
import utils.Constants;

/**
 * Database connection manager class
 * Handles MySQL database connections with connection pooling
 */
public class DatabaseConnection {
    private static final Logger logger = Logger.getLogger(DatabaseConnection.class.getName());
    private static DatabaseConnection instance;
    private Connection connection;
    
    // Database connection parameters
    private final String URL = Constants.DB_URL;
    private final String USERNAME = Constants.DB_USERNAME;
    private final String PASSWORD = Constants.DB_PASSWORD;
    
    // Private constructor for singleton pattern
    private DatabaseConnection() {
        try {
            // Load MySQL JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            this.connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            logger.info("Database connection established successfully");
        } catch (ClassNotFoundException e) {
            logger.log(Level.SEVERE, "MySQL JDBC driver not found", e);
            throw new RuntimeException("MySQL JDBC driver not found", e);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Failed to establish database connection", e);
            throw new RuntimeException("Failed to establish database connection", e);
        }
    }
    
    /**
     * Get singleton instance of DatabaseConnection
     */
    public static synchronized DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }
    
    /**
     * Get database connection
     */
    public Connection getConnection() {
        try {
            // Check if connection is closed or invalid
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
                logger.info("Database connection reestablished");
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Failed to get database connection", e);
            throw new RuntimeException("Failed to get database connection", e);
        }
        return connection;
    }
    
    /**
     * Test database connection
     */
    public boolean testConnection() {
        try {
            Connection conn = getConnection();
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            logger.log(Level.WARNING, "Connection test failed", e);
            return false;
        }
    }
    
    /**
     * Close database connection
     */
    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                logger.info("Database connection closed successfully");
            }
        } catch (SQLException e) {
            logger.log(Level.WARNING, "Failed to close database connection", e);
        }
    }
    
    /**
     * Get connection status information
     */
    public String getConnectionStatus() {
        try {
            if (connection != null && !connection.isClosed()) {
                return "Connected to: " + connection.getMetaData().getURL();
            } else {
                return "Not connected";
            }
        } catch (SQLException e) {
            return "Connection status unknown: " + e.getMessage();
        }
    }
}