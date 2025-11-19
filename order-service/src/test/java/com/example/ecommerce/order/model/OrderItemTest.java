package com.example.ecommerce.order.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("OrderItem Model Tests")
class OrderItemTest {
    
    private OrderItem orderItem;
    
    @BeforeEach
    void setUp() {
        orderItem = new OrderItem(1L, "Test Product", "TEST-001", new BigDecimal("10.00"), 3);
    }
    
    @Test
    @DisplayName("Should create order item with calculated total price")
    void shouldCreateOrderItemWithCalculatedTotalPrice() {
        // Then
        assertThat(orderItem.getProductId()).isEqualTo(1L);
        assertThat(orderItem.getProductName()).isEqualTo("Test Product");
        assertThat(orderItem.getProductSku()).isEqualTo("TEST-001");
        assertThat(orderItem.getUnitPrice()).isEqualTo(new BigDecimal("10.00"));
        assertThat(orderItem.getQuantity()).isEqualTo(3);
        assertThat(orderItem.getTotalPrice()).isEqualTo(new BigDecimal("30.00"));
    }
    
    @Test
    @DisplayName("Should recalculate total price when unit price changes")
    void shouldRecalculateTotalPriceWhenUnitPriceChanges() {
        // When
        orderItem.setUnitPrice(new BigDecimal("15.00"));
        
        // Then
        assertThat(orderItem.getTotalPrice()).isEqualTo(new BigDecimal("45.00"));
    }
    
    @Test
    @DisplayName("Should recalculate total price when quantity changes")
    void shouldRecalculateTotalPriceWhenQuantityChanges() {
        // When
        orderItem.setQuantity(5);
        
        // Then
        assertThat(orderItem.getTotalPrice()).isEqualTo(new BigDecimal("50.00"));
    }
    
    @Test
    @DisplayName("Should handle null unit price")
    void shouldHandleNullUnitPrice() {
        // When
        orderItem.setUnitPrice(null);
        
        // Then
        assertThat(orderItem.getTotalPrice()).isEqualTo(BigDecimal.ZERO);
    }
    
    @Test
    @DisplayName("Should handle null quantity")
    void shouldHandleNullQuantity() {
        // When
        orderItem.setQuantity(null);
        
        // Then
        assertThat(orderItem.getTotalPrice()).isEqualTo(BigDecimal.ZERO);
    }
    
    @Test
    @DisplayName("Should be equal when product ID and SKU are the same")
    void shouldBeEqualWhenProductIdAndSkuAreTheSame() {
        // Given
        OrderItem other = new OrderItem(1L, "Different Name", "TEST-001", new BigDecimal("20.00"), 1);
        
        // When & Then
        assertThat(orderItem).isEqualTo(other);
        assertThat(orderItem.hashCode()).isEqualTo(other.hashCode());
    }
    
    @Test
    @DisplayName("Should not be equal when product ID is different")
    void shouldNotBeEqualWhenProductIdIsDifferent() {
        // Given
        OrderItem other = new OrderItem(2L, "Test Product", "TEST-001", new BigDecimal("10.00"), 3);
        
        // When & Then
        assertThat(orderItem).isNotEqualTo(other);
    }
    
    @Test
    @DisplayName("Should not be equal when SKU is different")
    void shouldNotBeEqualWhenSkuIsDifferent() {
        // Given
        OrderItem other = new OrderItem(1L, "Test Product", "TEST-002", new BigDecimal("10.00"), 3);
        
        // When & Then
        assertThat(orderItem).isNotEqualTo(other);
    }
    
    @Test
    @DisplayName("Should have meaningful toString representation")
    void shouldHaveMeaningfulToStringRepresentation() {
        // When
        String toString = orderItem.toString();
        
        // Then
        assertThat(toString)
            .contains("OrderItem{")
            .contains("productId=1")
            .contains("productName='Test Product'")
            .contains("productSku='TEST-001'")
            .contains("unitPrice=10.00")
            .contains("quantity=3")
            .contains("totalPrice=30.00");
    }
}
