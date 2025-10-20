package com.networkinsights.ai.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Team Member model
 * Used for Team Coordination functionality, storing team member information
 */
public class TeamMember {
    
    private String id;                   // Member ID (alias for memberId)
    private String memberId;           // Member ID
    private String name;               // Full name
    private String email;              // Email address
    private String role;                // Job role/title
    private String department;         // Department
    private String location;           // Office location
    private String timezone;           // Time zone
    private String expertise;          // Areas of expertise
    private String languages;          // Languages spoken
    private String availability;       // Current availability status
    private Map<String, String> contactInfo; // Additional contact information
    private Map<String, String> responsibilities; // Key responsibilities
    
    // Constructor
    public TeamMember() {
        this.contactInfo = new HashMap<>();
        this.responsibilities = new HashMap<>();
    }
    
    public TeamMember(String memberId, String name, String email, String role) {
        this();
        this.memberId = memberId;
        this.name = name;
        this.email = email;
        this.role = role;
    }
    
    // Getters and Setters
    public String getId() {
        return id != null ? id : memberId;
    }
    
    public void setId(String id) {
        this.id = id;
        this.memberId = id;
    }
    
    public String getMemberId() {
        return memberId;
    }
    
    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getRole() {
        return role;
    }
    
    public void setRole(String role) {
        this.role = role;
    }
    
    public String getDepartment() {
        return department;
    }
    
    public void setDepartment(String department) {
        this.department = department;
    }
    
    public String getLocation() {
        return location;
    }
    
    public void setLocation(String location) {
        this.location = location;
    }
    
    public String getTimezone() {
        return timezone;
    }
    
    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }
    
    public String getExpertise() {
        return expertise;
    }
    
    public void setExpertise(String expertise) {
        this.expertise = expertise;
    }
    
    public String getLanguages() {
        return languages;
    }
    
    public void setLanguages(String languages) {
        this.languages = languages;
    }
    
    public String getAvailability() {
        return availability;
    }
    
    public void setAvailability(String availability) {
        this.availability = availability;
    }
    
    public Map<String, String> getContactInfo() {
        return contactInfo;
    }
    
    public void setContactInfo(Map<String, String> contactInfo) {
        this.contactInfo = contactInfo;
    }
    
    public Map<String, String> getResponsibilities() {
        return responsibilities;
    }
    
    public void setResponsibilities(Map<String, String> responsibilities) {
        this.responsibilities = responsibilities;
    }
    
    @Override
    public String toString() {
        return "TeamMember{" +
                "memberId='" + memberId + '\'' +
                ", name='" + name + '\'' +
                ", role='" + role + '\'' +
                ", location='" + location + '\'' +
                ", expertise='" + expertise + '\'' +
                '}';
    }
}
