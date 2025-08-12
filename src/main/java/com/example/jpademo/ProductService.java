package com.example.jpademo;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Combined Product entity and repository in a single file for pure Spring approach.
 * Contains both the JPA entity definition and repository interface.
 */

/**
 * Product entity representing a product in the system.
 * This class is mapped to the "products" table in the database.
 */
@Entity
@Table(name = "products")
class Product {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 100)
    private String name;
    
    @Column(length = 500)
    private String description;
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;
    
    @Column(nullable = false)
    private String category;
    
    @Column(name = "stock_quantity", nullable = false)
    private Integer stockQuantity;
    
    @Column(name = "is_available")
    private boolean available;
    
    // Default constructor required by JPA
    public Product() {
    }
    
    // Constructor with fields
    public Product(String name, String description, BigDecimal price, 
                  String category, Integer stockQuantity, boolean available) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.category = category;
        this.stockQuantity = stockQuantity;
        this.available = available;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Integer getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(Integer stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }
    
    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", price=" + price +
                ", category='" + category + '\'' +
                ", stockQuantity=" + stockQuantity +
                ", available=" + available +
                '}';
    }
}

/**
 * Repository interface for Product entity.
 * Extends JpaRepository to inherit standard CRUD operations.
 * Includes custom query methods using JPQL.
 */
@Repository
interface ProductRepository extends JpaRepository<Product, Long> {
    
    // Find products by category
    List<Product> findByCategory(String category);
    
    // Find products by availability
    List<Product> findByAvailable(boolean available);
    
    // Find products by name containing the given string (case insensitive)
    List<Product> findByNameContainingIgnoreCase(String name);
    
    // Custom query to find products within a price range and specific category
    @Query("SELECT p FROM Product p WHERE p.price BETWEEN :minPrice AND :maxPrice AND p.category = :category")
    List<Product> findByPriceRangeAndCategory(
            @Param("minPrice") BigDecimal minPrice, 
            @Param("maxPrice") BigDecimal maxPrice, 
            @Param("category") String category);
    
    // Custom query to find available products with stock below threshold in a specific category
    @Query("SELECT p FROM Product p WHERE p.available = true AND p.stockQuantity < :threshold AND p.category = :category")
    List<Product> findAvailableProductsWithLowStockByCategory(
            @Param("threshold") Integer threshold, 
            @Param("category") String category);
    
    // Custom query to find top selling products (those with lowest stock) in a category
    @Query(value = "SELECT p FROM Product p WHERE p.category = :category AND p.available = true ORDER BY p.stockQuantity ASC", 
           countQuery = "SELECT count(p) FROM Product p WHERE p.category = :category AND p.available = true")
    List<Product> findTopSellingProductsByCategory(
            @Param("category") String category, 
            org.springframework.data.domain.Pageable pageable);
    
    // Custom query with native SQL to find products with name matching pattern
    @Query(value = "SELECT * FROM products p WHERE p.name LIKE %:pattern% AND p.is_available = true", nativeQuery = true)
    List<Product> findByNamePatternNative(@Param("pattern") String pattern);
    
    // Find the most expensive product in a category using derived method name
    Optional<Product> findFirstByCategoryOrderByPriceDesc(String category);
    
    // Custom query to calculate average price by category
    @Query("SELECT AVG(p.price) FROM Product p WHERE p.category = :category")
    BigDecimal calculateAveragePriceByCategory(@Param("category") String category);
    
    // Custom query to count products by category and price range
    @Query("SELECT COUNT(p) FROM Product p WHERE p.category = :category AND p.price BETWEEN :minPrice AND :maxPrice")
    Long countByCategoryAndPriceRange(
            @Param("category") String category, 
            @Param("minPrice") BigDecimal minPrice, 
            @Param("maxPrice") BigDecimal maxPrice);
}
