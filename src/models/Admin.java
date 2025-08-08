package models;

import java.sql.Timestamp;

/**
 * Admin model class representing administrator users
 * Handles admin user data and authentication
 */
public class Admin {
    private int id;
    private String username;
    private String passwordHash;
    private Timestamp createdDate;
    private Timestamp lastLogin;
    private String status;
    
    // Constructors
    public Admin() {}
    
    public Admin(String username, String passwordHash) {
        this.username = username;
        this.passwordHash = passwordHash;
    }
    
    public Admin(int id, String username, String passwordHash, Timestamp createdDate, String status) {
        this.id = id;
        this.username = username;
        this.passwordHash = passwordHash;
        this.createdDate = createdDate;
        this.status = status;
    }
    
    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    
    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    
    public Timestamp getCreatedDate() { return createdDate; }
    public void setCreatedDate(Timestamp createdDate) { this.createdDate = createdDate; }
    
    public Timestamp getLastLogin() { return lastLogin; }
    public void setLastLogin(Timestamp lastLogin) { this.lastLogin = lastLogin; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    @Override
    public String toString() {
        return "Admin{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", status='" + status + '\'' +
                ", createdDate=" + createdDate +
                '}';
    }
}