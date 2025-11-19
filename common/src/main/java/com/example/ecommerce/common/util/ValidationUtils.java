package com.example.ecommerce.common.util;

import org.apache.commons.lang3.StringUtils;

import java.util.regex.Pattern;

/**
 * Utility class for common validation operations.
 */
public final class ValidationUtils {
    
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$"
    );
    
    private static final Pattern PHONE_PATTERN = Pattern.compile(
        "^\\+?[1-9]\\d{8,14}$"
    );
    
    private ValidationUtils() {
        // Utility class
    }
    
    /**
     * Validates if a string is not null and not empty.
     */
    public static boolean isNotEmpty(String value) {
        return StringUtils.isNotBlank(value);
    }
    
    /**
     * Validates if a string is a valid email address.
     */
    public static boolean isValidEmail(String email) {
        return isNotEmpty(email) && EMAIL_PATTERN.matcher(email).matches();
    }
    
    /**
     * Validates if a string is a valid phone number.
     */
    public static boolean isValidPhoneNumber(String phoneNumber) {
        return isNotEmpty(phoneNumber) && PHONE_PATTERN.matcher(phoneNumber).matches();
    }
    
    /**
     * Validates if a number is positive.
     */
    public static boolean isPositive(Number number) {
        return number != null && number.doubleValue() > 0;
    }
    
    /**
     * Validates if a number is non-negative.
     */
    public static boolean isNonNegative(Number number) {
        return number != null && number.doubleValue() >= 0;
    }
    
    /**
     * Validates if a string has a minimum length.
     */
    public static boolean hasMinLength(String value, int minLength) {
        return isNotEmpty(value) && value.length() >= minLength;
    }
    
    /**
     * Validates if a string has a maximum length.
     */
    public static boolean hasMaxLength(String value, int maxLength) {
        return value == null || value.length() <= maxLength;
    }
    
    /**
     * Validates if a string length is within a range.
     */
    public static boolean isLengthInRange(String value, int minLength, int maxLength) {
        return hasMinLength(value, minLength) && hasMaxLength(value, maxLength);
    }
}
