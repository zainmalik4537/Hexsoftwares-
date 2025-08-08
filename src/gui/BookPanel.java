package gui;

import database.DatabaseOperations;
import models.Book;
import utils.ValidationUtils;

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
 * Panel for book management operations
 * Handles viewing, adding, updating, and deleting books
 */
public class BookPanel extends JPanel implements ActionListener {
    private static final Logger logger = Logger.getLogger(BookPanel.class.getName());
    
    private DatabaseOperations dbOperations;
    private JTable bookTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JButton addButton;
    private JButton editButton;
    private JButton deleteButton;
    private JButton refreshButton;
    private JButton searchButton;
    
    // Table columns
    private final String[] columnNames = {
        "ID", "Title", "Author", "ISBN", "Quantity", "Status", "Date Added"
    };
    
    public BookPanel(DatabaseOperations dbOperations) {
        this.dbOperations = dbOperations;
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        loadBooks();
        
        logger.info("Book panel initialized");
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
        
        bookTable = new JTable(tableModel);
        bookTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        bookTable.setRowHeight(25);
        bookTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        
        // Enable sorting
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
        bookTable.setRowSorter(sorter);
        
        // Create search components
        searchField = new JTextField(20);
        searchButton = new JButton("Search");
        
        // Create action buttons
        addButton = new JButton("Add Book");
        editButton = new JButton("Edit Book");
        deleteButton = new JButton("Delete Book");
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
        // Create top panel with search
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(new JLabel("Search:"));
        topPanel.add(searchField);
        topPanel.add(searchButton);
        
        // Create button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(refreshButton);
        
        // Combine top panels
        JPanel controlPanel = new JPanel(new BorderLayout());
        controlPanel.add(topPanel, BorderLayout.WEST);
        controlPanel.add(buttonPanel, BorderLayout.EAST);
        controlPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Create table scroll pane
        JScrollPane scrollPane = new JScrollPane(bookTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(),
            "Books Inventory",
            0, 0,
            new Font("Arial", Font.BOLD, 14)
        ));
        
        // Add components to main panel
        add(controlPanel, BorderLayout.NORTH);
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
        searchButton.addActionListener(this);
        
        // Search field listener
        searchField.addActionListener(this);
        
        // Table selection listener
        bookTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                boolean hasSelection = bookTable.getSelectedRow() >= 0;
                editButton.setEnabled(hasSelection);
                deleteButton.setEnabled(hasSelection);
            }
        });
        
        // Double-click to edit
        bookTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && bookTable.getSelectedRow() >= 0) {
                    editSelectedBook();
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
            showAddBookDialog();
        } else if (source == editButton) {
            editSelectedBook();
        } else if (source == deleteButton) {
            deleteSelectedBook();
        } else if (source == refreshButton) {
            refreshData();
        } else if (source == searchButton || source == searchField) {
            performSearch();
        }
    }
    
    /**
     * Show add book dialog
     */
    public void showAddBookDialog() {
        BookDialog dialog = new BookDialog((Frame) SwingUtilities.getWindowAncestor(this), 
                                         "Add New Book", true, null);
        dialog.setVisible(true);
        
        if (dialog.isConfirmed()) {
            Book book = dialog.getBook();
            if (dbOperations.addBook(book)) {
                JOptionPane.showMessageDialog(this, "Book added successfully!", 
                                            "Success", JOptionPane.INFORMATION_MESSAGE);
                loadBooks();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to add book. Please check if ISBN already exists.", 
                                            "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    /**
     * Edit selected book
     */
    private void editSelectedBook() {
        int selectedRow = bookTable.getSelectedRow();
        if (selectedRow >= 0) {
            // Convert view row to model row
            selectedRow = bookTable.convertRowIndexToModel(selectedRow);
            
            // Get book data from table
            int bookId = (Integer) tableModel.getValueAt(selectedRow, 0);
            String title = (String) tableModel.getValueAt(selectedRow, 1);
            String author = (String) tableModel.getValueAt(selectedRow, 2);
            String isbn = (String) tableModel.getValueAt(selectedRow, 3);
            int quantity = (Integer) tableModel.getValueAt(selectedRow, 4);
            
            Book book = new Book(bookId, title, author, isbn, quantity, null, null);
            
            BookDialog dialog = new BookDialog((Frame) SwingUtilities.getWindowAncestor(this), 
                                             "Edit Book", true, book);
            dialog.setVisible(true);
            
            if (dialog.isConfirmed()) {
                Book updatedBook = dialog.getBook();
                updatedBook.setBookId(bookId);
                
                if (dbOperations.updateBook(updatedBook)) {
                    JOptionPane.showMessageDialog(this, "Book updated successfully!", 
                                                "Success", JOptionPane.INFORMATION_MESSAGE);
                    loadBooks();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to update book.", 
                                                "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }
    
    /**
     * Delete selected book
     */
    private void deleteSelectedBook() {
        int selectedRow = bookTable.getSelectedRow();
        if (selectedRow >= 0) {
            // Convert view row to model row
            selectedRow = bookTable.convertRowIndexToModel(selectedRow);
            
            int bookId = (Integer) tableModel.getValueAt(selectedRow, 0);
            String title = (String) tableModel.getValueAt(selectedRow, 1);
            
            int option = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete the book:\n\"" + title + "\"?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
            );
            
            if (option == JOptionPane.YES_OPTION) {
                if (dbOperations.deleteBook(bookId)) {
                    JOptionPane.showMessageDialog(this, "Book deleted successfully!", 
                                                "Success", JOptionPane.INFORMATION_MESSAGE);
                    loadBooks();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to delete book.", 
                                                "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }
    
    /**
     * Perform search
     */
    private void performSearch() {
        String searchTerm = searchField.getText().trim();
        
        if (searchTerm.isEmpty()) {
            loadBooks();
        } else {
            List<Book> books = dbOperations.searchBooks(searchTerm);
            populateTable(books);
        }
    }
    
    /**
     * Load books from database
     */
    private void loadBooks() {
        SwingWorker<List<Book>, Void> worker = new SwingWorker<List<Book>, Void>() {
            @Override
            protected List<Book> doInBackground() throws Exception {
                return dbOperations.getAllBooks();
            }
            
            @Override
            protected void done() {
                try {
                    List<Book> books = get();
                    populateTable(books);
                } catch (Exception e) {
                    logger.severe("Error loading books: " + e.getMessage());
                    JOptionPane.showMessageDialog(BookPanel.this, 
                                                "Error loading books: " + e.getMessage(),
                                                "Database Error", 
                                                JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        
        worker.execute();
    }
    
    /**
     * Populate table with book data
     */
    private void populateTable(List<Book> books) {
        // Clear existing data
        tableModel.setRowCount(0);
        
        // Add book data
        for (Book book : books) {
            Object[] row = {
                book.getBookId(),
                book.getTitle(),
                book.getAuthor(),
                book.getIsbn(),
                book.getQuantity(),
                book.getStatus(),
                book.getDateAdded()
            };
            tableModel.addRow(row);
        }
        
        // Clear selection
        bookTable.clearSelection();
    }
    
    /**
     * Refresh data
     */
    public void refreshData() {
        loadBooks();
        searchField.setText("");
    }
}