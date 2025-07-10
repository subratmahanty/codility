package com.docappointment.dto;

import com.docappointment.model.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public class RegisterRequest {
    @NotBlank
    private String username;
    
    @NotBlank
    private String password;
    
    @Email
    @NotBlank
    private String email;
    
    @NotBlank
    private String phoneNumber;
    
    @NotNull
    private User.Role role;
    
    @NotBlank
    private String firstName;
    
    @NotBlank
    private String lastName;
    
    // Doctor specific fields
    private String specialty;
    private List<User.Availability> availability;
    
    public RegisterRequest() {}
    
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    
    public User.Role getRole() { return role; }
    public void setRole(User.Role role) { this.role = role; }
    
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    
    public String getSpecialty() { return specialty; }
    public void setSpecialty(String specialty) { this.specialty = specialty; }
    
    public List<User.Availability> getAvailability() { return availability; }
    public void setAvailability(List<User.Availability> availability) { this.availability = availability; }
}