package lk.udcreations.product.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.ActiveProfiles;

import lk.udcreations.product.config.RepositoryTestConfig;
import lk.udcreations.product.entity.Product;

@DataJpaTest
@ActiveProfiles("test")
@ContextConfiguration(classes = RepositoryTestConfig.class)
@AutoConfigureTestDatabase(replace = Replace.NONE)
class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    @AfterEach
    void cleanup() {
        productRepository.deleteAll();
    }

    @Test
    void testFindByDeletedFalse() {
        // Get initial count of non-deleted products
        List<Product> initialActiveProducts = productRepository.findByDeletedFalse();
        int initialCount = initialActiveProducts.size();

        // Create test products
        Product product1 = new Product();
        product1.setProductId("PROD001_1");
        product1.setSku("SKU001_1");
        product1.setProductName("TestProduct1");
        product1.setDescription("Test Product 1 Description");
        product1.setCategoryId(1);
        product1.setDistributorId(1);
        product1.setPrice(new BigDecimal("10.99"));
        product1.setCostPrice(new BigDecimal("8.99"));
        product1.setMinPrice(new BigDecimal("9.99"));
        product1.setManufactureDate(LocalDate.now().minusDays(30));
        product1.setExpireDate(LocalDate.now().plusDays(365));
        product1.setEnabled(true);
        product1.setDeleted(false);

        Product product2 = new Product();
        product2.setProductId("PROD002_1");
        product2.setSku("SKU002_1");
        product2.setProductName("TestProduct2");
        product2.setDescription("Test Product 2 Description");
        product2.setCategoryId(1);
        product2.setDistributorId(1);
        product2.setPrice(new BigDecimal("20.99"));
        product2.setCostPrice(new BigDecimal("15.99"));
        product2.setMinPrice(new BigDecimal("18.99"));
        product2.setManufactureDate(LocalDate.now().minusDays(15));
        product2.setExpireDate(LocalDate.now().plusDays(180));
        product2.setEnabled(true);
        product2.setDeleted(false);

        Product product3 = new Product();
        product3.setProductId("PROD003_1");
        product3.setSku("SKU003_1");
        product3.setProductName("TestDeletedProduct");
        product3.setDescription("Test Deleted Product Description");
        product3.setCategoryId(2);
        product3.setDistributorId(2);
        product3.setPrice(new BigDecimal("30.99"));
        product3.setCostPrice(new BigDecimal("25.99"));
        product3.setMinPrice(new BigDecimal("28.99"));
        product3.setManufactureDate(LocalDate.now().minusDays(45));
        product3.setExpireDate(LocalDate.now().plusDays(90));
        product3.setEnabled(true);
        product3.setDeleted(true);

        // Save products to repository
        productRepository.save(product1);
        productRepository.save(product2);
        productRepository.save(product3);

        // Test findByDeletedFalse method
        List<Product> activeProducts = productRepository.findByDeletedFalse();

        // Verify results
        assertEquals(initialCount + 2, activeProducts.size());
        assertTrue(activeProducts.stream().anyMatch(product -> product.getProductName().equals("TestProduct1")));
        assertTrue(activeProducts.stream().anyMatch(product -> product.getProductName().equals("TestProduct2")));
        assertFalse(activeProducts.stream().anyMatch(product -> product.getProductName().equals("TestDeletedProduct")));
    }

    @Test
    void testFindByProductNameAndDeletedFalse() {
        // Create test product
        Product product = new Product();
        product.setProductId("PROD001_2");
        product.setSku("SKU001_2");
        product.setProductName("ActiveProduct");
        product.setDescription("Active Product Description");
        product.setCategoryId(1);
        product.setDistributorId(1);
        product.setPrice(new BigDecimal("10.99"));
        product.setCostPrice(new BigDecimal("8.99"));
        product.setMinPrice(new BigDecimal("9.99"));
        product.setEnabled(true);
        product.setDeleted(false);

        // Save product to repository
        productRepository.save(product);

        // Test findByProductNameAndDeletedFalse method
        Optional<Product> foundProduct = productRepository.findByProductNameAndDeletedFalse("ActiveProduct");

        // Verify results
        assertTrue(foundProduct.isPresent());
        assertEquals("ActiveProduct", foundProduct.get().getProductName());
    }

    @Test
    void testFindByProductNameAndDeletedFalse_NotFound() {
        // Test findByProductNameAndDeletedFalse method with non-existent product
        Optional<Product> foundProduct = productRepository.findByProductNameAndDeletedFalse("NonExistentProduct");

        // Verify results
        assertFalse(foundProduct.isPresent());
    }

    @Test
    void testFindByProductNameAndDeletedTrue() {
        // Create test product
        Product product = new Product();
        product.setProductId("PROD001_3");
        product.setSku("SKU001_3");
        product.setProductName("DeletedProduct");
        product.setDescription("Deleted Product Description");
        product.setCategoryId(1);
        product.setDistributorId(1);
        product.setPrice(new BigDecimal("10.99"));
        product.setCostPrice(new BigDecimal("8.99"));
        product.setMinPrice(new BigDecimal("9.99"));
        product.setEnabled(true);
        product.setDeleted(true);

        // Save product to repository
        productRepository.save(product);

        // Test findByProductNameAndDeletedTrue method
        Optional<Product> foundProduct = productRepository.findByProductNameAndDeletedTrue("DeletedProduct");

        // Verify results
        assertTrue(foundProduct.isPresent());
        assertEquals("DeletedProduct", foundProduct.get().getProductName());
    }

    @Test
    void testFindByProductNameAndDeletedTrue_NotFound() {
        // Test findByProductNameAndDeletedTrue method with non-existent product
        Optional<Product> foundProduct = productRepository.findByProductNameAndDeletedTrue("NonExistentProduct");

        // Verify results
        assertFalse(foundProduct.isPresent());
    }

    @Test
    void testFindByIdAndDeletedTrue() {
        // Create test product
        Product product = new Product();
        product.setProductId("PROD001_4");
        product.setSku("SKU001_4");
        product.setProductName("DeletedProductById");
        product.setDescription("Deleted Product By ID Description");
        product.setCategoryId(1);
        product.setDistributorId(1);
        product.setPrice(new BigDecimal("10.99"));
        product.setCostPrice(new BigDecimal("8.99"));
        product.setMinPrice(new BigDecimal("9.99"));
        product.setEnabled(true);
        product.setDeleted(true);

        // Save product to repository
        Product savedProduct = productRepository.save(product);

        // Test findByIdAndDeletedTrue method
        Optional<Product> foundProduct = productRepository.findByIdAndDeletedTrue(savedProduct.getId());

        // Verify results
        assertTrue(foundProduct.isPresent());
        assertEquals("DeletedProductById", foundProduct.get().getProductName());
    }

    @Test
    void testFindByIdAndDeletedTrue_NotFound() {
        // Test findByIdAndDeletedTrue method with non-existent product
        Optional<Product> foundProduct = productRepository.findByIdAndDeletedTrue(999);

        // Verify results
        assertFalse(foundProduct.isPresent());
    }

    @Test
    void testFindByProductIdAndDeletedTrue() {
        // Create test product
        Product product = new Product();
        product.setProductId("PROD001_5");
        product.setSku("SKU001_5");
        product.setProductName("DeletedProductByProductId");
        product.setDescription("Deleted Product By Product ID Description");
        product.setCategoryId(1);
        product.setDistributorId(1);
        product.setPrice(new BigDecimal("10.99"));
        product.setCostPrice(new BigDecimal("8.99"));
        product.setMinPrice(new BigDecimal("9.99"));
        product.setEnabled(true);
        product.setDeleted(true);

        // Save product to repository
        productRepository.save(product);

        // Test findByProductIdAndDeletedTrue method
        Optional<Product> foundProduct = productRepository.findByProductIdAndDeletedTrue("PROD001_5");

        // Verify results
        assertTrue(foundProduct.isPresent());
        assertEquals("DeletedProductByProductId", foundProduct.get().getProductName());
    }

    @Test
    void testFindByProductIdAndDeletedTrue_NotFound() {
        // Test findByProductIdAndDeletedTrue method with non-existent product
        Optional<Product> foundProduct = productRepository.findByProductIdAndDeletedTrue("NONEXISTENT");

        // Verify results
        assertFalse(foundProduct.isPresent());
    }

    @Test
    void testFindByIdAndEnabledTrue() {
        // Create test product
        Product product = new Product();
        product.setProductId("PROD001_6");
        product.setSku("SKU001_6");
        product.setProductName("EnabledProductById");
        product.setDescription("Enabled Product By ID Description");
        product.setCategoryId(1);
        product.setDistributorId(1);
        product.setPrice(new BigDecimal("10.99"));
        product.setCostPrice(new BigDecimal("8.99"));
        product.setMinPrice(new BigDecimal("9.99"));
        product.setEnabled(true);
        product.setDeleted(false);

        // Save product to repository
        Product savedProduct = productRepository.save(product);

        // Test findByIdAndEnabledTrue method
        Optional<Product> foundProduct = productRepository.findByIdAndEnabledTrue(savedProduct.getId());

        // Verify results
        assertTrue(foundProduct.isPresent());
        assertEquals("EnabledProductById", foundProduct.get().getProductName());
    }

    @Test
    void testFindByIdAndEnabledTrue_NotFound() {
        // Test findByIdAndEnabledTrue method with non-existent product
        Optional<Product> foundProduct = productRepository.findByIdAndEnabledTrue(999);

        // Verify results
        assertFalse(foundProduct.isPresent());
    }

    @Test
    void testFindByProductIdAndEnabledTrue() {
        // Create test product
        Product product = new Product();
        product.setProductId("PROD001_7");
        product.setSku("SKU001_7");
        product.setProductName("EnabledProductByProductId");
        product.setDescription("Enabled Product By Product ID Description");
        product.setCategoryId(1);
        product.setDistributorId(1);
        product.setPrice(new BigDecimal("10.99"));
        product.setCostPrice(new BigDecimal("8.99"));
        product.setMinPrice(new BigDecimal("9.99"));
        product.setEnabled(true);
        product.setDeleted(false);

        // Save product to repository
        productRepository.save(product);

        // Test findByProductIdAndEnabledTrue method
        Optional<Product> foundProduct = productRepository.findByProductIdAndEnabledTrue("PROD001_7");

        // Verify results
        assertTrue(foundProduct.isPresent());
        assertEquals("EnabledProductByProductId", foundProduct.get().getProductName());
    }

    @Test
    void testFindByProductIdAndEnabledTrue_NotFound() {
        // Test findByProductIdAndEnabledTrue method with non-existent product
        Optional<Product> foundProduct = productRepository.findByProductIdAndEnabledTrue("NONEXISTENT");

        // Verify results
        assertFalse(foundProduct.isPresent());
    }
}
