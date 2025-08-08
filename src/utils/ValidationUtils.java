package utils;

import models.Book;
import models.Staff;
import java.util.regex.Pattern;

/**
 * Validation utilities class
 * Contains validation methods for user input and data integrity
 */
public class ValidationUtils {
    
    // Compiled patterns for better performance
    private static final Pattern EMAIL_PATTERN = Pattern.compile(Constants.EMAIL_REGEX);
    private static final Pattern PHONE_PATTERN = Pattern.compile(Constants.PHONE_REGEX);
    private static final Pattern ISBN_PATTERN = Pattern.compile(Constants.ISBN_REGEX);
    
    /**
     * Validates if a string is not null and not empty
     */
    public static boolean isNotEmpty(String value) {
        return value != null && !value.trim().isEmpty();
    }
    
    /**
     * Validates email format
     */
    public static boolean isValidEmail(String email) {
        if (!isNotEmpty(email)) {
            return false;
        }
        return EMAIL_PATTERN.matcher(email).matches();
    }
    
    /**
     * Validates phone number format
     */
    public static boolean isValidPhone(String phone) {
        if (!isNotEmpty(phone)) {
            return true; // Phone is optional
        }
        return PHONE_PATTERN.matcher(phone).matches() && phone.length() >= 10;
    }
    
    /**
     * Validates ISBN format
     */
    public static boolean isValidISBN(String isbn) {
        if (!isNotEmpty(isbn)) {
            return false;
        }
        
        // Remove hyphens and spaces
        String cleanISBN = isbn.replaceAll("[\\s-]", "");
        
        // Check length
        if (cleanISBN.length() < Constants.MIN_ISBN_LENGTH || 
            cleanISBN.length() > Constants.MAX_ISBN_LENGTH) {
            return false;
        }
        
        // Check if contains only digits
        return ISBN_PATTERN.matcher(cleanISBN).matches();
    }
    
    /**
     * Validates password strength
     */
    public static boolean isValidPassword(String password) {
        if (!isNotEmpty(password)) {
            return false;
        }
        
        return password.length() >= Constants.MIN_PASSWORD_LENGTH;
    }
    
    /**
     * Validates username format
     */
    public static boolean isValidUsername(String username) {
        if (!isNotEmpty(username)) {
            return false;
        }
        
        // Username should be alphanumeric and between 3-30 characters
        return username.matches("^[a-zA-Z0-9_]{3,30}$");
    }
    
    /**
     * Validates Book object
     */
    public static boolean isValidBook(Book book) {
        if (book == null) {
            return false;
        }
        
        // Check required fields
        if (!isNotEmpty(book.getTitle()) || book.getTitle().length() > Constants.MAX_TITLE_LENGTH) {
            return false;
        }
        
        if (!isNotEmpty(book.getAuthor()) || book.getAuthor().length() > Constants.MAX_NAME_LENGTH) {
            return false;
        }
        
        if (!isValidISBN(book.getIsbn())) {
            return false;
        }
        
        if (book.getQuantity() < 0) {
            return false;
        }
        
        return true;
    }
    
    /**
     * Validates Staff object
     */
    public static boolean isValidStaff(Staff staff) {
        if (staff == null) {
            return false;
        }
        
        // Check required fields
        if (!isNotEmpty(staff.getName()) || staff.getName().length() > Constants.MAX_NAME_LENGTH) {
            return false;
        }
        
        if (!isNotEmpty(staff.getRole())) {
            return false;
        }
        
        if (staff.getHireDate() == null) {
            return false;
        }
        
        // Check optional fields if provided
        if (staff.getEmail() != null && !staff.getEmail().isEmpty()) {
            if (!isValidEmail(staff.getEmail())) {
                return false;
            }
        }
        
        if (staff.getPhone() != null && !staff.getPhone().isEmpty()) {
            if (!isValidPhone(staff.getPhone())) {
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * Validates numeric input
     */
    public static boolean isValidNumber(String value) {
        if (!isNotEmpty(value)) {
            return false;
        }
        
        try {
            Integer.parseInt(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    /**
     * Validates positive integer
     */
    public static boolean isPositiveInteger(String value) {
        if (!isValidNumber(value)) {
            return false;
        }
        
        try {
            int number = Integer.parseInt(value);
            return number >= 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    /**
     * Sanitizes string input by trimming and removing extra spaces
     */
    public static String sanitizeInput(String input) {
        if (input == null) {
            return "";
        }
        
        return input.trim().replaceAll("\\s+", " ");
    }
    
    /**
     * Validates string length
     */
    public static boolean isValidLength(String value, int minLength, int maxLength) {
        if (value == null) {
            return false;
        }
        
        int length = value.trim().length();
        return length >= minLength && length <= maxLength;
    }
    
    // Private constructor to prevent instantiation
    private ValidationUtils() {
        throw new AssertionError("ValidationUtils class should not be instantiated");
    }
}