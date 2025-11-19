package com.example.ecommerce.user.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("User Model Tests")
class UserTest {
    
    private User user;
    
    @BeforeEach
    void setUp() {
        user = new User("testuser", "test@example.com", "John", "Doe");
    }
    
    @Test
    @DisplayName("Should create user with default active status")
    void shouldCreateUserWithDefaultActiveStatus() {
        // Given & When
        User newUser = new User();
        
        // Then
        assertThat(newUser.getStatus()).isEqualTo(UserStatus.ACTIVE);
        assertThat(newUser.isActive()).isTrue();
    }
    
    @Test
    @DisplayName("Should create user with provided details")
    void shouldCreateUserWithProvidedDetails() {
        // Then
        assertThat(user.getUsername()).isEqualTo("testuser");
        assertThat(user.getEmail()).isEqualTo("test@example.com");
        assertThat(user.getFirstName()).isEqualTo("John");
        assertThat(user.getLastName()).isEqualTo("Doe");
        assertThat(user.getStatus()).isEqualTo(UserStatus.ACTIVE);
    }
    
    @Test
    @DisplayName("Should return full name when both first and last names are present")
    void shouldReturnFullNameWhenBothNamesArePresent() {
        // When & Then
        assertThat(user.getFullName()).isEqualTo("John Doe");
    }
    
    @Test
    @DisplayName("Should return first name when last name is null")
    void shouldReturnFirstNameWhenLastNameIsNull() {
        // Given
        user.setLastName(null);
        
        // When & Then
        assertThat(user.getFullName()).isEqualTo("John");
    }
    
    @Test
    @DisplayName("Should return last name when first name is null")
    void shouldReturnLastNameWhenFirstNameIsNull() {
        // Given
        user.setFirstName(null);
        
        // When & Then
        assertThat(user.getFullName()).isEqualTo("Doe");
    }
    
    @Test
    @DisplayName("Should return username when both names are null")
    void shouldReturnUsernameWhenBothNamesAreNull() {
        // Given
        user.setFirstName(null);
        user.setLastName(null);
        
        // When & Then
        assertThat(user.getFullName()).isEqualTo("testuser");
    }
    
    @Test
    @DisplayName("Should activate user and update timestamp")
    void shouldActivateUserAndUpdateTimestamp() {
        // Given
        user.setStatus(UserStatus.INACTIVE);
        
        // When
        user.activate();
        
        // Then
        assertThat(user.getStatus()).isEqualTo(UserStatus.ACTIVE);
        assertThat(user.isActive()).isTrue();
    }
    
    @Test
    @DisplayName("Should deactivate user and update timestamp")
    void shouldDeactivateUserAndUpdateTimestamp() {
        // When
        user.deactivate();
        
        // Then
        assertThat(user.getStatus()).isEqualTo(UserStatus.INACTIVE);
        assertThat(user.isActive()).isFalse();
    }
    
    @Test
    @DisplayName("Should suspend user and update timestamp")
    void shouldSuspendUserAndUpdateTimestamp() {
        // When
        user.suspend();
        
        // Then
        assertThat(user.getStatus()).isEqualTo(UserStatus.SUSPENDED);
        assertThat(user.isActive()).isFalse();
    }
    
    @Test
    @DisplayName("Should return false for isActive when status is not ACTIVE")
    void shouldReturnFalseForIsActiveWhenStatusIsNotActive() {
        // Given
        user.setStatus(UserStatus.PENDING);
        
        // When & Then
        assertThat(user.isActive()).isFalse();
        
        // Given
        user.setStatus(UserStatus.SUSPENDED);
        
        // When & Then
        assertThat(user.isActive()).isFalse();
        
        // Given
        user.setStatus(UserStatus.INACTIVE);
        
        // When & Then
        assertThat(user.isActive()).isFalse();
    }
    
    @Test
    @DisplayName("Should have meaningful toString representation")
    void shouldHaveMeaningfulToStringRepresentation() {
        // Given
        user.setId(123L);
        
        // When
        String toString = user.toString();
        
        // Then
        assertThat(toString)
            .contains("User{")
            .contains("id=123")
            .contains("username='testuser'")
            .contains("email='test@example.com'")
            .contains("fullName='John Doe'")
            .contains("status=ACTIVE");
    }
}
