-- Library Management System Database Setup
-- MySQL Database Schema with Sample Data

-- Create database
CREATE DATABASE IF NOT EXISTS library_management;
USE library_management;

-- Drop existing tables if they exist
DROP TABLE IF EXISTS admin_table;
DROP TABLE IF EXISTS books_table;
DROP TABLE IF EXISTS staff_table;

-- Create admin_table
CREATE TABLE admin_table (
    id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_login TIMESTAMP NULL,
    status ENUM('ACTIVE', 'INACTIVE') DEFAULT 'ACTIVE'
);

-- Create books_table
CREATE TABLE books_table (
    book_id INT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(255) NOT NULL,
    author VARCHAR(255) NOT NULL,
    isbn VARCHAR(13) UNIQUE NOT NULL,
    quantity INT DEFAULT 1,
    date_added TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status ENUM('AVAILABLE', 'OUT_OF_STOCK') DEFAULT 'AVAILABLE'
);

-- Create staff_table
CREATE TABLE staff_table (
    staff_id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    role VARCHAR(50) NOT NULL,
    hire_date DATE NOT NULL,
    status ENUM('ACTIVE', 'INACTIVE') DEFAULT 'ACTIVE',
    email VARCHAR(100),
    phone VARCHAR(15)
);

-- Insert sample admin user (password: admin123)
INSERT INTO admin_table (username, password_hash) VALUES 
('admin', SHA2('admin123', 256));

-- Insert sample books
INSERT INTO books_table (title, author, isbn, quantity) VALUES
('The Great Gatsby', 'F. Scott Fitzgerald', '9780743273565', 5),
('To Kill a Mockingbird', 'Harper Lee', '9780446310789', 3),
('1984', 'George Orwell', '9780451524935', 4),
('Pride and Prejudice', 'Jane Austen', '9780141439518', 2),
('The Catcher in the Rye', 'J.D. Salinger', '9780316769174', 6),
('Lord of the Flies', 'William Golding', '9780571056866', 3),
('Animal Farm', 'George Orwell', '9780451526342', 4),
('Brave New World', 'Aldous Huxley', '9780060850524', 2),
('The Hobbit', 'J.R.R. Tolkien', '9780547928227', 5),
('Fahrenheit 451', 'Ray Bradbury', '9781451673319', 3);

-- Insert sample staff
INSERT INTO staff_table (name, role, hire_date, email, phone) VALUES
('John Smith', 'Librarian', '2023-01-15', 'john.smith@library.com', '555-0101'),
('Sarah Johnson', 'Assistant Librarian', '2023-03-20', 'sarah.johnson@library.com', '555-0102'),
('Mike Wilson', 'Library Technician', '2023-05-10', 'mike.wilson@library.com', '555-0103'),
('Emily Brown', 'Reference Librarian', '2023-07-01', 'emily.brown@library.com', '555-0104'),
('David Lee', 'Cataloger', '2023-09-15', 'david.lee@library.com', '555-0105');

-- Create indexes for better performance
CREATE INDEX idx_books_title ON books_table(title);
CREATE INDEX idx_books_author ON books_table(author);
CREATE INDEX idx_books_isbn ON books_table(isbn);
CREATE INDEX idx_staff_name ON staff_table(name);
CREATE INDEX idx_staff_role ON staff_table(role);

-- Display table structures
DESCRIBE admin_table;
DESCRIBE books_table;
DESCRIBE staff_table;

-- Show sample data
SELECT 'Admin Users' as Table_Name;
SELECT username, created_date, status FROM admin_table;

SELECT 'Books Inventory' as Table_Name;
SELECT title, author, isbn, quantity, status FROM books_table LIMIT 5;

SELECT 'Staff Members' as Table_Name;
SELECT name, role, hire_date, status FROM staff_table;

