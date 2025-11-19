package com.example.ecommerce.order.model;

/**
 * Enumeration representing the status of an order.
 */
public enum OrderStatus {
    
    /**
     * Order has been created but not yet confirmed.
     */
    PENDING,
    
    /**
     * Order has been confirmed and is being processed.
     */
    CONFIRMED,
    
    /**
     * Order has been shipped to the customer.
     */
    SHIPPED,
    
    /**
     * Order has been delivered to the customer.
     */
    DELIVERED,
    
    /**
     * Order has been cancelled.
     */
    CANCELLED
}
