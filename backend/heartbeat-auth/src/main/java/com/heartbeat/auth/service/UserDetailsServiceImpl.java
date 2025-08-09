package com.heartbeat.auth.service;

import com.heartbeat.repo.entity.User;
import com.heartbeat.repo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.debug("Loading user by username: {}", username);
        
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    log.warn("User not found with username: {}", username);
                    return new UsernameNotFoundException("User not found: " + username);
                });

        if (!user.getIsActive()) {
            log.warn("User account is disabled: {}", username);
            throw new UsernameNotFoundException("User account is disabled: " + username);
        }

        log.debug("Successfully loaded user: {} with role: {}", username, user.getRole());

        return HeartbeatUserDetails.builder()
                .id(user.getId())
                .username(user.getUsername())
                .password(user.getPasswordHash())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .role(user.getRole())
                .phone(user.getPhone())
                .isActive(user.getIsActive())
                .lastLogin(user.getLastLogin())
                .authorities(Collections.singletonList(
                        new SimpleGrantedAuthority("ROLE_" + user.getRole().name())
                ))
                .accountNonExpired(true)
                .accountNonLocked(true)
                .credentialsNonExpired(true)
                .enabled(user.getIsActive())
                .build();
    }

    public UserDetails loadUserById(String userId) {
        log.debug("Loading user by ID: {}", userId);
        
        User user = userRepository.findById(java.util.UUID.fromString(userId))
                .orElseThrow(() -> {
                    log.warn("User not found with ID: {}", userId);
                    return new UsernameNotFoundException("User not found with ID: " + userId);
                });

        if (!user.getIsActive()) {
            log.warn("User account is disabled: {}", user.getUsername());
            throw new UsernameNotFoundException("User account is disabled: " + user.getUsername());
        }

        return HeartbeatUserDetails.builder()
                .id(user.getId())
                .username(user.getUsername())
                .password(user.getPasswordHash())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .role(user.getRole())
                .phone(user.getPhone())
                .isActive(user.getIsActive())
                .lastLogin(user.getLastLogin())
                .authorities(Collections.singletonList(
                        new SimpleGrantedAuthority("ROLE_" + user.getRole().name())
                ))
                .accountNonExpired(true)
                .accountNonLocked(true)
                .credentialsNonExpired(true)
                .enabled(user.getIsActive())
                .build();
    }

    @Transactional
    public void updateLastLogin(String username) {
        userRepository.findByUsername(username)
                .ifPresent(user -> {
                    user.setLastLogin(LocalDateTime.now());
                    userRepository.save(user);
                    log.debug("Updated last login for user: {}", username);
                });
    }
}