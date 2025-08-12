package com.example.jpademo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.TestPropertySource;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Comprehensive test suite for ProductService.
 * Tests both standard JPA repository methods and custom query methods.
 * Contains embedded test configuration properties.
 */
@DataJpaTest
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE",
    "spring.datasource.driverClassName=org.h2.Driver",
    "spring.datasource.username=sa",
    "spring.datasource.password=",
    "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
    "spring.jpa.hibernate.ddl-auto=create-drop",
    "spring.jpa.show-sql=true",
    "spring.jpa.properties.hibernate.format_sql=true",
    "spring.jpa.open-in-view=false",
    "logging.level.org.hibernate.SQL=DEBUG",
    "logging.level.org.springframework.test=INFO"
})
public class ProductServiceTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ProductRepository productRepository;

    // Test data
    private Product laptop;
    private Product smartphone;
    private Product tablet;
    private Product headphones;
    private Product speaker;

    @BeforeEach
    public void setup() {
        // Clear any existing data
        productRepository.deleteAll();
        
        // Create test products
        laptop = new Product(
                "MacBook Pro", 
                "High-performance laptop for professionals", 
                new BigDecimal("1999.99"), 
                "Electronics", 
                50, 
                true);
        
        smartphone = new Product(
                "iPhone 14", 
                "Latest smartphone with advanced features", 
                new BigDecimal("999.99"), 
                "Electronics", 
                100, 
                true);
        
        tablet = new Product(
                "iPad Pro", 
                "Powerful tablet for creative work", 
                new BigDecimal("799.99"), 
                "Electronics", 
                75, 
                true);
        
        headphones = new Product(
                "AirPods Pro", 
                "Wireless noise-cancelling earbuds", 
                new BigDecimal("249.99"), 
                "Audio", 
                25, 
                true);
        
        speaker = new Product(
                "Bluetooth Speaker", 
                "Portable wireless speaker", 
                new BigDecimal("89.99"), 
                "Audio", 
                0, 
                false);
        
        // Persist test data
        entityManager.persist(laptop);
        entityManager.persist(smartphone);
        entityManager.persist(tablet);
        entityManager.persist(headphones);
        entityManager.persist(speaker);
        entityManager.flush();
    }

    // Test standard JPA repository methods
    
    @Test
    public void testFindAll() {
        List<Product> products = productRepository.findAll();
        assertEquals(5, products.size(), "Should find all 5 products");
    }
    
    @Test
    public void testFindById() {
        Optional<Product> foundProduct = productRepository.findById(laptop.getId());
        assertTrue(foundProduct.isPresent(), "Product should be found by ID");
        assertEquals("MacBook Pro", foundProduct.get().getName(), "Product name should match");
    }
    
    @Test
    public void testSave() {
        Product newProduct = new Product(
                "Smart Watch", 
                "Fitness tracking watch", 
                new BigDecimal("199.99"), 
                "Wearables", 
                30, 
                true);
        
        Product savedProduct = productRepository.save(newProduct);
        assertNotNull(savedProduct.getId(), "Saved product should have an ID");
        
        Optional<Product> foundProduct = productRepository.findById(savedProduct.getId());
        assertTrue(foundProduct.isPresent(), "Product should be found after saving");
        assertEquals("Smart Watch", foundProduct.get().getName(), "Product name should match");
    }
    
    @Test
    public void testDelete() {
        productRepository.delete(laptop);
        Optional<Product> foundProduct = productRepository.findById(laptop.getId());
        assertFalse(foundProduct.isPresent(), "Product should be deleted");
    }
    
    // Test derived query methods
    
    @Test
    public void testFindByCategory() {
        List<Product> electronicsProducts = productRepository.findByCategory("Electronics");
        assertEquals(3, electronicsProducts.size(), "Should find 3 electronics products");
        
        List<Product> audioProducts = productRepository.findByCategory("Audio");
        assertEquals(2, audioProducts.size(), "Should find 2 audio products");
        
        List<Product> wearablesProducts = productRepository.findByCategory("Wearables");
        assertEquals(0, wearablesProducts.size(), "Should find 0 wearables products");
    }
    
    @Test
    public void testFindByAvailable() {
        List<Product> availableProducts = productRepository.findByAvailable(true);
        assertEquals(4, availableProducts.size(), "Should find 4 available products");
        
        List<Product> unavailableProducts = productRepository.findByAvailable(false);
        assertEquals(1, unavailableProducts.size(), "Should find 1 unavailable product");
    }
    
    @Test
    public void testFindByNameContainingIgnoreCase() {
        List<Product> proProducts = productRepository.findByNameContainingIgnoreCase("Pro");
        assertEquals(3, proProducts.size(), "Should find 3 products with 'Pro' in the name");
        
        List<Product> podProducts = productRepository.findByNameContainingIgnoreCase("pod");
        assertEquals(1, podProducts.size(), "Should find 1 product with 'pod' in the name");
        
        List<Product> nonExistentProducts = productRepository.findByNameContainingIgnoreCase("nonexistent");
        assertEquals(0, nonExistentProducts.size(), "Should find 0 products with 'nonexistent' in the name");
    }
    
    // Test custom query methods
    
    @Test
    public void testFindByPriceRangeAndCategory() {
        List<Product> products = productRepository.findByPriceRangeAndCategory(
                new BigDecimal("700.00"), 
                new BigDecimal("1500.00"), 
                "Electronics");
        
        assertEquals(2, products.size(), "Should find 2 electronics products in the price range");
        assertTrue(products.stream().anyMatch(p -> p.getName().equals("iPad Pro")), 
                "Should include iPad Pro");
        assertFalse(products.stream().anyMatch(p -> p.getName().equals("MacBook Pro")), 
                "Should not include MacBook Pro (price too high)");
    }
    
    @Test
    public void testFindAvailableProductsWithLowStockByCategory() {
        List<Product> products = productRepository.findAvailableProductsWithLowStockByCategory(30, "Audio");
        
        assertEquals(1, products.size(), "Should find 1 audio product with stock below 30");
        assertEquals("AirPods Pro", products.get(0).getName(), "Should be AirPods Pro");
    }
    
    @Test
    public void testFindTopSellingProductsByCategory() {
        // Create a PageRequest for the first 2 results
        PageRequest pageRequest = PageRequest.of(0, 2);
        
        List<Product> products = productRepository.findTopSellingProductsByCategory("Electronics", pageRequest);
        
        assertEquals(2, products.size(), "Should find exactly 2 electronics products");
        assertEquals("MacBook Pro", products.get(0).getName(), "First product should be MacBook Pro (lowest stock)");
        assertEquals("iPad Pro", products.get(1).getName(), "Second product should be iPad Pro (second lowest stock)");
    }
    
    @Test
    public void testFindByNamePatternNative() {
        List<Product> products = productRepository.findByNamePatternNative("Phone");
        
        assertEquals(1, products.size(), "Should find 1 product matching 'Phone'");
        assertEquals("iPhone 14", products.get(0).getName(), "Should be iPhone 14");
        
        // Test that unavailable products are not returned
        List<Product> speakerProducts = productRepository.findByNamePatternNative("Speaker");
        assertEquals(0, speakerProducts.size(), "Should not find 'Speaker' as it's unavailable");
    }
    
    @Test
    public void testFindMostExpensiveProductInCategory() {
        Optional<Product> product = productRepository.findFirstByCategoryOrderByPriceDesc("Electronics");
        
        assertTrue(product.isPresent(), "Should find most expensive electronics product");
        assertEquals("MacBook Pro", product.get().getName(), "Most expensive should be MacBook Pro");
    }
    
    @Test
    public void testCalculateAveragePriceByCategory() {
        BigDecimal avgPrice = productRepository.calculateAveragePriceByCategory("Electronics");
        
        // Calculate expected average: (1999.99 + 999.99 + 799.99) / 3 = 1266.66
        BigDecimal expected = new BigDecimal("1266.66");
        
        // Use compareTo with a small delta for floating-point comparison
        assertTrue(avgPrice.subtract(expected).abs().compareTo(new BigDecimal("0.01")) < 0,
                "Average price should be approximately 1266.66");
    }
    
    @Test
    public void testCountByCategoryAndPriceRange() {
        Long count = productRepository.countByCategoryAndPriceRange(
                "Electronics", 
                new BigDecimal("500.00"), 
                new BigDecimal("1000.00"));
        
        assertEquals(2L, count, "Should count 2 electronics products in the price range");
    }
    
    // Edge cases and boundary tests
    
    @Test
    public void testEmptyRepository() {
        // Clear all data
        productRepository.deleteAll();
        
        List<Product> products = productRepository.findAll();
        assertEquals(0, products.size(), "Repository should be empty");
        
        Optional<Product> mostExpensive = productRepository.findFirstByCategoryOrderByPriceDesc("Electronics");
        assertFalse(mostExpensive.isPresent(), "Should not find any product in empty repository");
    }
    
    @Test
    public void testExactPriceBoundaries() {
        List<Product> products = productRepository.findByPriceRangeAndCategory(
                new BigDecimal("799.99"), // Exactly iPad Pro's price
                new BigDecimal("999.99"), // Exactly iPhone's price
                "Electronics");
        
        assertEquals(2, products.size(), "Should include products at the exact boundary prices");
        assertTrue(products.stream().anyMatch(p -> p.getName().equals("iPad Pro")), 
                "Should include iPad Pro (at lower boundary)");
        assertTrue(products.stream().anyMatch(p -> p.getName().equals("iPhone 14")), 
                "Should include iPhone 14 (at upper boundary)");
    }
}
