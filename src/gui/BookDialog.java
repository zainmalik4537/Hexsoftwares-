package gui;

import models.Book;
import utils.ValidationUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Dialog for adding/editing books
 * Provides form interface with validation
 */
public class BookDialog extends JDialog implements ActionListener {
    
    private JTextField titleField;
    private JTextField authorField;
    private JTextField isbnField;
    private JSpinner quantitySpinner;
    private JButton saveButton;
    private JButton cancelButton;
    
    private Book book;
    private boolean confirmed = false;
    
    public BookDialog(Frame parent, String title, boolean modal, Book book) {
        super(parent, title, modal);
        this.book = book;
        
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        
        if (book != null) {
            populateFields();
        }
        
        setSize(400, 300);
        setLocationRelativeTo(parent);
    }
    
    /**
     * Initialize components
     */
    private void initializeComponents() {
        titleField = new JTextField(20);
        authorField = new JTextField(20);
        isbnField = new JTextField(20);
        quantitySpinner = new JSpinner(new SpinnerNumberModel(1, 0, 9999, 1));
        
        saveButton = new JButton("Save");
        cancelButton = new JButton("Cancel");
        
        // Style buttons
        saveButton.setPreferredSize(new Dimension(80, 30));
        cancelButton.setPreferredSize(new Dimension(80, 30));
        
        saveButton.setBackground(new Color(76, 175, 80));
        saveButton.setForeground(Color.WHITE);
    }
    
    /**
     * Setup layout
     */
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Title
        gbc.gridx = 0;
        gbc.gridy = 0;
        mainPanel.add(new JLabel("Title:*"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(titleField, gbc);
        
        // Author
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.NONE;
        mainPanel.add(new JLabel("Author:*"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(authorField, gbc);
        
        // ISBN
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.NONE;
        mainPanel.add(new JLabel("ISBN:*"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(isbnField, gbc);
        
        // Quantity
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.fill = GridBagConstraints.NONE;
        mainPanel.add(new JLabel("Quantity:*"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(quantitySpinner, gbc);
        
        // Required fields note
        JLabel noteLabel = new JLabel("* Required fields");
        noteLabel.setFont(new Font("Arial", Font.ITALIC, 10));
        noteLabel.setForeground(Color.GRAY);
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        mainPanel.add(noteLabel, gbc);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        
        add(mainPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    /**
     * Setup event handlers
     */
    private void setupEventHandlers() {
        saveButton.addActionListener(this);
        cancelButton.addActionListener(this);
        
        // Set default button
        getRootPane().setDefaultButton(saveButton);
    }
    
    /**
     * Populate fields with existing book data
     */
    private void populateFields() {
        if (book != null) {
            titleField.setText(book.getTitle());
            authorField.setText(book.getAuthor());
            isbnField.setText(book.getIsbn());
            quantitySpinner.setValue(book.getQuantity());
        }
    }
    
    /**
     * Handle button actions
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == saveButton) {
            if (validateAndSave()) {
                confirmed = true;
                dispose();
            }
        } else if (e.getSource() == cancelButton) {
            dispose();
        }
    }
    
    /**
     * Validate input and create book object
     */
    private boolean validateAndSave() {
        // Get input values
        String title = ValidationUtils.sanitizeInput(titleField.getText());
        String author = ValidationUtils.sanitizeInput(authorField.getText());
        String isbn = ValidationUtils.sanitizeInput(isbnField.getText());
        int quantity = (Integer) quantitySpinner.getValue();
        
        // Validate fields
        if (!ValidationUtils.isNotEmpty(title)) {
            showError("Title is required.");
            titleField.requestFocus();
            return false;
        }
        
        if (!ValidationUtils.isNotEmpty(author)) {
            showError("Author is required.");
            authorField.requestFocus();
            return false;
        }
        
        if (!ValidationUtils.isValidISBN(isbn)) {
            showError("Please enter a valid ISBN (10-13 digits).");
            isbnField.requestFocus();
            return false;
        }
        
        if (quantity < 0) {
            showError("Quantity cannot be negative.");
            quantitySpinner.requestFocus();
            return false;
        }
        
        // Create book object
        if (book == null) {
            book = new Book();
        }
        
        book.setTitle(title);
        book.setAuthor(author);
        book.setIsbn(isbn.replaceAll("[\\s-]", "")); // Remove spaces and hyphens
        book.setQuantity(quantity);
        
        return true;
    }
    
    /**
     * Show error message
     */
    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Validation Error", 
                                    JOptionPane.ERROR_MESSAGE);
    }
    
    /**
     * Get the book object
     */
    public Book getBook() {
        return book;
    }
    
    /**
     * Check if dialog was confirmed
     */
    public boolean isConfirmed() {
        return confirmed;
    }
}