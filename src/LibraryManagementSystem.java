import gui.LoginFrame;
import database.DatabaseConnection;
import utils.Constants;

import javax.swing.*;
import java.awt.*;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * Main class for Library Management System
 * Entry point of the application with initialization and error handling
 */
public class LibraryManagementSystem {
    private static final Logger logger = Logger.getLogger(LibraryManagementSystem.class.getName());
    
    /**
     * Main method - Application entry point
     */
    public static void main(String[] args) {
        // Set system properties for better UI experience
        System.setProperty("sun.java2d.metal", "true");
        System.setProperty("swing.aatext", "true");
        System.setProperty("awt.useSystemAAFontSettings", "on");
        
        // Initialize logging
        logger.info("Starting Library Management System v" + Constants.APP_VERSION);
        
        // Set look and feel to system default
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            logger.info("System Look and Feel set successfully");
        } catch (Exception e) {
            logger.log(Level.WARNING, "Could not set system Look and Feel", e);
        }
        
        // Test database connection before starting GUI
        SwingUtilities.invokeLater(() -> {
            try {
                // Show splash screen
                showSplashScreen();
                
                // Test database connection
                DatabaseConnection dbConnection = DatabaseConnection.getInstance();
                if (dbConnection.testConnection()) {
                    logger.info("Database connection test successful");
                    
                    // Hide splash and show login
                    hideSplashScreen();
                    
                    // Create and show login frame
                    LoginFrame loginFrame = new LoginFrame();
                    loginFrame.setVisible(true);
                    
                    logger.info("Login frame displayed successfully");
                } else {
                    // Database connection failed
                    hideSplashScreen();
                    showDatabaseError();
                }
            } catch (Exception e) {
                logger.log(Level.SEVERE, "Failed to initialize application", e);
                hideSplashScreen();
                showFatalError(e);
            }
        });
    }
    
    /**
     * Show splash screen while initializing
     */
    private static JWindow splashWindow;
    
    private static void showSplashScreen() {
        splashWindow = new JWindow();
        
        // Create splash panel
        JPanel splashPanel = new JPanel(new BorderLayout());
        splashPanel.setBackground(new Color(33, 150, 243));
        splashPanel.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));
        
        // Application title
        JLabel titleLabel = new JLabel(Constants.APP_NAME);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        // Version info
        JLabel versionLabel = new JLabel("Version " + Constants.APP_VERSION);
        versionLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        versionLabel.setForeground(Color.WHITE);
        versionLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        // Loading message
        JLabel loadingLabel = new JLabel("Initializing...");
        loadingLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        loadingLabel.setForeground(Color.WHITE);
        loadingLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        // Progress bar
        JProgressBar progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);
        progressBar.setBackground(new Color(33, 150, 243));
        progressBar.setForeground(Color.WHITE);
        
        // Layout splash components
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setBackground(new Color(33, 150, 243));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 20, 10, 20);
        
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        centerPanel.add(titleLabel, gbc);
        
        gbc.gridy = 1;
        centerPanel.add(versionLabel, gbc);
        
        gbc.gridy = 2;
        gbc.insets = new Insets(20, 20, 10, 20);
        centerPanel.add(loadingLabel, gbc);
        
        gbc.gridy = 3;
        gbc.insets = new Insets(10, 20, 20, 20);
        centerPanel.add(progressBar, gbc);
        
        splashPanel.add(centerPanel, BorderLayout.CENTER);
        
        // Add author info at bottom
        JLabel authorLabel = new JLabel("© 2024 " + Constants.APP_AUTHOR);
        authorLabel.setFont(new Font("Arial", Font.PLAIN, 10));
        authorLabel.setForeground(Color.WHITE);
        authorLabel.setHorizontalAlignment(SwingConstants.CENTER);
        authorLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        splashPanel.add(authorLabel, BorderLayout.SOUTH);
        
        splashWindow.setContentPane(splashPanel);
        splashWindow.setSize(400, 300);
        splashWindow.setLocationRelativeTo(null);
        splashWindow.setVisible(true);
        
        logger.info("Splash screen displayed");
    }
    
    /**
     * Hide splash screen
     */
    private static void hideSplashScreen() {
        if (splashWindow != null) {
            splashWindow.setVisible(false);
            splashWindow.dispose();
            logger.info("Splash screen hidden");
        }
    }
    
    /**
     * Show database connection error
     */
    private static void showDatabaseError() {
        String message = "Failed to connect to the database.\n\n" +
                        "Please ensure that:\n" +
                        "1. MySQL server is running\n" +
                        "2. Database 'library_management' exists\n" +
                        "3. Database credentials are correct\n" +
                        "4. MySQL JDBC driver is available\n\n" +
                        "Check the database configuration in Constants.java";
        
        int option = JOptionPane.showOptionDialog(
            null,
            message,
            "Database Connection Error",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.ERROR_MESSAGE,
            null,
            new String[]{"Retry", "Exit"},
            "Retry"
        );
        
        if (option == JOptionPane.YES_OPTION) {
            // Retry connection
            logger.info("Retrying database connection...");
            main(new String[0]);
        } else {
            // Exit application
            logger.info("Application terminated by user");
            System.exit(1);
        }
    }
    
    /**
     * Show fatal error dialog
     */
    private static void showFatalError(Exception e) {
        String message = "A fatal error occurred during application startup:\n\n" +
                        e.getClass().getSimpleName() + ": " + e.getMessage() + "\n\n" +
                        "Please check the application logs for more details.";
        
        JOptionPane.showMessageDialog(
            null,
            message,
            "Fatal Error",
            JOptionPane.ERROR_MESSAGE
        );
        
        logger.log(Level.SEVERE, "Fatal error - Application will exit", e);
        System.exit(1);
    }
}