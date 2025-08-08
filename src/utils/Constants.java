package utils;

/**
 * Constants class containing application-wide configuration
 * Centralized location for all constants and configuration values
 */
public class Constants {
    
    // Database Configuration
    public static final String DB_URL = "jdbc:mysql://localhost:3306/library_management";
    public static final String DB_USERNAME = "root";
    public static final String DB_PASSWORD = "";  // Update with your MySQL password
    
    // Application Information
    public static final String APP_NAME = "Library Management System";
    public static final String APP_VERSION = "1.0.0";
    public static final String APP_AUTHOR = "Library Admin";
    
    // UI Constants
    public static final int WINDOW_WIDTH = 1200;
    public static final int WINDOW_HEIGHT = 800;
    public static final int LOGIN_WINDOW_WIDTH = 400;
    public static final int LOGIN_WINDOW_HEIGHT = 300;
    
    // Colors (RGB values)
    public static final String PRIMARY_COLOR = "#2196F3";
    public static final String SECONDARY_COLOR = "#FFC107";
    public static final String SUCCESS_COLOR = "#4CAF50";
    public static final String ERROR_COLOR = "#F44336";
    public static final String WARNING_COLOR = "#FF9800";
    
    // Table Column Names
    public static final String[] BOOK_COLUMNS = {
        "ID", "Title", "Author", "ISBN", "Quantity", "Status", "Date Added"
    };
    
    public static final String[] STAFF_COLUMNS = {
        "ID", "Name", "Role", "Hire Date", "Status", "Email", "Phone"
    };
    
    // Validation Constants
    public static final int MIN_ISBN_LENGTH = 10;
    public static final int MAX_ISBN_LENGTH = 13;
    public static final int MIN_PASSWORD_LENGTH = 6;
    public static final int MAX_NAME_LENGTH = 100;
    public static final int MAX_TITLE_LENGTH = 255;
    
    // Status Values
    public static final String ACTIVE_STATUS = "ACTIVE";
    public static final String INACTIVE_STATUS = "INACTIVE";
    public static final String AVAILABLE_STATUS = "AVAILABLE";
    public static final String OUT_OF_STOCK_STATUS = "OUT_OF_STOCK";
    
    // Messages
    public static final String LOGIN_SUCCESS_MSG = "Login successful!";
    public static final String LOGIN_FAILED_MSG = "Invalid username or password.";
    public static final String CONNECTION_ERROR_MSG = "Database connection failed.";
    public static final String OPERATION_SUCCESS_MSG = "Operation completed successfully.";
    public static final String OPERATION_FAILED_MSG = "Operation failed. Please try again.";
    
    // File Paths
    public static final String LOG_FILE_PATH = "logs/library_system.log";
    public static final String CONFIG_FILE_PATH = "config/app.properties";
    
    // Default Values
    public static final int DEFAULT_BOOK_QUANTITY = 1;
    public static final String DEFAULT_STAFF_STATUS = ACTIVE_STATUS;
    public static final String DEFAULT_BOOK_STATUS = AVAILABLE_STATUS;
    
    // Regular Expressions
    public static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@(.+)$";
    public static final String PHONE_REGEX = "^[0-9-+()\\s]+$";
    public static final String ISBN_REGEX = "^[0-9-]+$";
    
    // Private constructor to prevent instantiation
    private Constants() {
        throw new AssertionError("Constants class should not be instantiated");
    }
}