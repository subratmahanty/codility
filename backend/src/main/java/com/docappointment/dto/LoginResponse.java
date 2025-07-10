package com.docappointment.dto;

public class LoginResponse {
    private String token;
    private String username;
    private String userId;
    private String role;
    private String firstName;
    private String lastName;
    private String email;
    
    public LoginResponse() {}
    
    public LoginResponse(String token, String username, String userId, String role, 
                        String firstName, String lastName, String email) {
        this.token = token;
        this.username = username;
        this.userId = userId;
        this.role = role;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }
    
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}