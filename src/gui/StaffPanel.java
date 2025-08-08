package gui;

import database.DatabaseOperations;
import models.Staff;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.logging.Logger;

/**
 * Panel for staff management operations
 * Handles viewing, adding, updating, and deleting staff members
 */
public class StaffPanel extends JPanel implements ActionListener {
    private static final Logger logger = Logger.getLogger(StaffPanel.class.getName());
    
    private DatabaseOperations dbOperations;
    private JTable staffTable;
    private DefaultTableModel tableModel;
    private JButton addButton;
    private JButton editButton;
    private JButton deleteButton;
    private JButton refreshButton;
    
    // Table columns
    private final String[] columnNames = {
        "ID", "Name", "Role", "Hire Date", "Status", "Email", "Phone"
    };
    
    public StaffPanel(DatabaseOperations dbOperations) {
        this.dbOperations = dbOperations;
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        loadStaff();
        
        logger.info("Staff panel initialized");
    }
    
    /**
     * Initialize GUI components
     */
    private void initializeComponents() {
        setLayout(new BorderLayout());
        
        // Create table model and table
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table read-only
            }
        };
        
        staffTable = new JTable(tableModel);
        staffTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        staffTable.setRowHeight(25);
        staffTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        
        // Enable sorting
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
        staffTable.setRowSorter(sorter);
        
        // Create action buttons
        addButton = new JButton("Add Staff");
        editButton = new JButton("Edit Staff");
        deleteButton = new JButton("Delete Staff");
        refreshButton = new JButton("Refresh");
        
        // Style buttons
        Dimension buttonSize = new Dimension(120, 30);
        addButton.setPreferredSize(buttonSize);
        editButton.setPreferredSize(buttonSize);
        deleteButton.setPreferredSize(buttonSize);
        refreshButton.setPreferredSize(buttonSize);
        
        addButton.setBackground(new Color(76, 175, 80));
        addButton.setForeground(Color.WHITE);
        editButton.setBackground(new Color(255, 152, 0));
        editButton.setForeground(Color.WHITE);
        deleteButton.setBackground(new Color(244, 67, 54));
        deleteButton.setForeground(Color.WHITE);
        
        // Initially disable edit and delete buttons
        editButton.setEnabled(false);
        deleteButton.setEnabled(false);
    }
    
    /**
     * Setup panel layout
     */
    private void setupLayout() {
        // Create button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(refreshButton);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Create table scroll pane
        JScrollPane scrollPane = new JScrollPane(staffTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(),
            "Staff Members",
            0, 0,
            new Font("Arial", Font.BOLD, 14)
        ));
        
        // Add components to main panel
        add(buttonPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }
    
    /**
     * Setup event handlers
     */
    private void setupEventHandlers() {
        // Button listeners
        addButton.addActionListener(this);
        editButton.addActionListener(this);
        deleteButton.addActionListener(this);
        refreshButton.addActionListener(this);
        
        // Table selection listener
        staffTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                boolean hasSelection = staffTable.getSelectedRow() >= 0;
                editButton.setEnabled(hasSelection);
                deleteButton.setEnabled(hasSelection);
            }
        });
        
        // Double-click to edit
        staffTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && staffTable.getSelectedRow() >= 0) {
                    editSelectedStaff();
                }
            }
        });
    }
    
    /**
     * Handle button actions
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        
        if (source == addButton) {
            showAddStaffDialog();
        } else if (source == editButton) {
            editSelectedStaff();
        } else if (source == deleteButton) {
            deleteSelectedStaff();
        } else if (source == refreshButton) {
            refreshData();
        }
    }
    
    /**
     * Show add staff dialog
     */
    public void showAddStaffDialog() {
        StaffDialog dialog = new StaffDialog((Frame) SwingUtilities.getWindowAncestor(this), 
                                           "Add New Staff Member", true, null);
        dialog.setVisible(true);
        
        if (dialog.isConfirmed()) {
            Staff staff = dialog.getStaff();
            if (dbOperations.addStaff(staff)) {
                JOptionPane.showMessageDialog(this, "Staff member added successfully!", 
                                            "Success", JOptionPane.INFORMATION_MESSAGE);
                loadStaff();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to add staff member.", 
                                            "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    /**
     * Edit selected staff member
     */
    private void editSelectedStaff() {
        int selectedRow = staffTable.getSelectedRow();
        if (selectedRow >= 0) {
            // Convert view row to model row
            selectedRow = staffTable.convertRowIndexToModel(selectedRow);
            
            // Get staff data from table
            int staffId = (Integer) tableModel.getValueAt(selectedRow, 0);
            String name = (String) tableModel.getValueAt(selectedRow, 1);
            String role = (String) tableModel.getValueAt(selectedRow, 2);
            java.sql.Date hireDate = (java.sql.Date) tableModel.getValueAt(selectedRow, 3);
            String status = (String) tableModel.getValueAt(selectedRow, 4);
            String email = (String) tableModel.getValueAt(selectedRow, 5);
            String phone = (String) tableModel.getValueAt(selectedRow, 6);
            
            Staff staff = new Staff(staffId, name, role, hireDate, status, email, phone);
            
            StaffDialog dialog = new StaffDialog((Frame) SwingUtilities.getWindowAncestor(this), 
                                               "Edit Staff Member", true, staff);
            dialog.setVisible(true);
            
            if (dialog.isConfirmed()) {
                Staff updatedStaff = dialog.getStaff();
                updatedStaff.setStaffId(staffId);
                
                if (dbOperations.updateStaff(updatedStaff)) {
                    JOptionPane.showMessageDialog(this, "Staff member updated successfully!", 
                                                "Success", JOptionPane.INFORMATION_MESSAGE);
                    loadStaff();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to update staff member.", 
                                                "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }
    
    /**
     * Delete selected staff member
     */
    private void deleteSelectedStaff() {
        int selectedRow = staffTable.getSelectedRow();
        if (selectedRow >= 0) {
            // Convert view row to model row
            selectedRow = staffTable.convertRowIndexToModel(selectedRow);
            
            int staffId = (Integer) tableModel.getValueAt(selectedRow, 0);
            String name = (String) tableModel.getValueAt(selectedRow, 1);
            
            int option = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete staff member:\n\"" + name + "\"?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
            );
            
            if (option == JOptionPane.YES_OPTION) {
                if (dbOperations.deleteStaff(staffId)) {
                    JOptionPane.showMessageDialog(this, "Staff member deleted successfully!", 
                                                "Success", JOptionPane.INFORMATION_MESSAGE);
                    loadStaff();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to delete staff member.", 
                                                "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }
    
    /**
     * Load staff from database
     */
    private void loadStaff() {
        SwingWorker<List<Staff>, Void> worker = new SwingWorker<List<Staff>, Void>() {
            @Override
            protected List<Staff> doInBackground() throws Exception {
                return dbOperations.getAllStaff();
            }
            
            @Override
            protected void done() {
                try {
                    List<Staff> staffList = get();
                    populateTable(staffList);
                } catch (Exception e) {
                    logger.severe("Error loading staff: " + e.getMessage());
                    JOptionPane.showMessageDialog(StaffPanel.this, 
                                                "Error loading staff: " + e.getMessage(),
                                                "Database Error", 
                                                JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        
        worker.execute();
    }
    
    /**
     * Populate table with staff data
     */
    private void populateTable(List<Staff> staffList) {
        // Clear existing data
        tableModel.setRowCount(0);
        
        // Add staff data
        for (Staff staff : staffList) {
            Object[] row = {
                staff.getStaffId(),
                staff.getName(),
                staff.getRole(),
                staff.getHireDate(),
                staff.getStatus(),
                staff.getEmail(),
                staff.getPhone()
            };
            tableModel.addRow(row);
        }
        
        // Clear selection
        staffTable.clearSelection();
    }
    
    /**
     * Refresh data
     */
    public void refreshData() {
        loadStaff();
    }
}