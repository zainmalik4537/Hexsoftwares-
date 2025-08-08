# Library Management System

A comprehensive desktop application for managing library operations built with Java Swing and MySQL.

## Features

### 🔐 Authentication System
- Secure admin login with password hashing
- Session management and logout functionality
- Input validation and error handling

### 📚 Book Management
- **View Books**: Display all books with sorting and filtering
- **Add Books**: Form with validation for title, author, ISBN, quantity
- **Edit Books**: Update existing book information
- **Delete Books**: Remove books with confirmation dialogs
- **Search Books**: Search by title or author

### 👥 Staff Management
- **View Staff**: Display staff information in organized tables
- **Add Staff**: Input form with validation for name, role, hire date
- **Edit Staff**: Update existing staff member information
- **Delete Staff**: Remove staff records with confirmation

### 🎨 User Interface
- Modern, clean Java Swing interface
- Professional tabbed navigation
- Responsive design that works on different screen sizes
- Status bar with real-time updates
- Confirmation dialogs for destructive operations

### 🔧 Technical Features
- MVC (Model-View-Controller) architecture
- Robust database connection handling
- SQL injection prevention using PreparedStatements
- Comprehensive input validation
- Error handling and logging
- Transaction management

## Requirements

### Software Requirements
- **Java**: JDK 8 or higher
- **IDE**: NetBeans 12+ (recommended) or any Java IDE
- **Database**: MySQL 5.7+ or 8.0+
- **JDBC Driver**: MySQL Connector/J

### System Requirements
- **OS**: Windows 7+, macOS 10.12+, or Linux
- **RAM**: 512MB minimum, 1GB recommended
- **Storage**: 50MB for application, additional space for database

## Installation & Setup

### 1. Database Setup

1. **Install MySQL** if not already installed
2. **Create Database** by running the provided SQL script:
   ```sql
   mysql -u root -p < database_setup.sql
   ```
3. **Verify Installation**:
   ```sql
   USE library_management;
   SHOW TABLES;
   ```

### 2. Configure Database Connection

Edit `src/utils/Constants.java` and update the database connection settings:

```java
public static final String DB_URL = "jdbc:mysql://localhost:3306/library_management";
public static final String DB_USERNAME = "root";
public static final String DB_PASSWORD = "your_mysql_password";
```

### 3. Add MySQL JDBC Driver

Download and add the MySQL Connector/J JAR file to your project:

1. Download from [MySQL Official Site](https://dev.mysql.com/downloads/connector/j/)
2. In NetBeans: Right-click project → Properties → Libraries → Add JAR/Folder
3. Select the downloaded `mysql-connector-java-x.x.x.jar` file

### 4. Compile and Run

#### Using NetBeans:
1. Open NetBeans IDE
2. File → Open Project
3. Navigate to the project directory and open
4. Right-click project → Clean and Build
5. Right-click project → Run

#### Using Command Line:
```bash
# Navigate to project directory
cd library-management-system

# Compile (ensure MySQL driver is in classpath)
javac -cp ".:mysql-connector-java-8.0.33.jar" src/**/*.java

# Run
java -cp ".:mysql-connector-java-8.0.33.jar:src" LibraryManagementSystem
```

## Default Login Credentials

- **Username**: `admin`
- **Password**: `admin123`

> ⚠️ **Important**: Change the default password after first login for security.

## Project Structure

```
library-management-system/
├── src/
│   ├── models/
│   │   ├── Admin.java          # Admin user model
│   │   ├── Book.java           # Book model
│   │   └── Staff.java          # Staff model
│   ├── database/
│   │   ├── DatabaseConnection.java    # Database connection manager
│   │   └── DatabaseOperations.java    # CRUD operations
│   ├── gui/
│   │   ├── LoginFrame.java            # Login interface
│   │   ├── MainDashboard.java         # Main application window
│   │   ├── BookPanel.java             # Book management panel
│   │   ├── BookDialog.java            # Book add/edit dialog
│   │   ├── StaffPanel.java            # Staff management panel
│   │   └── StaffDialog.java           # Staff add/edit dialog
│   ├── utils/
│   │   ├── Constants.java             # Application constants
│   │   └── ValidationUtils.java       # Input validation utilities
│   └── LibraryManagementSystem.java   # Main application class
├── database_setup.sql                 # Database schema and sample data
└── README.md                          # This file
```

## Usage Guide

### Starting the Application
1. Launch the application
2. Enter admin credentials on the login screen
3. Click "Login" or press Enter

### Managing Books
1. Navigate to the **Books** tab
2. Use **Add Book** to add new books with title, author, ISBN, and quantity
3. Select a book and click **Edit Book** to modify information
4. Select a book and click **Delete Book** to remove it (with confirmation)
5. Use the search field to find books by title or author
6. Click column headers to sort the table

### Managing Staff
1. Navigate to the **Staff** tab
2. Use **Add Staff** to add new staff members with name, role, and hire date
3. Select a staff member and click **Edit Staff** to modify information
4. Select a staff member and click **Delete Staff** to remove them (with confirmation)
5. Click column headers to sort the table

### Dashboard Overview
- View total book count and active staff count
- Use quick action buttons to directly access common functions
- Monitor system status in the status bar

## Troubleshooting

### Common Issues

**1. Database Connection Failed**
- Verify MySQL server is running
- Check database credentials in `Constants.java`
- Ensure `library_management` database exists
- Verify MySQL JDBC driver is in classpath

**2. Login Failed**
- Verify admin user exists in database:
  ```sql
  SELECT * FROM admin_table;
  ```
- Reset password if needed:
  ```sql
  UPDATE admin_table SET password_hash = SHA2('admin123', 256) WHERE username = 'admin';
  ```

**3. Table Not Found Errors**
- Run the complete `database_setup.sql` script
- Verify all tables were created:
  ```sql
  SHOW TABLES;
  DESCRIBE books_table;
  DESCRIBE staff_table;
  DESCRIBE admin_table;
  ```

**4. ClassNotFoundException: MySQL Driver**
- Download MySQL Connector/J from official site
- Add JAR file to project classpath
- Restart IDE and rebuild project

**5. GUI Display Issues**
- Update Java to latest version
- Try different Look and Feel settings
- Check system display scaling settings

### Database Maintenance

**Backup Database:**
```sql
mysqldump -u root -p library_management > library_backup.sql
```

**Restore Database:**
```sql
mysql -u root -p library_management < library_backup.sql
```

**Reset Sample Data:**
```sql
-- Re-run the INSERT statements from database_setup.sql
```

## Development

### Adding New Features
1. Create model classes in `models/` package
2. Add database operations in `DatabaseOperations.java`
3. Create GUI components in `gui/` package
4. Add validation in `ValidationUtils.java`
5. Update constants in `Constants.java`

### Code Style Guidelines
- Follow Java naming conventions
- Add JavaDoc comments for public methods
- Use proper exception handling
- Implement input validation
- Include logging for important operations

### Testing
- Test all CRUD operations
- Verify input validation
- Test database connection handling
- Check error message display
- Verify confirmation dialogs

## Security Considerations

- Passwords are hashed using SHA-256
- SQL injection prevention with PreparedStatements
- Input sanitization and validation
- Proper resource cleanup (connections, statements, result sets)
- Session management with logout functionality

## License

This project is created for educational purposes. Feel free to modify and use according to your needs.

## Support

For issues and questions:
1. Check the troubleshooting section above
2. Verify all setup steps were completed
3. Check application logs for error messages
4. Ensure all dependencies are properly installed

## Version History

- **v1.0.0** - Initial release with core functionality
  - Admin authentication system
  - Book management (CRUD operations)
  - Staff management (CRUD operations)
  - Dashboard with statistics
  - Search and sorting capabilities
  - Professional GUI with validation