package gui;

import database.DatabaseOperations;
import models.Admin;
import utils.Constants;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Logger;
import java.awt.image.BufferedImage;




/**
 * Main dashboard window for the Library Management System
 * Contains tabbed interface for different modules
 */
public class MainDashboard extends JFrame implements ActionListener {
    private static final Logger logger = Logger.getLogger(MainDashboard.class.getName());
    
    private Admin currentAdmin;
    private DatabaseOperations dbOperations;
    private JTabbedPane tabbedPane;
    private BookPanel bookPanel;
    private StaffPanel staffPanel;
    private JLabel statusLabel;
    private JLabel userLabel;
    private JLabel timeLabel;
    private Timer timeTimer;
    
    public MainDashboard(Admin admin) {
        this.currentAdmin = admin;
        this.dbOperations = new DatabaseOperations();
        
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        startTimeUpdater();
        
        logger.info("Main dashboard initialized for user: " + admin.getUsername());
    }
    
    /**
     * Initialize GUI components
     */
    private void initializeComponents() {
        setTitle(Constants.APP_NAME + " - Dashboard");
        setSize(Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        
        // Set application icon
        setIconImage(createApplicationIcon());
        
        // Create main components
        tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        bookPanel = new BookPanel(dbOperations);
        staffPanel = new StaffPanel(dbOperations);
      

        
        // Create status bar components
        statusLabel = new JLabel("Ready");
        userLabel = new JLabel("User: " + currentAdmin.getUsername());
        timeLabel = new JLabel();
        
        // Style status bar
        statusLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        userLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        timeLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        
        userLabel.setFont(new Font("Arial", Font.BOLD, 12));
    }
    
    /**
     * Setup layout and add components
     */
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        // Create menu bar
        JMenuBar menuBar = createMenuBar();
        setJMenuBar(menuBar);
        
        // Add dashboard panel
        JPanel dashboardPanel = createDashboardPanel();
        tabbedPane.addTab("Dashboard", new ImageIcon(), dashboardPanel, "System Overview");
        
        // Add other panels
        tabbedPane.addTab("Books", new ImageIcon(), bookPanel, "Book Management");
        tabbedPane.addTab("Staff", new ImageIcon(), staffPanel, "Staff Management");
        
        // Style tabs
        tabbedPane.setFont(new Font("Arial", Font.BOLD, 14));
        
        add(tabbedPane, BorderLayout.CENTER);
        
        // Create and add status bar
        JPanel statusBar = createStatusBar();
        add(statusBar, BorderLayout.SOUTH);
    }
    
    /**
     * Create menu bar
     */
    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        
        // File menu
        JMenu fileMenu = new JMenu("File");
        fileMenu.setMnemonic('F');
        
        JMenuItem refreshItem = new JMenuItem("Refresh All");
        refreshItem.setMnemonic('R');
        refreshItem.setAccelerator(KeyStroke.getKeyStroke("F5"));
        refreshItem.addActionListener(this);
        
        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.setMnemonic('E');
        exitItem.setAccelerator(KeyStroke.getKeyStroke("alt F4"));
        exitItem.addActionListener(this);
        
        fileMenu.add(refreshItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);
        
        // View menu
        JMenu viewMenu = new JMenu("View");
        viewMenu.setMnemonic('V');
        
        JMenuItem dashboardItem = new JMenuItem("Dashboard");
        dashboardItem.setAccelerator(KeyStroke.getKeyStroke("ctrl 1"));
        dashboardItem.addActionListener(e -> tabbedPane.setSelectedIndex(0));
        
        JMenuItem booksItem = new JMenuItem("Books");
        booksItem.setAccelerator(KeyStroke.getKeyStroke("ctrl 2"));
        booksItem.addActionListener(e -> tabbedPane.setSelectedIndex(1));
        
        JMenuItem staffItem = new JMenuItem("Staff");
        staffItem.setAccelerator(KeyStroke.getKeyStroke("ctrl 3"));
        staffItem.addActionListener(e -> tabbedPane.setSelectedIndex(2));
        
        viewMenu.add(dashboardItem);
        viewMenu.add(booksItem);
        viewMenu.add(staffItem);
        
        // Help menu
        JMenu helpMenu = new JMenu("Help");
        helpMenu.setMnemonic('H');
        
        JMenuItem aboutItem = new JMenuItem("About");
        aboutItem.setMnemonic('A');
        aboutItem.addActionListener(this);
        
        helpMenu.add(aboutItem);
        
        // Add menus to menu bar
        menuBar.add(fileMenu);
        menuBar.add(viewMenu);
        menuBar.add(Box.createHorizontalGlue());
        menuBar.add(helpMenu);
        
        return menuBar;
    }
    
    /**
     * Create dashboard overview panel
     */
    private JPanel createDashboardPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        
        // Welcome message
        JLabel welcomeLabel = new JLabel("Welcome, " + currentAdmin.getUsername() + "!");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 24));
        welcomeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(welcomeLabel, gbc);
        
        // Statistics cards
        gbc.gridwidth = 1;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        
        // Books card
        JPanel booksCard = createStatsCard("Total Books", 
            String.valueOf(dbOperations.getTotalBooks()), 
            new Color(33, 150, 243));
        gbc.gridx = 0;
        panel.add(booksCard, gbc);
        
        // Staff card
        JPanel staffCard = createStatsCard("Active Staff", 
            String.valueOf(dbOperations.getTotalStaff()), 
            new Color(76, 175, 80));
        gbc.gridx = 1;
        panel.add(staffCard, gbc);
        
        // Quick actions
        JPanel actionsPanel = createQuickActionsPanel();
        gbc.gridy = 2;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.weighty = 0.5;
        panel.add(actionsPanel, gbc);
        
        return panel;
    }
    
    /**
     * Create statistics card
     */
    private JPanel createStatsCard(String title, String value, Color color) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(color, 2),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        card.setBackground(Color.WHITE);
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setForeground(color);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Arial", Font.BOLD, 36));
        valueLabel.setForeground(color);
        valueLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        
        return card;
    }
    
    /**
     * Create quick actions panel
     */
    private JPanel createQuickActionsPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(),
            "Quick Actions",
            0, 0,
            new Font("Arial", Font.BOLD, 14)
        ));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Create action buttons
        JButton addBookBtn = new JButton("Add New Book");
        JButton addStaffBtn = new JButton("Add New Staff");
        JButton viewBooksBtn = new JButton("View All Books");
        JButton viewStaffBtn = new JButton("View All Staff");
        
        // Style buttons
        Dimension buttonSize = new Dimension(150, 40);
        addBookBtn.setPreferredSize(buttonSize);
        addStaffBtn.setPreferredSize(buttonSize);
        viewBooksBtn.setPreferredSize(buttonSize);
        viewStaffBtn.setPreferredSize(buttonSize);
        
        // Add action listeners
        addBookBtn.addActionListener(e -> {
            tabbedPane.setSelectedIndex(1);
            bookPanel.showAddBookDialog();
        });
        
        addStaffBtn.addActionListener(e -> {
            tabbedPane.setSelectedIndex(2);
            staffPanel.showAddStaffDialog();
        });
        
        viewBooksBtn.addActionListener(e -> tabbedPane.setSelectedIndex(1));
        viewStaffBtn.addActionListener(e -> tabbedPane.setSelectedIndex(2));
        
        // Add buttons to panel
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(addBookBtn, gbc);
        
        gbc.gridx = 1;
        panel.add(addStaffBtn, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(viewBooksBtn, gbc);
        
        gbc.gridx = 1;
        panel.add(viewStaffBtn, gbc);
        
        return panel;
    }
    
    /**
     * Create status bar
     */
    private JPanel createStatusBar() {
        JPanel statusBar = new JPanel(new BorderLayout());
        statusBar.setBorder(BorderFactory.createRaisedBevelBorder());
        statusBar.setBackground(new Color(240, 240, 240));
        
        statusBar.add(statusLabel, BorderLayout.WEST);
        statusBar.add(userLabel, BorderLayout.CENTER);
        statusBar.add(timeLabel, BorderLayout.EAST);
        
        return statusBar;
    }
    
    /**
     * Start time updater timer
     */
    private void startTimeUpdater() {
        timeTimer = new Timer(1000, e -> {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            timeLabel.setText(sdf.format(new Date()));
        });
        timeTimer.start();
    }
    
    /**
     * Create application icon
     */
    private Image createApplicationIcon() {
        // Create a simple icon programmatically
        Image image = new BufferedImage(32, 32, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = (Graphics2D) image.getGraphics();
        g2d.setColor(new Color(33, 150, 243));
        g2d.fillRect(0, 0, 32, 32);
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 20));
        g2d.drawString("LM", 6, 22);
        g2d.dispose();
        
        return image;
    }
    
    /**
     * Setup event handlers
     */
    private void setupEventHandlers() {
        // Window closing event
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                exitApplication();
            }
        });
        
        // Tab change listener
        tabbedPane.addChangeListener(e -> {
            int selectedIndex = tabbedPane.getSelectedIndex();
            String tabName = tabbedPane.getTitleAt(selectedIndex);
            setStatus("Viewing " + tabName + " module");
        });
    }
    
    /**
     * Handle action events
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        
        switch (command) {
            case "Refresh All":
                refreshAllData();
                break;
            case "Exit":
                exitApplication();
                break;
            case "About":
                showAboutDialog();
                break;
        }
    }
    
    /**
     * Refresh all data
     */
    private void refreshAllData() {
        setStatus("Refreshing data...");
        
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                bookPanel.refreshData();
                staffPanel.refreshData();
                return null;
            }
            
            @Override
            protected void done() {
                setStatus("Data refreshed successfully");
                
                // Update dashboard statistics
                if (tabbedPane.getSelectedIndex() == 0) {
                    // Refresh dashboard if it's currently selected
                    Component dashboardComponent = tabbedPane.getComponentAt(0);
                    tabbedPane.removeTabAt(0);
                    tabbedPane.insertTab("Dashboard", null, createDashboardPanel(), "System Overview", 0);
                    tabbedPane.setSelectedIndex(0);
                }
            }
        };
        
        worker.execute();
    }
    
    /**
     * Show about dialog
     */
    private void showAboutDialog() {
        String message = Constants.APP_NAME + "\n" +
                        "Version: " + Constants.APP_VERSION + "\n" +
                        "Author: " + Constants.APP_AUTHOR + "\n\n" +
                        "A comprehensive library management system\n" +
                        "built with Java Swing and MySQL.";
        
        JOptionPane.showMessageDialog(this, message, "About " + Constants.APP_NAME, 
                                    JOptionPane.INFORMATION_MESSAGE);
    }
    
    /**
     * Exit application with confirmation
     */
    private void exitApplication() {
        int option = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to exit?",
            "Confirm Exit",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );
        
        if (option == JOptionPane.YES_OPTION) {
            // Stop timer
            if (timeTimer != null) {
                timeTimer.stop();
            }
            
            // Log logout
            logger.info("User logged out: " + currentAdmin.getUsername());
            
            // Close application
            System.exit(0);
        }
    }
    
    /**
     * Set status message
     */
    public void setStatus(String message) {
        statusLabel.setText(message);
    }
}