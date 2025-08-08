package models;

import java.sql.Date;

/**
 * Staff model class representing library staff members
 * Contains staff information and role management
 */
public class Staff {
    private int staffId;
    private String name;
    private String role;
    private Date hireDate;
    private String status;
    private String email;
    private String phone;
    
    // Constructors
    public Staff() {}
    
    public Staff(String name, String role, Date hireDate, String email, String phone) {
        this.name = name;
        this.role = role;
        this.hireDate = hireDate;
        this.email = email;
        this.phone = phone;
    }
    
    public Staff(int staffId, String name, String role, Date hireDate, 
                 String status, String email, String phone) {
        this.staffId = staffId;
        this.name = name;
        this.role = role;
        this.hireDate = hireDate;
        this.status = status;
        this.email = email;
        this.phone = phone;
    }
    
    // Getters and Setters
    public int getStaffId() { return staffId; }
    public void setStaffId(int staffId) { this.staffId = staffId; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    
    public Date getHireDate() { return hireDate; }
    public void setHireDate(Date hireDate) { this.hireDate = hireDate; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    
    /**
     * Checks if staff member is active
     */
    public boolean isActive() {
        return "ACTIVE".equals(status);
    }
    
    @Override
    public String toString() {
        return "Staff{" +
                "staffId=" + staffId +
                ", name='" + name + '\'' +
                ", role='" + role + '\'' +
                ", hireDate=" + hireDate +
                ", status='" + status + '\'' +
                '}';
    }
}