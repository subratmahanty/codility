package com.heartbeat.auth.service;

import com.heartbeat.common.enums.UserRole;
import lombok.Builder;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.UUID;

@Data
@Builder
public class HeartbeatUserDetails implements UserDetails {
    
    private UUID id;
    private String username;
    private String password;
    private String email;
    private String firstName;
    private String lastName;
    private UserRole role;
    private String phone;
    private Boolean isActive;
    private LocalDateTime lastLogin;
    private Collection<? extends GrantedAuthority> authorities;
    private boolean accountNonExpired;
    private boolean accountNonLocked;
    private boolean credentialsNonExpired;
    private boolean enabled;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return accountNonExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return accountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return credentialsNonExpired;
    }

    @Override
    public boolean isEnabled() {
        return enabled && isActive;
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    public boolean hasRole(UserRole role) {
        return this.role == role;
    }

    public boolean isAdmin() {
        return role == UserRole.ADMIN;
    }

    public boolean canManagePatients() {
        return role != null && role.canManagePatients();
    }

    public boolean canCreateTreatments() {
        return role != null && role.canCreateTreatments();
    }

    public boolean canManageMedicines() {
        return role != null && role.canManageMedicines();
    }

    public boolean canManageLabTests() {
        return role != null && role.canManageLabTests();
    }

    public boolean canEnterLabResults() {
        return role != null && role.canEnterLabResults();
    }

    public boolean canUploadFiles() {
        return role != null && role.canUploadFiles();
    }

    public boolean canManageNotifications() {
        return role != null && role.canManageNotifications();
    }

    public boolean canCreatePracticeTemplates() {
        return role != null && role.canCreatePracticeTemplates();
    }
}