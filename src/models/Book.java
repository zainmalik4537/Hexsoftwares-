package models;

import java.sql.Timestamp;

/**
 * Book model class representing library books
 * Contains all book-related data and validation methods
 */
public class Book {
    private int bookId;
    private String title;
    private String author;
    private String isbn;
    private int quantity;
    private Timestamp dateAdded;
    private String status;
    
    // Constructors
    public Book() {}
    
    public Book(String title, String author, String isbn, int quantity) {
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.quantity = quantity;
    }
    
    public Book(int bookId, String title, String author, String isbn, int quantity, 
                Timestamp dateAdded, String status) {
        this.bookId = bookId;
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.quantity = quantity;
        this.dateAdded = dateAdded;
        this.status = status;
    }
    
    // Getters and Setters
    public int getBookId() { return bookId; }
    public void setBookId(int bookId) { this.bookId = bookId; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }
    
    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }
    
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    
    public Timestamp getDateAdded() { return dateAdded; }
    public void setDateAdded(Timestamp dateAdded) { this.dateAdded = dateAdded; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    /**
     * Validates ISBN format (basic validation)
     */
    public boolean isValidISBN() {
        return isbn != null && isbn.length() >= 10 && isbn.length() <= 13;
    }
    
    /**
     * Checks if book is available
     */
    public boolean isAvailable() {
        return quantity > 0 && "AVAILABLE".equals(status);
    }
    
    @Override
    public String toString() {
        return "Book{" +
                "bookId=" + bookId +
                ", title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", isbn='" + isbn + '\'' +
                ", quantity=" + quantity +
                ", status='" + status + '\'' +
                '}';
    }
}