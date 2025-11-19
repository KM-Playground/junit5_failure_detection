package com.example.ecommerce.product.model;

import com.example.ecommerce.common.model.BaseEntity;

import java.math.BigDecimal;

/**
 * Product entity representing a product in the catalog.
 */
public class Product extends BaseEntity {
    
    private String name;
    private String description;
    private String sku;
    private BigDecimal price;
    private ProductCategory category;
    private Integer stockQuantity;
    private boolean active;
    
    public Product() {
        super();
        this.active = true;
        this.stockQuantity = 0;
    }
    
    public Product(String name, String sku, BigDecimal price, ProductCategory category) {
        this();
        this.name = name;
        this.sku = sku;
        this.price = price;
        this.category = category;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getSku() {
        return sku;
    }
    
    public void setSku(String sku) {
        this.sku = sku;
    }
    
    public BigDecimal getPrice() {
        return price;
    }
    
    public void setPrice(BigDecimal price) {
        this.price = price;
    }
    
    public ProductCategory getCategory() {
        return category;
    }
    
    public void setCategory(ProductCategory category) {
        this.category = category;
    }
    
    public Integer getStockQuantity() {
        return stockQuantity;
    }
    
    public void setStockQuantity(Integer stockQuantity) {
        this.stockQuantity = stockQuantity;
    }
    
    public boolean isActive() {
        return active;
    }
    
    public void setActive(boolean active) {
        this.active = active;
    }
    
    public boolean isInStock() {
        return stockQuantity != null && stockQuantity > 0;
    }
    
    public boolean isAvailable() {
        return active && isInStock();
    }
    
    public void addStock(int quantity) {
        if (quantity > 0) {
            this.stockQuantity = (this.stockQuantity == null ? 0 : this.stockQuantity) + quantity;
            updateTimestamp();
        }
    }
    
    public boolean removeStock(int quantity) {
        if (quantity > 0 && this.stockQuantity != null && this.stockQuantity >= quantity) {
            this.stockQuantity -= quantity;
            updateTimestamp();
            return true;
        }
        return false;
    }
    
    public void activate() {
        this.active = true;
        updateTimestamp();
    }
    
    public void deactivate() {
        this.active = false;
        updateTimestamp();
    }
    
    @Override
    public String toString() {
        return "Product{" +
                "id=" + getId() +
                ", name='" + name + '\'' +
                ", sku='" + sku + '\'' +
                ", price=" + price +
                ", category=" + category +
                ", stockQuantity=" + stockQuantity +
                ", active=" + active +
                '}';
    }
}
