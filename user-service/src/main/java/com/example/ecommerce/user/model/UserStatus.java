package com.example.ecommerce.user.model;

/**
 * Enumeration representing the status of a user account.
 */
public enum UserStatus {
    
    /**
     * User account is active and can perform all operations.
     */
    ACTIVE,
    
    /**
     * User account is inactive (e.g., user deactivated their account).
     */
    INACTIVE,
    
    /**
     * User account is suspended (e.g., due to policy violations).
     */
    SUSPENDED,
    
    /**
     * User account is pending activation (e.g., email verification required).
     */
    PENDING
}
