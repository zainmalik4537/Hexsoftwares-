package database;

import models.Admin;
import models.Book;
import models.Staff;
import utils.ValidationUtils;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * Database operations class
 * Handles all CRUD operations for the library management system
 */
public class DatabaseOperations {
    private static final Logger logger = Logger.getLogger(DatabaseOperations.class.getName());
    private final DatabaseConnection dbConnection;
    
    public DatabaseOperations() {
        this.dbConnection = DatabaseConnection.getInstance();
    }
    
    // ==================== ADMIN OPERATIONS ====================
    
    /**
     * Authenticate admin user
     */
    public Admin authenticateAdmin(String username, String password) {
        String sql = "SELECT * FROM admin_table WHERE username = ? AND password_hash = SHA2(?, 256) AND status = 'ACTIVE'";
        
        try (PreparedStatement pstmt = dbConnection.getConnection().prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Admin admin = new Admin(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("password_hash"),
                        rs.getTimestamp("created_date"),
                        rs.getString("status")
                    );
                    
                    // Update last login
                    updateLastLogin(admin.getId());
                    logger.info("Admin authenticated successfully: " + username);
                    return admin;
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error authenticating admin: " + username, e);
        }
        
        logger.warning("Authentication failed for user: " + username);
        return null;
    }
    
    /**
     * Update admin's last login timestamp
     */
    private void updateLastLogin(int adminId) {
        String sql = "UPDATE admin_table SET last_login = CURRENT_TIMESTAMP WHERE id = ?";
        
        try (PreparedStatement pstmt = dbConnection.getConnection().prepareStatement(sql)) {
            pstmt.setInt(1, adminId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            logger.log(Level.WARNING, "Failed to update last login for admin ID: " + adminId, e);
        }
    }
    
    // ==================== BOOK OPERATIONS ====================
    
    /**
     * Get all books from database
     */
    public List<Book> getAllBooks() {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT * FROM books_table ORDER BY title";
        
        try (PreparedStatement pstmt = dbConnection.getConnection().prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                Book book = new Book(
                    rs.getInt("book_id"),
                    rs.getString("title"),
                    rs.getString("author"),
                    rs.getString("isbn"),
                    rs.getInt("quantity"),
                    rs.getTimestamp("date_added"),
                    rs.getString("status")
                );
                books.add(book);
            }
            logger.info("Retrieved " + books.size() + " books from database");
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error retrieving books from database", e);
        }
        
        return books;
    }
    
    /**
     * Add new book to database
     */
    public boolean addBook(Book book) {
        if (!ValidationUtils.isValidBook(book)) {
            logger.warning("Invalid book data provided");
            return false;
        }
        
        String sql = "INSERT INTO books_table (title, author, isbn, quantity, status) VALUES (?, ?, ?, ?, ?)";
        
        try (PreparedStatement pstmt = dbConnection.getConnection().prepareStatement(sql)) {
            pstmt.setString(1, book.getTitle());
            pstmt.setString(2, book.getAuthor());
            pstmt.setString(3, book.getIsbn());
            pstmt.setInt(4, book.getQuantity());
            pstmt.setString(5, book.getQuantity() > 0 ? "AVAILABLE" : "OUT_OF_STOCK");
            
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                logger.info("Book added successfully: " + book.getTitle());
                return true;
            }
        } catch (SQLIntegrityConstraintViolationException e) {
            logger.warning("Book with ISBN " + book.getIsbn() + " already exists");
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error adding book: " + book.getTitle(), e);
        }
        
        return false;
    }
    
    /**
     * Update existing book
     */
    public boolean updateBook(Book book) {
        if (!ValidationUtils.isValidBook(book)) {
            logger.warning("Invalid book data provided for update");
            return false;
        }
        
        String sql = "UPDATE books_table SET title = ?, author = ?, isbn = ?, quantity = ?, status = ? WHERE book_id = ?";
        
        try (PreparedStatement pstmt = dbConnection.getConnection().prepareStatement(sql)) {
            pstmt.setString(1, book.getTitle());
            pstmt.setString(2, book.getAuthor());
            pstmt.setString(3, book.getIsbn());
            pstmt.setInt(4, book.getQuantity());
            pstmt.setString(5, book.getQuantity() > 0 ? "AVAILABLE" : "OUT_OF_STOCK");
            pstmt.setInt(6, book.getBookId());
            
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                logger.info("Book updated successfully: " + book.getTitle());
                return true;
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error updating book: " + book.getTitle(), e);
        }
        
        return false;
    }
    
    /**
     * Delete book from database
     */
    public boolean deleteBook(int bookId) {
        String sql = "DELETE FROM books_table WHERE book_id = ?";
        
        try (PreparedStatement pstmt = dbConnection.getConnection().prepareStatement(sql)) {
            pstmt.setInt(1, bookId);
            
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                logger.info("Book deleted successfully with ID: " + bookId);
                return true;
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error deleting book with ID: " + bookId, e);
        }
        
        return false;
    }
    
    /**
     * Search books by title or author
     */
    public List<Book> searchBooks(String searchTerm) {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT * FROM books_table WHERE title LIKE ? OR author LIKE ? ORDER BY title";
        
        try (PreparedStatement pstmt = dbConnection.getConnection().prepareStatement(sql)) {
            String searchPattern = "%" + searchTerm + "%";
            pstmt.setString(1, searchPattern);
            pstmt.setString(2, searchPattern);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Book book = new Book(
                        rs.getInt("book_id"),
                        rs.getString("title"),
                        rs.getString("author"),
                        rs.getString("isbn"),
                        rs.getInt("quantity"),
                        rs.getTimestamp("date_added"),
                        rs.getString("status")
                    );
                    books.add(book);
                }
            }
            logger.info("Found " + books.size() + " books matching search term: " + searchTerm);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error searching books", e);
        }
        
        return books;
    }
    
    // ==================== STAFF OPERATIONS ====================
    
    /**
     * Get all staff members
     */
    public List<Staff> getAllStaff() {
        List<Staff> staffList = new ArrayList<>();
        String sql = "SELECT * FROM staff_table ORDER BY name";
        
        try (PreparedStatement pstmt = dbConnection.getConnection().prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                Staff staff = new Staff(
                    rs.getInt("staff_id"),
                    rs.getString("name"),
                    rs.getString("role"),
                    rs.getDate("hire_date"),
                    rs.getString("status"),
                    rs.getString("email"),
                    rs.getString("phone")
                );
                staffList.add(staff);
            }
            logger.info("Retrieved " + staffList.size() + " staff members from database");
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error retrieving staff from database", e);
        }
        
        return staffList;
    }
    
    /**
     * Add new staff member
     */
    public boolean addStaff(Staff staff) {
        if (!ValidationUtils.isValidStaff(staff)) {
            logger.warning("Invalid staff data provided");
            return false;
        }
        
        String sql = "INSERT INTO staff_table (name, role, hire_date, status, email, phone) VALUES (?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement pstmt = dbConnection.getConnection().prepareStatement(sql)) {
            pstmt.setString(1, staff.getName());
            pstmt.setString(2, staff.getRole());
            pstmt.setDate(3, staff.getHireDate());
            pstmt.setString(4, "ACTIVE");
            pstmt.setString(5, staff.getEmail());
            pstmt.setString(6, staff.getPhone());
            
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                logger.info("Staff member added successfully: " + staff.getName());
                return true;
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error adding staff member: " + staff.getName(), e);
        }
        
        return false;
    }
    
    /**
     * Update existing staff member
     */
    public boolean updateStaff(Staff staff) {
        if (!ValidationUtils.isValidStaff(staff)) {
            logger.warning("Invalid staff data provided for update");
            return false;
        }
        
        String sql = "UPDATE staff_table SET name = ?, role = ?, hire_date = ?, email = ?, phone = ? WHERE staff_id = ?";
        
        try (PreparedStatement pstmt = dbConnection.getConnection().prepareStatement(sql)) {
            pstmt.setString(1, staff.getName());
            pstmt.setString(2, staff.getRole());
            pstmt.setDate(3, staff.getHireDate());
            pstmt.setString(4, staff.getEmail());
            pstmt.setString(5, staff.getPhone());
            pstmt.setInt(6, staff.getStaffId());
            
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                logger.info("Staff member updated successfully: " + staff.getName());
                return true;
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error updating staff member: " + staff.getName(), e);
        }
        
        return false;
    }
    
    /**
     * Delete staff member
     */
    public boolean deleteStaff(int staffId) {
        String sql = "DELETE FROM staff_table WHERE staff_id = ?";
        
        try (PreparedStatement pstmt = dbConnection.getConnection().prepareStatement(sql)) {
            pstmt.setInt(1, staffId);
            
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                logger.info("Staff member deleted successfully with ID: " + staffId);
                return true;
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error deleting staff member with ID: " + staffId, e);
        }
        
        return false;
    }
    
    // ==================== UTILITY OPERATIONS ====================
    
    /**
     * Get total count of books
     */
    public int getTotalBooks() {
        String sql = "SELECT COUNT(*) as total FROM books_table";
        
        try (PreparedStatement pstmt = dbConnection.getConnection().prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error getting total book count", e);
        }
        
        return 0;
    }
    
    /**
     * Get total count of staff
     */
    public int getTotalStaff() {
        String sql = "SELECT COUNT(*) as total FROM staff_table WHERE status = 'ACTIVE'";
        
        try (PreparedStatement pstmt = dbConnection.getConnection().prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error getting total staff count", e);
        }
        
        return 0;
    }
    

}
