package lk.udcreations.product.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
import lk.udcreations.product.entity.Brand;

@DataJpaTest
@ActiveProfiles("test")
@ContextConfiguration(classes = RepositoryTestConfig.class)
@AutoConfigureTestDatabase(replace = Replace.NONE)
class BrandRepositoryTest {

    @Autowired
    private BrandRepository brandRepository;

    @AfterEach
    void cleanup() {
        brandRepository.deleteAll();
    }

    @Test
    void testFindByDeletedFalse() {
        // Get initial count of non-deleted brands
        List<Brand> initialActiveBrands = brandRepository.findByDeletedFalse();
        int initialCount = initialActiveBrands.size();

        // Create test brands
        Brand brand1 = new Brand();
        brand1.setName("TestBrand1");
        brand1.setDescription("Test Brand 1 Description");
        brand1.setEnabled(true);
        brand1.setDeleted(false);

        Brand brand2 = new Brand();
        brand2.setName("TestBrand2");
        brand2.setDescription("Test Brand 2 Description");
        brand2.setEnabled(true);
        brand2.setDeleted(false);

        Brand brand3 = new Brand();
        brand3.setName("TestDeletedBrand");
        brand3.setDescription("Test Deleted Brand Description");
        brand3.setEnabled(true);
        brand3.setDeleted(true);

        // Save brands to repository
        brandRepository.save(brand1);
        brandRepository.save(brand2);
        brandRepository.save(brand3);

        // Test findByDeletedFalse method
        List<Brand> activeBrands = brandRepository.findByDeletedFalse();

        // Verify results
        assertEquals(initialCount + 2, activeBrands.size());
        assertTrue(activeBrands.stream().anyMatch(brand -> brand.getName().equals("TestBrand1")));
        assertTrue(activeBrands.stream().anyMatch(brand -> brand.getName().equals("TestBrand2")));
        assertFalse(activeBrands.stream().anyMatch(brand -> brand.getName().equals("TestDeletedBrand")));
    }

    @Test
    void testFindByNameAndDeletedFalse() {
        // Create test brand
        Brand brand = new Brand();
        brand.setName("ActiveBrand");
        brand.setDescription("Active Brand Description");
        brand.setEnabled(true);
        brand.setDeleted(false);

        // Save brand to repository
        brandRepository.save(brand);

        // Test findByNameAndDeletedFalse method
        Optional<Brand> foundBrand = brandRepository.findByNameAndDeletedFalse("ActiveBrand");

        // Verify results
        assertTrue(foundBrand.isPresent());
        assertEquals("ActiveBrand", foundBrand.get().getName());
    }

    @Test
    void testFindByNameAndDeletedFalse_NotFound() {
        // Test findByNameAndDeletedFalse method with non-existent brand
        Optional<Brand> foundBrand = brandRepository.findByNameAndDeletedFalse("NonExistentBrand");

        // Verify results
        assertFalse(foundBrand.isPresent());
    }

    @Test
    void testFindByNameAndDeletedTrue() {
        // Create test brand
        Brand brand = new Brand();
        brand.setName("TestDeletedBrand");
        brand.setDescription("Test Deleted Brand Description");
        brand.setEnabled(true);
        brand.setDeleted(true);

        // Save brand to repository
        brandRepository.save(brand);

        // Test findByNameAndDeletedTrue method
        Optional<Brand> foundBrand = brandRepository.findByNameAndDeletedTrue("TestDeletedBrand");

        // Verify results
        assertTrue(foundBrand.isPresent());
        assertEquals("TestDeletedBrand", foundBrand.get().getName());
    }

    @Test
    void testFindByNameAndDeletedTrue_NotFound() {
        // Test findByNameAndDeletedTrue method with non-existent brand
        Optional<Brand> foundBrand = brandRepository.findByNameAndDeletedTrue("NonExistentBrand");

        // Verify results
        assertFalse(foundBrand.isPresent());
    }
}
