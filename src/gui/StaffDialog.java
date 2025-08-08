package gui;

import models.Staff;
import utils.ValidationUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Date;
import java.time.LocalDate;

/**
 * Dialog for adding/editing staff members
 * Provides form interface with validation
 */
public class StaffDialog extends JDialog implements ActionListener {
    
    private JTextField nameField;
    private JTextField roleField;
    private JSpinner hireDateSpinner;
    private JTextField emailField;
    private JTextField phoneField;
    private JButton saveButton;
    private JButton cancelButton;
    
    private Staff staff;
    private boolean confirmed = false;
    
    public StaffDialog(Frame parent, String title, boolean modal, Staff staff) {
        super(parent, title, modal);
        this.staff = staff;
        
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        
        if (staff != null) {
            populateFields();
        }
        
        setSize(400, 350);
        setLocationRelativeTo(parent);
    }
    
    /**
     * Initialize components
     */
    private void initializeComponents() {
        nameField = new JTextField(20);
        roleField = new JTextField(20);
        
        // Date spinner
        SpinnerDateModel dateModel = new SpinnerDateModel();
        hireDateSpinner = new JSpinner(dateModel);
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(hireDateSpinner, "yyyy-MM-dd");
        hireDateSpinner.setEditor(dateEditor);
        hireDateSpinner.setValue(new java.util.Date());
        
        emailField = new JTextField(20);
        phoneField = new JTextField(20);
        
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
        
        // Name
        gbc.gridx = 0;
        gbc.gridy = 0;
        mainPanel.add(new JLabel("Name:*"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(nameField, gbc);
        
        // Role
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.NONE;
        mainPanel.add(new JLabel("Role:*"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(roleField, gbc);
        
        // Hire Date
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.NONE;
        mainPanel.add(new JLabel("Hire Date:*"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(hireDateSpinner, gbc);
        
        // Email
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.fill = GridBagConstraints.NONE;
        mainPanel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(emailField, gbc);
        
        // Phone
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.fill = GridBagConstraints.NONE;
        mainPanel.add(new JLabel("Phone:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(phoneField, gbc);
        
        // Required fields note
        JLabel noteLabel = new JLabel("* Required fields");
        noteLabel.setFont(new Font("Arial", Font.ITALIC, 10));
        noteLabel.setForeground(Color.GRAY);
        gbc.gridx = 0;
        gbc.gridy = 5;
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
     * Populate fields with existing staff data
     */
    private void populateFields() {
        if (staff != null) {
            nameField.setText(staff.getName());
            roleField.setText(staff.getRole());
            if (staff.getHireDate() != null) {
                hireDateSpinner.setValue(staff.getHireDate());
            }
            emailField.setText(staff.getEmail() != null ? staff.getEmail() : "");
            phoneField.setText(staff.getPhone() != null ? staff.getPhone() : "");
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
     * Validate input and create staff object
     */
    private boolean validateAndSave() {
        // Get input values
        String name = ValidationUtils.sanitizeInput(nameField.getText());
        String role = ValidationUtils.sanitizeInput(roleField.getText());
        java.util.Date utilDate = (java.util.Date) hireDateSpinner.getValue();
        Date hireDate = new Date(utilDate.getTime());
        String email = ValidationUtils.sanitizeInput(emailField.getText());
        String phone = ValidationUtils.sanitizeInput(phoneField.getText());
        
        // Validate required fields
        if (!ValidationUtils.isNotEmpty(name)) {
            showError("Name is required.");
            nameField.requestFocus();
            return false;
        }
        
        if (!ValidationUtils.isNotEmpty(role)) {
            showError("Role is required.");
            roleField.requestFocus();
            return false;
        }
        
        if (hireDate == null) {
            showError("Hire date is required.");
            hireDateSpinner.requestFocus();
            return false;
        }
        
        // Validate hire date is not in the future
        if (hireDate.after(Date.valueOf(LocalDate.now()))) {
            showError("Hire date cannot be in the future.");
            hireDateSpinner.requestFocus();
            return false;
        }
        
        // Validate optional fields
        if (!email.isEmpty() && !ValidationUtils.isValidEmail(email)) {
            showError("Please enter a valid email address.");
            emailField.requestFocus();
            return false;
        }
        
        if (!phone.isEmpty() && !ValidationUtils.isValidPhone(phone)) {
            showError("Please enter a valid phone number.");
            phoneField.requestFocus();
            return false;
        }
        
        // Create staff object
        if (staff == null) {
            staff = new Staff();
        }
        
        staff.setName(name);
        staff.setRole(role);
        staff.setHireDate(hireDate);
        staff.setEmail(email.isEmpty() ? null : email);
        staff.setPhone(phone.isEmpty() ? null : phone);
        
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
     * Get the staff object
     */
    public Staff getStaff() {
        return staff;
    }
    
    /**
     * Check if dialog was confirmed
     */
    public boolean isConfirmed() {
        return confirmed;
    }
}