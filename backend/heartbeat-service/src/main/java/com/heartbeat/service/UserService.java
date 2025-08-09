package com.heartbeat.service;

import com.heartbeat.common.dto.UserDTO;
import com.heartbeat.common.exception.HeartbeatException;
import com.heartbeat.common.exception.ResourceNotFoundException;
import com.heartbeat.repo.entity.User;
import com.heartbeat.repo.repository.UserRepository;
import com.heartbeat.service.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserDTO createUser(UserDTO userDTO, UUID createdBy) {
        log.info("Creating new user: {}", userDTO.getUsername());

        // Validate uniqueness
        validateUniqueUsername(userDTO.getUsername());
        validateUniqueEmail(userDTO.getEmail());

        User creator = null;
        if (createdBy != null) {
            creator = userRepository.findById(createdBy)
                    .orElseThrow(() -> ResourceNotFoundException.user(createdBy));
        }

        User user = userMapper.toEntity(userDTO);
        user.setPasswordHash(passwordEncoder.encode(userDTO.getPassword()));
        user.setCreatedBy(creator);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        User savedUser = userRepository.save(user);
        log.info("User created successfully with ID: {}", savedUser.getId());

        return userMapper.toDTO(savedUser);
    }

    @Transactional(readOnly = true)
    public UserDTO getUser(UUID userId) {
        log.debug("Fetching user with ID: {}", userId);
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> ResourceNotFoundException.user(userId));

        return userMapper.toDTO(user);
    }

    @Transactional(readOnly = true)
    public UserDTO getUserByUsername(String username) {
        log.debug("Fetching user with username: {}", username);
        
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new HeartbeatException("User not found: " + username, HttpStatus.NOT_FOUND, "USER_NOT_FOUND"));

        return userMapper.toDTO(user);
    }

    @Transactional(readOnly = true)
    public Page<UserDTO> getAllUsers(Pageable pageable) {
        log.debug("Fetching all users with pagination: {}", pageable);
        
        Page<User> users = userRepository.findByIsActiveTrue(pageable);
        return users.map(userMapper::toDTO);
    }

    @Transactional(readOnly = true)
    public Page<UserDTO> searchUsers(String query, Pageable pageable) {
        log.debug("Searching users with query: '{}' and pagination: {}", query, pageable);
        
        Page<User> users = userRepository.searchUsers(query, pageable);
        return users.map(userMapper::toDTO);
    }

    @Transactional(readOnly = true)
    public List<UserDTO> getUsersByRole(String role) {
        log.debug("Fetching users with role: {}", role);
        
        List<User> users = userRepository.findByRoleAndIsActiveTrue(
                com.heartbeat.common.enums.UserRole.valueOf(role)
        );
        return userMapper.toDTOList(users);
    }

    @Transactional
    public UserDTO updateUser(UUID userId, UserDTO userDTO, UUID updatedBy) {
        log.info("Updating user with ID: {}", userId);

        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> ResourceNotFoundException.user(userId));

        // Validate uniqueness if username/email are being changed
        if (!existingUser.getUsername().equals(userDTO.getUsername())) {
            validateUniqueUsername(userDTO.getUsername());
        }
        if (!existingUser.getEmail().equals(userDTO.getEmail())) {
            validateUniqueEmail(userDTO.getEmail());
        }

        // Update user fields
        updateUserFields(existingUser, userDTO);
        existingUser.setUpdatedAt(LocalDateTime.now());

        User savedUser = userRepository.save(existingUser);
        log.info("User updated successfully: {}", userId);

        return userMapper.toDTO(savedUser);
    }

    @Transactional
    public UserDTO updatePassword(UUID userId, String newPassword, UUID updatedBy) {
        log.info("Updating password for user: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> ResourceNotFoundException.user(userId));

        user.setPasswordHash(passwordEncoder.encode(newPassword));
        user.setUpdatedAt(LocalDateTime.now());

        User savedUser = userRepository.save(user);
        return userMapper.toDTO(savedUser);
    }

    @Transactional
    public void deactivateUser(UUID userId, UUID deactivatedBy) {
        log.info("Deactivating user with ID: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> ResourceNotFoundException.user(userId));

        user.setIsActive(false);
        user.setUpdatedAt(LocalDateTime.now());
        
        userRepository.save(user);
        log.info("User deactivated successfully: {}", userId);
    }

    @Transactional
    public void reactivateUser(UUID userId, UUID reactivatedBy) {
        log.info("Reactivating user with ID: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> ResourceNotFoundException.user(userId));

        user.setIsActive(true);
        user.setUpdatedAt(LocalDateTime.now());
        
        userRepository.save(user);
        log.info("User reactivated successfully: {}", userId);
    }

    @Transactional(readOnly = true)
    public long getUserCount() {
        return userRepository.count();
    }

    @Transactional(readOnly = true)
    public long getActiveUserCount() {
        return userRepository.countByRoleAndIsActiveTrue(null); // Count all active users
    }

    @Transactional(readOnly = true)
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    private void validateUniqueUsername(String username) {
        if (userRepository.existsByUsername(username)) {
            throw new HeartbeatException(
                    "Username already exists: " + username,
                    HttpStatus.CONFLICT,
                    "DUPLICATE_USERNAME"
            );
        }
    }

    private void validateUniqueEmail(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new HeartbeatException(
                    "Email already exists: " + email,
                    HttpStatus.CONFLICT,
                    "DUPLICATE_EMAIL"
            );
        }
    }

    private void updateUserFields(User existingUser, UserDTO userDTO) {
        if (userDTO.getUsername() != null) {
            existingUser.setUsername(userDTO.getUsername());
        }
        if (userDTO.getEmail() != null) {
            existingUser.setEmail(userDTO.getEmail());
        }
        if (userDTO.getFirstName() != null) {
            existingUser.setFirstName(userDTO.getFirstName());
        }
        if (userDTO.getLastName() != null) {
            existingUser.setLastName(userDTO.getLastName());
        }
        if (userDTO.getRole() != null) {
            existingUser.setRole(userDTO.getRole());
        }
        if (userDTO.getPhone() != null) {
            existingUser.setPhone(userDTO.getPhone());
        }
        if (userDTO.getPassword() != null && !userDTO.getPassword().isEmpty()) {
            existingUser.setPasswordHash(passwordEncoder.encode(userDTO.getPassword()));
        }
    }
}