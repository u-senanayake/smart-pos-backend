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
import lk.udcreations.product.entity.Category;

@DataJpaTest
@ActiveProfiles("test")
@ContextConfiguration(classes = RepositoryTestConfig.class)
@AutoConfigureTestDatabase(replace = Replace.NONE)
class CategoryRepositoryTest {

    @Autowired
    private CategoryRepository categoryRepository;

    @AfterEach
    void cleanup() {
        categoryRepository.deleteAll();
    }

    @Test
    void testFindByDeletedFalse() {
        // Get initial count of non-deleted categories
        List<Category> initialActiveCategories = categoryRepository.findByDeletedFalse();
        int initialCount = initialActiveCategories.size();

        // Create test categories
        Category category1 = new Category();
        category1.setName("TestCategory1");
        category1.setDescription("Test Category 1 Description");
        category1.setCatPrefix("T");
        category1.setEnabled(true);
        category1.setDeleted(false);

        Category category2 = new Category();
        category2.setName("TestCategory2");
        category2.setDescription("Test Category 2 Description");
        category2.setCatPrefix("S");
        category2.setEnabled(true);
        category2.setDeleted(false);

        Category category3 = new Category();
        category3.setName("TestDeletedCategory");
        category3.setDescription("Test Deleted Category Description");
        category3.setCatPrefix("D");
        category3.setEnabled(true);
        category3.setDeleted(true);

        // Save categories to repository
        categoryRepository.save(category1);
        categoryRepository.save(category2);
        categoryRepository.save(category3);

        // Test findByDeletedFalse method
        List<Category> activeCategories = categoryRepository.findByDeletedFalse();

        // Verify results
        assertEquals(initialCount + 2, activeCategories.size());
        assertTrue(activeCategories.stream().anyMatch(category -> category.getName().equals("TestCategory1")));
        assertTrue(activeCategories.stream().anyMatch(category -> category.getName().equals("TestCategory2")));
        assertFalse(activeCategories.stream().anyMatch(category -> category.getName().equals("TestDeletedCategory")));
    }

    @Test
    void testFindByNameAndDeletedFalse() {
        // Create test category
        Category category = new Category();
        category.setName("ActiveCategory");
        category.setDescription("Active Category Description");
        category.setCatPrefix("A");
        category.setEnabled(true);
        category.setDeleted(false);

        // Save category to repository
        categoryRepository.save(category);

        // Test findByNameAndDeletedFalse method
        Optional<Category> foundCategory = categoryRepository.findByNameAndDeletedFalse("ActiveCategory");

        // Verify results
        assertTrue(foundCategory.isPresent());
        assertEquals("ActiveCategory", foundCategory.get().getName());
    }

    @Test
    void testFindByNameAndDeletedFalse_NotFound() {
        // Test findByNameAndDeletedFalse method with non-existent category
        Optional<Category> foundCategory = categoryRepository.findByNameAndDeletedFalse("NonExistentCategory");

        // Verify results
        assertFalse(foundCategory.isPresent());
    }

    @Test
    void testFindByNameAndDeletedTrue() {
        // Create test category
        Category category = new Category();
        category.setName("TestDeletedCategory");
        category.setDescription("Test Deleted Category Description");
        category.setCatPrefix("D");
        category.setEnabled(true);
        category.setDeleted(true);

        // Save category to repository
        categoryRepository.save(category);

        // Test findByNameAndDeletedTrue method
        Optional<Category> foundCategory = categoryRepository.findByNameAndDeletedTrue("TestDeletedCategory");

        // Verify results
        assertTrue(foundCategory.isPresent());
        assertEquals("TestDeletedCategory", foundCategory.get().getName());
    }

    @Test
    void testFindByNameAndDeletedTrue_NotFound() {
        // Test findByNameAndDeletedTrue method with non-existent category
        Optional<Category> foundCategory = categoryRepository.findByNameAndDeletedTrue("NonExistentCategory");

        // Verify results
        assertFalse(foundCategory.isPresent());
    }
}
