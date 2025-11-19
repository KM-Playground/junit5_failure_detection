package com.example.ecommerce.order.model;

import com.example.ecommerce.common.model.BaseEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Order entity representing a customer order.
 */
public class Order extends BaseEntity {
    
    private String orderNumber;
    private Long customerId;
    private OrderStatus status;
    private BigDecimal totalAmount;
    private BigDecimal taxAmount;
    private BigDecimal shippingAmount;
    private String shippingAddress;
    private String billingAddress;
    private LocalDateTime orderDate;
    private LocalDateTime shippedDate;
    private LocalDateTime deliveredDate;
    private List<OrderItem> items;
    
    public Order() {
        super();
        this.status = OrderStatus.PENDING;
        this.orderDate = LocalDateTime.now();
        this.items = new ArrayList<>();
        this.totalAmount = BigDecimal.ZERO;
        this.taxAmount = BigDecimal.ZERO;
        this.shippingAmount = BigDecimal.ZERO;
    }
    
    public Order(String orderNumber, Long customerId) {
        this();
        this.orderNumber = orderNumber;
        this.customerId = customerId;
    }
    
    public String getOrderNumber() {
        return orderNumber;
    }
    
    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }
    
    public Long getCustomerId() {
        return customerId;
    }
    
    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }
    
    public OrderStatus getStatus() {
        return status;
    }
    
    public void setStatus(OrderStatus status) {
        this.status = status;
        updateTimestamp();
    }
    
    public BigDecimal getTotalAmount() {
        return totalAmount;
    }
    
    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }
    
    public BigDecimal getTaxAmount() {
        return taxAmount;
    }
    
    public void setTaxAmount(BigDecimal taxAmount) {
        this.taxAmount = taxAmount;
    }
    
    public BigDecimal getShippingAmount() {
        return shippingAmount;
    }
    
    public void setShippingAmount(BigDecimal shippingAmount) {
        this.shippingAmount = shippingAmount;
    }
    
    public String getShippingAddress() {
        return shippingAddress;
    }
    
    public void setShippingAddress(String shippingAddress) {
        this.shippingAddress = shippingAddress;
    }
    
    public String getBillingAddress() {
        return billingAddress;
    }
    
    public void setBillingAddress(String billingAddress) {
        this.billingAddress = billingAddress;
    }
    
    public LocalDateTime getOrderDate() {
        return orderDate;
    }
    
    public void setOrderDate(LocalDateTime orderDate) {
        this.orderDate = orderDate;
    }
    
    public LocalDateTime getShippedDate() {
        return shippedDate;
    }
    
    public void setShippedDate(LocalDateTime shippedDate) {
        this.shippedDate = shippedDate;
    }
    
    public LocalDateTime getDeliveredDate() {
        return deliveredDate;
    }
    
    public void setDeliveredDate(LocalDateTime deliveredDate) {
        this.deliveredDate = deliveredDate;
    }
    
    public List<OrderItem> getItems() {
        return new ArrayList<>(items);
    }
    
    public void setItems(List<OrderItem> items) {
        this.items = items != null ? new ArrayList<>(items) : new ArrayList<>();
        recalculateTotal();
    }
    
    public void addItem(OrderItem item) {
        if (item != null) {
            this.items.add(item);
            recalculateTotal();
            updateTimestamp();
        }
    }
    
    public void removeItem(OrderItem item) {
        if (this.items.remove(item)) {
            recalculateTotal();
            updateTimestamp();
        }
    }
    
    public int getItemCount() {
        return items.size();
    }
    
    public BigDecimal getSubtotal() {
        return items.stream()
            .map(OrderItem::getTotalPrice)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    public void confirm() {
        this.status = OrderStatus.CONFIRMED;
        updateTimestamp();
    }
    
    public void ship() {
        this.status = OrderStatus.SHIPPED;
        this.shippedDate = LocalDateTime.now();
        updateTimestamp();
    }
    
    public void deliver() {
        this.status = OrderStatus.DELIVERED;
        this.deliveredDate = LocalDateTime.now();
        updateTimestamp();
    }
    
    public void cancel() {
        this.status = OrderStatus.CANCELLED;
        updateTimestamp();
    }
    
    public boolean isPending() {
        return OrderStatus.PENDING.equals(status);
    }
    
    public boolean isConfirmed() {
        return OrderStatus.CONFIRMED.equals(status);
    }
    
    public boolean isShipped() {
        return OrderStatus.SHIPPED.equals(status);
    }
    
    public boolean isDelivered() {
        return OrderStatus.DELIVERED.equals(status);
    }
    
    public boolean isCancelled() {
        return OrderStatus.CANCELLED.equals(status);
    }
    
    private void recalculateTotal() {
        BigDecimal subtotal = getSubtotal();
        this.totalAmount = subtotal.add(taxAmount).add(shippingAmount);
    }
    
    @Override
    public String toString() {
        return "Order{" +
                "id=" + getId() +
                ", orderNumber='" + orderNumber + '\'' +
                ", customerId=" + customerId +
                ", status=" + status +
                ", totalAmount=" + totalAmount +
                ", itemCount=" + getItemCount() +
                ", orderDate=" + orderDate +
                '}';
    }
}
