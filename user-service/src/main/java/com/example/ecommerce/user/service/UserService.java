package com.example.ecommerce.user.service;

import com.example.ecommerce.common.exception.BusinessException;
import com.example.ecommerce.common.util.ValidationUtils;
import com.example.ecommerce.user.model.User;
import com.example.ecommerce.user.model.UserStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Service class for user management operations.
 */
public class UserService {
    
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    
    private final Map<Long, User> users = new HashMap<>();
    private final Map<String, User> usersByUsername = new HashMap<>();
    private final Map<String, User> usersByEmail = new HashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);
    
    /**
     * Creates a new user.
     */
    public User createUser(String username, String email, String firstName, String lastName) 
            throws BusinessException {
        
        logger.info("Creating user with username: {}, email: {}", username, email);
        
        validateUserInput(username, email, firstName, lastName);
        checkUsernameAvailability(username);
        checkEmailAvailability(email);
        
        User user = new User(username, email, firstName, lastName);
        user.setId(idGenerator.getAndIncrement());
        user.setCreatedBy("system");
        user.setUpdatedBy("system");
        
        users.put(user.getId(), user);
        usersByUsername.put(username.toLowerCase(), user);
        usersByEmail.put(email.toLowerCase(), user);
        
        logger.info("User created successfully with ID: {}", user.getId());
        return user;
    }
    
    /**
     * Finds a user by ID.
     */
    public Optional<User> findById(Long id) {
        if (id == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(users.get(id));
    }
    
    /**
     * Finds a user by username.
     */
    public Optional<User> findByUsername(String username) {
        if (!ValidationUtils.isNotEmpty(username)) {
            return Optional.empty();
        }
        return Optional.ofNullable(usersByUsername.get(username.toLowerCase()));
    }
    
    /**
     * Finds a user by email.
     */
    public Optional<User> findByEmail(String email) {
        if (!ValidationUtils.isValidEmail(email)) {
            return Optional.empty();
        }
        return Optional.ofNullable(usersByEmail.get(email.toLowerCase()));
    }
    
    /**
     * Updates user information.
     */
    public User updateUser(Long userId, String firstName, String lastName, String phoneNumber) 
            throws BusinessException {
        
        User user = findById(userId)
            .orElseThrow(() -> new BusinessException("USER_NOT_FOUND", "User not found with ID: " + userId));
        
        if (ValidationUtils.isNotEmpty(firstName)) {
            user.setFirstName(firstName);
        }
        if (ValidationUtils.isNotEmpty(lastName)) {
            user.setLastName(lastName);
        }
        if (ValidationUtils.isNotEmpty(phoneNumber)) {
            if (!ValidationUtils.isValidPhoneNumber(phoneNumber)) {
                throw new BusinessException("INVALID_PHONE", "Invalid phone number format");
            }
            user.setPhoneNumber(phoneNumber);
        }
        
        user.setUpdatedBy("system");
        user.updateTimestamp();
        
        logger.info("User updated successfully: {}", user.getId());
        return user;
    }
    
    /**
     * Changes user status.
     */
    public User changeUserStatus(Long userId, UserStatus newStatus) throws BusinessException {
        User user = findById(userId)
            .orElseThrow(() -> new BusinessException("USER_NOT_FOUND", "User not found with ID: " + userId));
        
        UserStatus oldStatus = user.getStatus();
        user.setStatus(newStatus);
        user.setUpdatedBy("system");
        user.updateTimestamp();
        
        logger.info("User status changed from {} to {} for user ID: {}", oldStatus, newStatus, userId);
        return user;
    }
    
    /**
     * Gets all active users.
     */
    public List<User> getActiveUsers() {
        List<User> activeUsers = new ArrayList<>();
        for (User user : users.values()) {
            if (user.isActive()) {
                activeUsers.add(user);
            }
        }
        return activeUsers;
    }
    
    /**
     * Gets total user count.
     */
    public long getUserCount() {
        return users.size();
    }
    
    private void validateUserInput(String username, String email, String firstName, String lastName) 
            throws BusinessException {
        
        if (!ValidationUtils.isNotEmpty(username)) {
            throw new BusinessException("INVALID_USERNAME", "Username cannot be empty");
        }
        if (!ValidationUtils.isLengthInRange(username, 3, 50)) {
            throw new BusinessException("INVALID_USERNAME", "Username must be between 3 and 50 characters");
        }
        if (!ValidationUtils.isValidEmail(email)) {
            throw new BusinessException("INVALID_EMAIL", "Invalid email format");
        }
        if (!ValidationUtils.isNotEmpty(firstName)) {
            throw new BusinessException("INVALID_FIRST_NAME", "First name cannot be empty");
        }
        if (!ValidationUtils.isNotEmpty(lastName)) {
            throw new BusinessException("INVALID_LAST_NAME", "Last name cannot be empty");
        }
    }
    
    private void checkUsernameAvailability(String username) throws BusinessException {
        if (usersByUsername.containsKey(username.toLowerCase())) {
            throw new BusinessException("USERNAME_EXISTS", "Username already exists: " + username);
        }
    }
    
    private void checkEmailAvailability(String email) throws BusinessException {
        if (usersByEmail.containsKey(email.toLowerCase())) {
            throw new BusinessException("EMAIL_EXISTS", "Email already exists: " + email);
        }
    }
}
