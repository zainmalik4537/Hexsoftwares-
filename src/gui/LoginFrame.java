package gui;

import database.DatabaseOperations;
import models.Admin;
import utils.Constants;
import utils.ValidationUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.logging.Logger;

/**
 * Login frame for admin authentication
 * Provides secure login interface with validation
 */
public class LoginFrame extends JFrame implements ActionListener, KeyListener {
    private static final Logger logger = Logger.getLogger(LoginFrame.class.getName());
    
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton exitButton;
    private JLabel statusLabel;
    private DatabaseOperations dbOperations;
    
    public LoginFrame() {
        dbOperations = new DatabaseOperations();
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        
        logger.info("Login frame initialized");
    }
    
    /**
     * Initialize GUI components
     */
    private void initializeComponents() {
        setTitle(Constants.APP_NAME + " - Login");
        setSize(Constants.LOGIN_WINDOW_WIDTH, Constants.LOGIN_WINDOW_HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        
        // Set look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            logger.warning("Could not set system look and feel: " + e.getMessage());
        }
        
        // Create components
        usernameField = new JTextField(15);
        passwordField = new JPasswordField(15);
        loginButton = new JButton("Login");
        exitButton = new JButton("Exit");
        statusLabel = new JLabel(" ");
        
        // Set component properties
        loginButton.setPreferredSize(new Dimension(100, 30));
        exitButton.setPreferredSize(new Dimension(100, 30));
        loginButton.setBackground(new Color(33, 150, 243));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);
        
        statusLabel.setForeground(Color.RED);
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
    }
    
    /**
     * Setup layout using GridBagLayout
     */
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        // Create main panel
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        
        // Title
        JLabel titleLabel = new JLabel(Constants.APP_NAME);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(titleLabel, gbc);
        
        // Username label and field
        gbc.gridwidth = 1;
        gbc.gridy = 1;
        gbc.gridx = 0;
        gbc.anchor = GridBagConstraints.EAST;
        mainPanel.add(new JLabel("Username:"), gbc);
        
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(usernameField, gbc);
        
        // Password label and field
        gbc.gridy = 2;
        gbc.gridx = 0;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.fill = GridBagConstraints.NONE;
        mainPanel.add(new JLabel("Password:"), gbc);
        
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(passwordField, gbc);
        
        // Buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(loginButton);
        buttonPanel.add(exitButton);
        
        gbc.gridy = 3;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(buttonPanel, gbc);
        
        // Status label
        gbc.gridy = 4;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(statusLabel, gbc);
        
        add(mainPanel, BorderLayout.CENTER);
        
        // Set focus to username field
        usernameField.requestFocus();
    }
    
    /**
     * Setup event handlers
     */
    private void setupEventHandlers() {
        loginButton.addActionListener(this);
        exitButton.addActionListener(this);
        usernameField.addKeyListener(this);
        passwordField.addKeyListener(this);
        
        // Set enter key as default action
        getRootPane().setDefaultButton(loginButton);
    }
    
    /**
     * Handle button clicks
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == loginButton) {
            performLogin();
        } else if (e.getSource() == exitButton) {
            System.exit(0);
        }
    }
    
    /**
     * Perform login authentication
     */
    private void performLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        
        // Clear previous status
        statusLabel.setText(" ");
        
        // Validate input
        if (!ValidationUtils.isValidUsername(username)) {
            showError("Please enter a valid username");
            usernameField.requestFocus();
            return;
        }
        
        if (!ValidationUtils.isValidPassword(password)) {
            showError("Password must be at least " + Constants.MIN_PASSWORD_LENGTH + " characters");
            passwordField.requestFocus();
            return;
        }
        
        // Show loading state
        loginButton.setEnabled(false);
        loginButton.setText("Logging in...");
        statusLabel.setText("Authenticating...");
        statusLabel.setForeground(Color.BLUE);
        
        // Perform authentication in background thread
        SwingWorker<Admin, Void> worker = new SwingWorker<Admin, Void>() {
            @Override
            protected Admin doInBackground() throws Exception {
                return dbOperations.authenticateAdmin(username, password);
            }
            
            @Override
            protected void done() {
                try {
                    Admin admin = get();
                    if (admin != null) {
                        // Login successful
                        logger.info("Login successful for user: " + username);
                        statusLabel.setText("Login successful!");
                        statusLabel.setForeground(new Color(76, 175, 80));
                        
                        // Open main dashboard
                        SwingUtilities.invokeLater(() -> {
                            new MainDashboard(admin).setVisible(true);
                            dispose();
                        });
                    } else {
                        // Login failed
                        showError(Constants.LOGIN_FAILED_MSG);
                        passwordField.setText("");
                        passwordField.requestFocus();
                    }
                } catch (Exception e) {
                    logger.severe("Login error: " + e.getMessage());
                    showError("Login failed. Please check your database connection.");
                } finally {
                    // Reset button state
                    loginButton.setEnabled(true);
                    loginButton.setText("Login");
                }
            }
        };
        
        worker.execute();
    }
    
    /**
     * Show error message
     */
    private void showError(String message) {
        statusLabel.setText(message);
        statusLabel.setForeground(Color.RED);
    }
    
    /**
     * Handle key events for Enter key
     */
    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            performLogin();
        }
    }
    
    @Override
    public void keyTyped(KeyEvent e) {
        // Not used
    }
    
    @Override
    public void keyReleased(KeyEvent e) {
        // Not used
    }
}