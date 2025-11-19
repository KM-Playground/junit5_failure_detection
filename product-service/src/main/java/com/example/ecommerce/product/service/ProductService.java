package com.example.ecommerce.product.service;

import com.example.ecommerce.common.exception.BusinessException;
import com.example.ecommerce.common.util.ValidationUtils;
import com.example.ecommerce.product.model.Product;
import com.example.ecommerce.product.model.ProductCategory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * Service class for product management operations.
 */
public class ProductService {
    
    private static final Logger logger = LoggerFactory.getLogger(ProductService.class);
    
    private final Map<Long, Product> products = new HashMap<>();
    private final Map<String, Product> productsBySku = new HashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);
    
    /**
     * Creates a new product.
     */
    public Product createProduct(String name, String sku, BigDecimal price, ProductCategory category) 
            throws BusinessException {
        
        logger.info("Creating product with name: {}, sku: {}", name, sku);
        
        validateProductInput(name, sku, price, category);
        checkSkuAvailability(sku);
        
        Product product = new Product(name, sku, price, category);
        product.setId(idGenerator.getAndIncrement());
        product.setCreatedBy("system");
        product.setUpdatedBy("system");
        
        products.put(product.getId(), product);
        productsBySku.put(sku.toUpperCase(), product);
        
        logger.info("Product created successfully with ID: {}", product.getId());
        return product;
    }
    
    /**
     * Finds a product by ID.
     */
    public Optional<Product> findById(Long id) {
        if (id == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(products.get(id));
    }
    
    /**
     * Finds a product by SKU.
     */
    public Optional<Product> findBySku(String sku) {
        if (!ValidationUtils.isNotEmpty(sku)) {
            return Optional.empty();
        }
        return Optional.ofNullable(productsBySku.get(sku.toUpperCase()));
    }
    
    /**
     * Updates product information.
     */
    public Product updateProduct(Long productId, String name, String description, BigDecimal price) 
            throws BusinessException {
        
        Product product = findById(productId)
            .orElseThrow(() -> new BusinessException("PRODUCT_NOT_FOUND", "Product not found with ID: " + productId));
        
        if (ValidationUtils.isNotEmpty(name)) {
            product.setName(name);
        }
        if (description != null) {
            product.setDescription(description);
        }
        if (price != null) {
            if (!ValidationUtils.isPositive(price)) {
                throw new BusinessException("INVALID_PRICE", "Price must be positive");
            }
            product.setPrice(price);
        }
        
        product.setUpdatedBy("system");
        product.updateTimestamp();
        
        logger.info("Product updated successfully: {}", product.getId());
        return product;
    }
    
    /**
     * Updates product stock.
     */
    public Product updateStock(Long productId, int quantity) throws BusinessException {
        Product product = findById(productId)
            .orElseThrow(() -> new BusinessException("PRODUCT_NOT_FOUND", "Product not found with ID: " + productId));
        
        if (quantity < 0) {
            throw new BusinessException("INVALID_QUANTITY", "Stock quantity cannot be negative");
        }
        
        product.setStockQuantity(quantity);
        product.setUpdatedBy("system");
        product.updateTimestamp();
        
        logger.info("Stock updated for product {}: new quantity = {}", productId, quantity);
        return product;
    }
    
    /**
     * Adds stock to a product.
     */
    public Product addStock(Long productId, int quantity) throws BusinessException {
        Product product = findById(productId)
            .orElseThrow(() -> new BusinessException("PRODUCT_NOT_FOUND", "Product not found with ID: " + productId));
        
        if (quantity <= 0) {
            throw new BusinessException("INVALID_QUANTITY", "Quantity to add must be positive");
        }
        
        product.addStock(quantity);
        product.setUpdatedBy("system");
        
        logger.info("Added {} units to product {}: new quantity = {}", quantity, productId, product.getStockQuantity());
        return product;
    }
    
    /**
     * Removes stock from a product.
     */
    public Product removeStock(Long productId, int quantity) throws BusinessException {
        Product product = findById(productId)
            .orElseThrow(() -> new BusinessException("PRODUCT_NOT_FOUND", "Product not found with ID: " + productId));
        
        if (quantity <= 0) {
            throw new BusinessException("INVALID_QUANTITY", "Quantity to remove must be positive");
        }
        
        if (!product.removeStock(quantity)) {
            throw new BusinessException("INSUFFICIENT_STOCK", 
                "Insufficient stock. Available: " + product.getStockQuantity() + ", Requested: " + quantity);
        }
        
        product.setUpdatedBy("system");
        
        logger.info("Removed {} units from product {}: new quantity = {}", quantity, productId, product.getStockQuantity());
        return product;
    }
    
    /**
     * Activates a product.
     */
    public Product activateProduct(Long productId) throws BusinessException {
        Product product = findById(productId)
            .orElseThrow(() -> new BusinessException("PRODUCT_NOT_FOUND", "Product not found with ID: " + productId));
        
        product.activate();
        product.setUpdatedBy("system");
        
        logger.info("Product activated: {}", productId);
        return product;
    }
    
    /**
     * Deactivates a product.
     */
    public Product deactivateProduct(Long productId) throws BusinessException {
        Product product = findById(productId)
            .orElseThrow(() -> new BusinessException("PRODUCT_NOT_FOUND", "Product not found with ID: " + productId));
        
        product.deactivate();
        product.setUpdatedBy("system");
        
        logger.info("Product deactivated: {}", productId);
        return product;
    }
    
    /**
     * Gets all active products.
     */
    public List<Product> getActiveProducts() {
        return products.values().stream()
            .filter(Product::isActive)
            .collect(Collectors.toList());
    }
    
    /**
     * Gets all available products (active and in stock).
     */
    public List<Product> getAvailableProducts() {
        return products.values().stream()
            .filter(Product::isAvailable)
            .collect(Collectors.toList());
    }
    
    /**
     * Gets products by category.
     */
    public List<Product> getProductsByCategory(ProductCategory category) {
        if (category == null) {
            return new ArrayList<>();
        }
        
        return products.values().stream()
            .filter(product -> category.equals(product.getCategory()))
            .collect(Collectors.toList());
    }
    
    /**
     * Gets total product count.
     */
    public long getProductCount() {
        return products.size();
    }
    
    private void validateProductInput(String name, String sku, BigDecimal price, ProductCategory category) 
            throws BusinessException {
        
        if (!ValidationUtils.isNotEmpty(name)) {
            throw new BusinessException("INVALID_NAME", "Product name cannot be empty");
        }
        if (!ValidationUtils.isNotEmpty(sku)) {
            throw new BusinessException("INVALID_SKU", "Product SKU cannot be empty");
        }
        if (price == null || !ValidationUtils.isPositive(price)) {
            throw new BusinessException("INVALID_PRICE", "Product price must be positive");
        }
        if (category == null) {
            throw new BusinessException("INVALID_CATEGORY", "Product category cannot be null");
        }
    }
    
    private void checkSkuAvailability(String sku) throws BusinessException {
        if (productsBySku.containsKey(sku.toUpperCase())) {
            throw new BusinessException("SKU_EXISTS", "SKU already exists: " + sku);
        }
    }
}
