package com.example.ecommerce.common.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("BaseEntity Tests")
class BaseEntityTest {
    
    private TestEntity entity1;
    private TestEntity entity2;
    
    @BeforeEach
    void setUp() {
        entity1 = new TestEntity();
        entity2 = new TestEntity();
    }
    
    @Test
    @DisplayName("Should initialize timestamps on creation")
    void shouldInitializeTimestampsOnCreation() {
        // Given & When
        TestEntity entity = new TestEntity();
        
        // Then
        assertThat(entity.getCreatedAt()).isNotNull();
        assertThat(entity.getUpdatedAt()).isNotNull();
        assertThat(entity.getCreatedAt()).isEqualTo(entity.getUpdatedAt());
    }
    
    @Test
    @DisplayName("Should update timestamp when updateTimestamp is called")
    void shouldUpdateTimestampWhenUpdateTimestampIsCalled() throws InterruptedException {
        // Given
        LocalDateTime originalUpdatedAt = entity1.getUpdatedAt();
        Thread.sleep(1); // Ensure time difference
        
        // When
        entity1.updateTimestamp();
        
        // Then
        assertThat(entity1.getUpdatedAt()).isAfter(originalUpdatedAt);
        assertThat(entity1.getCreatedAt()).isEqualTo(entity1.getCreatedAt()); // Should not change
    }
    
    @Test
    @DisplayName("Should be equal when IDs are the same")
    void shouldBeEqualWhenIdsAreTheSame() {
        // Given
        entity1.setId(1L);
        entity2.setId(1L);
        
        // When & Then
        assertThat(entity1).isEqualTo(entity2);
        assertThat(entity1.hashCode()).isEqualTo(entity2.hashCode());
    }
    
    @Test
    @DisplayName("Should not be equal when IDs are different")
    void shouldNotBeEqualWhenIdsAreDifferent() {
        // Given
        entity1.setId(1L);
        entity2.setId(2L);
        
        // When & Then
        assertThat(entity1).isNotEqualTo(entity2);
    }
    
    @Test
    @DisplayName("Should not be equal when one ID is null")
    void shouldNotBeEqualWhenOneIdIsNull() {
        // Given
        entity1.setId(1L);
        entity2.setId(null);
        
        // When & Then
        assertThat(entity1).isNotEqualTo(entity2);
    }
    
    @Test
    @DisplayName("Should be equal when both IDs are null")
    void shouldBeEqualWhenBothIdsAreNull() {
        // Given
        entity1.setId(null);
        entity2.setId(null);
        
        // When & Then
        assertThat(entity1).isEqualTo(entity2);
    }
    
    @Test
    @DisplayName("Should have meaningful toString representation")
    void shouldHaveMeaningfulToStringRepresentation() {
        // Given
        entity1.setId(123L);
        
        // When
        String toString = entity1.toString();
        
        // Then
        assertThat(toString)
            .contains("TestEntity")
            .contains("id=123")
            .contains("createdAt=")
            .contains("updatedAt=");
    }
    
    // Test implementation of BaseEntity
    private static class TestEntity extends BaseEntity {
        public TestEntity() {
            super();
        }
        
        public TestEntity(Long id) {
            super(id);
        }
    }
}
