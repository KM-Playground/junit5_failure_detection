package com.example.ecommerce.order.integration;

import com.example.ecommerce.common.exception.BusinessException;
import com.example.ecommerce.order.model.Order;
import com.example.ecommerce.order.model.OrderItem;
import com.example.ecommerce.order.model.OrderStatus;
import com.example.ecommerce.product.model.Product;
import com.example.ecommerce.product.model.ProductCategory;
import com.example.ecommerce.product.service.ProductService;
import com.example.ecommerce.user.model.User;
import com.example.ecommerce.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("E-Commerce Integration Tests")
class ECommerceIntegrationTest {
    
    private UserService userService;
    private ProductService productService;
    private User testUser;
    private Product testProduct;
    
    @BeforeEach
    void setUp() throws BusinessException {
        userService = new UserService();
        productService = new ProductService();
        
        // Create test user
        testUser = userService.createUser("testuser", "test@example.com", "Test", "User");
        
        // Create test product
        testProduct = productService.createProduct("Test Product", "TEST-001", 
            new BigDecimal("29.99"), ProductCategory.ELECTRONICS);
        productService.updateStock(testProduct.getId(), 10);
    }
    
    @Test
    @DisplayName("Should create complete order workflow")
    void shouldCreateCompleteOrderWorkflow() throws BusinessException {
        // Given - User and Product are already created
        assertThat(testUser.isActive()).isTrue();
        assertThat(testProduct.isAvailable()).isTrue();
        
        // When - Create order
        Order order = new Order("ORD-001", testUser.getId());
        OrderItem orderItem = new OrderItem(
            testProduct.getId(),
            testProduct.getName(),
            testProduct.getSku(),
            testProduct.getPrice(),
            2
        );
        order.addItem(orderItem);
        
        // Then - Verify order creation
        assertThat(order.getCustomerId()).isEqualTo(testUser.getId());
        assertThat(order.getItems()).hasSize(1);
        assertThat(order.getSubtotal()).isEqualTo(new BigDecimal("59.98"));
        assertThat(order.isPending()).isTrue();
        
        // When - Process order through workflow
        order.confirm();
        assertThat(order.isConfirmed()).isTrue();
        
        // Simulate stock reduction
        productService.removeStock(testProduct.getId(), 2);
        Product updatedProduct = productService.findById(testProduct.getId()).orElse(null);
        assertThat(updatedProduct).isNotNull();
        assertThat(updatedProduct.getStockQuantity()).isEqualTo(8);
        
        // Complete order workflow
        order.ship();
        assertThat(order.isShipped()).isTrue();
        assertThat(order.getShippedDate()).isNotNull();
        
        order.deliver();
        assertThat(order.isDelivered()).isTrue();
        assertThat(order.getDeliveredDate()).isNotNull();
    }
    
    @Test
    @DisplayName("Should handle insufficient stock scenario")
    void shouldHandleInsufficientStockScenario() throws BusinessException {
        // Given - Product with limited stock
        Product limitedProduct = productService.createProduct("Limited Product", "LIM-001", 
            new BigDecimal("99.99"), ProductCategory.ELECTRONICS);
        productService.updateStock(limitedProduct.getId(), 2);
        
        // When - Try to remove more stock than available
        assertThatThrownBy(() -> productService.removeStock(limitedProduct.getId(), 5))
            .isInstanceOf(BusinessException.class)
            .hasMessageContaining("Insufficient stock");
        
        // Then - Stock should remain unchanged
        Product unchangedProduct = productService.findById(limitedProduct.getId()).orElse(null);
        assertThat(unchangedProduct).isNotNull();
        assertThat(unchangedProduct.getStockQuantity()).isEqualTo(2);
    }
    
    @Test
    @DisplayName("Should handle inactive user scenario")
    void shouldHandleInactiveUserScenario() throws BusinessException {
        // Given - Deactivate user
        userService.changeUserStatus(testUser.getId(), com.example.ecommerce.user.model.UserStatus.INACTIVE);
        User inactiveUser = userService.findById(testUser.getId()).orElse(null);
        
        // Then - User should be inactive
        assertThat(inactiveUser).isNotNull();
        assertThat(inactiveUser.isActive()).isFalse();
        
        // When - Create order for inactive user (business logic would prevent this)
        Order order = new Order("ORD-002", inactiveUser.getId());
        
        // Then - Order can be created but business logic should validate user status
        assertThat(order.getCustomerId()).isEqualTo(inactiveUser.getId());
        // In a real system, you would validate user status before allowing order creation
    }
    
    @Test
    @DisplayName("Should handle product deactivation scenario")
    void shouldHandleProductDeactivationScenario() throws BusinessException {
        // Given - Deactivate product
        productService.deactivateProduct(testProduct.getId());
        Product deactivatedProduct = productService.findById(testProduct.getId()).orElse(null);
        
        // Then - Product should be deactivated
        assertThat(deactivatedProduct).isNotNull();
        assertThat(deactivatedProduct.isActive()).isFalse();
        assertThat(deactivatedProduct.isAvailable()).isFalse(); // Not available even with stock
        
        // When - Try to create order with deactivated product
        Order order = new Order("ORD-003", testUser.getId());
        OrderItem orderItem = new OrderItem(
            deactivatedProduct.getId(),
            deactivatedProduct.getName(),
            deactivatedProduct.getSku(),
            deactivatedProduct.getPrice(),
            1
        );
        order.addItem(orderItem);
        
        // Then - Order can be created but business logic should validate product availability
        assertThat(order.getItems()).hasSize(1);
        // In a real system, you would validate product availability before allowing order creation
    }
    
    @Test
    @DisplayName("Should handle order cancellation scenario")
    void shouldHandleOrderCancellationScenario() throws BusinessException {
        // Given - Create and confirm order
        Order order = new Order("ORD-004", testUser.getId());
        OrderItem orderItem = new OrderItem(
            testProduct.getId(),
            testProduct.getName(),
            testProduct.getSku(),
            testProduct.getPrice(),
            1
        );
        order.addItem(orderItem);
        order.confirm();
        
        // Reduce stock
        productService.removeStock(testProduct.getId(), 1);
        
        // When - Cancel order
        order.cancel();
        
        // Then - Order should be cancelled
        assertThat(order.isCancelled()).isTrue();
        assertThat(order.getStatus()).isEqualTo(OrderStatus.CANCELLED);
        
        // In a real system, you would restore stock when order is cancelled
        productService.addStock(testProduct.getId(), 1);
        Product restoredProduct = productService.findById(testProduct.getId()).orElse(null);
        assertThat(restoredProduct).isNotNull();
        assertThat(restoredProduct.getStockQuantity()).isEqualTo(10); // Back to original
    }
    
    @Test
    @DisplayName("Should demonstrate cross-module validation")
    void shouldDemonstrateCrossModuleValidation() throws BusinessException {
        // Given - Multiple users and products
        User user2 = userService.createUser("user2", "user2@example.com", "User", "Two");
        Product product2 = productService.createProduct("Product 2", "PROD-002", 
            new BigDecimal("19.99"), ProductCategory.BOOKS);
        productService.updateStock(product2.getId(), 5);
        
        // When - Create orders for different users
        Order order1 = new Order("ORD-005", testUser.getId());
        Order order2 = new Order("ORD-006", user2.getId());
        
        order1.addItem(new OrderItem(testProduct.getId(), testProduct.getName(), 
            testProduct.getSku(), testProduct.getPrice(), 1));
        order2.addItem(new OrderItem(product2.getId(), product2.getName(), 
            product2.getSku(), product2.getPrice(), 2));
        
        // Then - Verify cross-module relationships
        assertThat(order1.getCustomerId()).isEqualTo(testUser.getId());
        assertThat(order2.getCustomerId()).isEqualTo(user2.getId());
        assertThat(order1.getSubtotal()).isEqualTo(testProduct.getPrice());
        assertThat(order2.getSubtotal()).isEqualTo(product2.getPrice().multiply(BigDecimal.valueOf(2)));
        
        // Verify services maintain separate data
        assertThat(userService.getUserCount()).isEqualTo(2);
        assertThat(productService.getProductCount()).isEqualTo(2);
    }
}
