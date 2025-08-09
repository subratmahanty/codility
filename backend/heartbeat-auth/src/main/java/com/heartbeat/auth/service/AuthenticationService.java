package com.heartbeat.auth.service;

import com.heartbeat.common.dto.UserDTO;
import com.heartbeat.common.exception.HeartbeatException;
import com.heartbeat.repo.entity.User;
import com.heartbeat.repo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserDetailsServiceImpl userDetailsService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RedisTemplate<String, String> redisTemplate;

    private static final String BLACKLIST_PREFIX = "blacklist:";
    private static final String SESSION_PREFIX = "session:";

    @Transactional
    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        log.info("Authentication attempt for user: {}", request.getUsername());

        try {
            // Authenticate user credentials
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword()
                    )
            );

            HeartbeatUserDetails userDetails = (HeartbeatUserDetails) authentication.getPrincipal();

            // Generate JWT tokens
            String accessToken = jwtService.generateToken(userDetails, userDetails.getId(), userDetails.getRole().name());
            String refreshToken = jwtService.generateRefreshToken(userDetails, userDetails.getId());

            // Update last login
            userDetailsService.updateLastLogin(userDetails.getUsername());

            // Store session in Redis
            storeUserSession(userDetails.getId().toString(), accessToken);

            log.info("User authenticated successfully: {}", request.getUsername());

            return AuthenticationResponse.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .tokenType("Bearer")
                    .expiresIn(jwtService.getExpirationTime())
                    .user(convertToUserDTO(userDetails))
                    .build();

        } catch (BadCredentialsException e) {
            log.warn("Authentication failed for user: {} - Invalid credentials", request.getUsername());
            throw new HeartbeatException("Invalid username or password", HttpStatus.UNAUTHORIZED, "INVALID_CREDENTIALS");
        } catch (Exception e) {
            log.error("Authentication error for user: {} - {}", request.getUsername(), e.getMessage());
            throw new HeartbeatException("Authentication failed", HttpStatus.INTERNAL_SERVER_ERROR, "AUTH_ERROR");
        }
    }

    @Transactional
    public AuthenticationResponse refreshToken(String refreshToken) {
        try {
            if (!jwtService.isTokenValid(refreshToken)) {
                throw new HeartbeatException("Invalid refresh token", HttpStatus.UNAUTHORIZED, "INVALID_REFRESH_TOKEN");
            }

            String username = jwtService.extractUsername(refreshToken);
            HeartbeatUserDetails userDetails = (HeartbeatUserDetails) userDetailsService.loadUserByUsername(username);

            String newAccessToken = jwtService.generateToken(userDetails, userDetails.getId(), userDetails.getRole().name());
            String newRefreshToken = jwtService.generateRefreshToken(userDetails, userDetails.getId());

            // Update session in Redis
            storeUserSession(userDetails.getId().toString(), newAccessToken);

            log.info("Token refreshed for user: {}", username);

            return AuthenticationResponse.builder()
                    .accessToken(newAccessToken)
                    .refreshToken(newRefreshToken)
                    .tokenType("Bearer")
                    .expiresIn(jwtService.getExpirationTime())
                    .user(convertToUserDTO(userDetails))
                    .build();

        } catch (Exception e) {
            log.error("Token refresh failed: {}", e.getMessage());
            throw new HeartbeatException("Token refresh failed", HttpStatus.UNAUTHORIZED, "REFRESH_FAILED");
        }
    }

    public void logout(String token) {
        try {
            if (token != null && jwtService.isTokenValid(token)) {
                String username = jwtService.extractUsername(token);
                
                // Add token to blacklist
                blacklistToken(token);
                
                // Remove user session
                removeUserSession(jwtService.extractUserId(token).toString());
                
                // Clear security context
                SecurityContextHolder.clearContext();
                
                log.info("User logged out successfully: {}", username);
            }
        } catch (Exception e) {
            log.error("Logout error: {}", e.getMessage());
            // Don't throw exception on logout errors
        }
    }

    public boolean isTokenBlacklisted(String token) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(BLACKLIST_PREFIX + token));
    }

    private void blacklistToken(String token) {
        try {
            long expirationTime = jwtService.extractExpiration(token).getTime();
            long currentTime = System.currentTimeMillis();
            long ttl = expirationTime - currentTime;

            if (ttl > 0) {
                redisTemplate.opsForValue().set(
                        BLACKLIST_PREFIX + token,
                        "blacklisted",
                        ttl,
                        TimeUnit.MILLISECONDS
                );
            }
        } catch (Exception e) {
            log.warn("Failed to blacklist token: {}", e.getMessage());
        }
    }

    private void storeUserSession(String userId, String token) {
        try {
            redisTemplate.opsForValue().set(
                    SESSION_PREFIX + userId,
                    token,
                    jwtService.getExpirationTime(),
                    TimeUnit.SECONDS
            );
        } catch (Exception e) {
            log.warn("Failed to store user session: {}", e.getMessage());
        }
    }

    private void removeUserSession(String userId) {
        try {
            redisTemplate.delete(SESSION_PREFIX + userId);
        } catch (Exception e) {
            log.warn("Failed to remove user session: {}", e.getMessage());
        }
    }

    @Transactional
    public UserDTO changePassword(String username, String currentPassword, String newPassword) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new HeartbeatException("User not found", HttpStatus.NOT_FOUND, "USER_NOT_FOUND"));

        if (!passwordEncoder.matches(currentPassword, user.getPasswordHash())) {
            throw new HeartbeatException("Current password is incorrect", HttpStatus.BAD_REQUEST, "INVALID_CURRENT_PASSWORD");
        }

        user.setPasswordHash(passwordEncoder.encode(newPassword));
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);

        log.info("Password changed for user: {}", username);

        return convertToUserDTO(user);
    }

    private UserDTO convertToUserDTO(HeartbeatUserDetails userDetails) {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(userDetails.getId());
        userDTO.setUsername(userDetails.getUsername());
        userDTO.setEmail(userDetails.getEmail());
        userDTO.setFirstName(userDetails.getFirstName());
        userDTO.setLastName(userDetails.getLastName());
        userDTO.setRole(userDetails.getRole());
        userDTO.setPhone(userDetails.getPhone());
        userDTO.setIsActive(userDetails.getIsActive());
        userDTO.setLastLogin(userDetails.getLastLogin());
        return userDTO;
    }

    private UserDTO convertToUserDTO(User user) {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setUsername(user.getUsername());
        userDTO.setEmail(user.getEmail());
        userDTO.setFirstName(user.getFirstName());
        userDTO.setLastName(user.getLastName());
        userDTO.setRole(user.getRole());
        userDTO.setPhone(user.getPhone());
        userDTO.setIsActive(user.getIsActive());
        userDTO.setCreatedAt(user.getCreatedAt());
        userDTO.setUpdatedAt(user.getUpdatedAt());
        userDTO.setLastLogin(user.getLastLogin());
        return userDTO;
    }

    public static class AuthenticationRequest {
        private String username;
        private String password;

        // Constructors
        public AuthenticationRequest() {}

        public AuthenticationRequest(String username, String password) {
            this.username = username;
            this.password = password;
        }

        // Getters and Setters
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }

    public static class AuthenticationResponse {
        private String accessToken;
        private String refreshToken;
        private String tokenType;
        private Long expiresIn;
        private UserDTO user;

        // Builder pattern
        public static Builder builder() {
            return new Builder();
        }

        public static class Builder {
            private final AuthenticationResponse response = new AuthenticationResponse();

            public Builder accessToken(String accessToken) {
                response.accessToken = accessToken;
                return this;
            }

            public Builder refreshToken(String refreshToken) {
                response.refreshToken = refreshToken;
                return this;
            }

            public Builder tokenType(String tokenType) {
                response.tokenType = tokenType;
                return this;
            }

            public Builder expiresIn(Long expiresIn) {
                response.expiresIn = expiresIn;
                return this;
            }

            public Builder user(UserDTO user) {
                response.user = user;
                return this;
            }

            public AuthenticationResponse build() {
                return response;
            }
        }

        // Getters and Setters
        public String getAccessToken() { return accessToken; }
        public void setAccessToken(String accessToken) { this.accessToken = accessToken; }
        public String getRefreshToken() { return refreshToken; }
        public void setRefreshToken(String refreshToken) { this.refreshToken = refreshToken; }
        public String getTokenType() { return tokenType; }
        public void setTokenType(String tokenType) { this.tokenType = tokenType; }
        public Long getExpiresIn() { return expiresIn; }
        public void setExpiresIn(Long expiresIn) { this.expiresIn = expiresIn; }
        public UserDTO getUser() { return user; }
        public void setUser(UserDTO user) { this.user = user; }
    }
}