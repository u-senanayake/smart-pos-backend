package lk.udcreations.customer.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import lk.udcreations.customer.entity.CustomerGroup;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.ANY)
@ContextConfiguration(classes = CustomerGroupRepositoryTest.TestConfig.class)
@TestPropertySource(properties = {
    "spring.jpa.hibernate.ddl-auto=create-drop",
    "spring.sql.init.mode=never",
    "spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE",
    "spring.datasource.driverClassName=org.h2.Driver",
    "spring.datasource.username=sa",
    "spring.datasource.password=",
    "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect"
})
class CustomerGroupRepositoryTest {

    @Configuration
    @EnableAutoConfiguration
    @EnableJpaRepositories(basePackages = "lk.udcreations.customer.repository")
    @EntityScan(basePackages = "lk.udcreations.customer.entity")
    static class TestConfig {
        // This is a minimal configuration for repository tests
    }

    @Autowired
    private CustomerGroupRepository customerGroupRepository;

    @AfterEach
    void cleanup() {
        customerGroupRepository.deleteAll();
    }

    private CustomerGroup createCustomerGroup(String name, String description, boolean deleted) {
        CustomerGroup group = new CustomerGroup();
        group.setName(name);
        group.setDescription(description);
        group.setEnabled(true);
        group.setDeleted(deleted);
        group.setCreatedAt(LocalDateTime.now());
        group.setUpdatedAt(LocalDateTime.now());
        if (deleted) {
            group.setDeletedAt(LocalDateTime.now());
            group.setDeletedUser(1);
        }
        group.setCreatedUser(1);
        group.setUpdatedUser(1);

        return customerGroupRepository.save(group);
    }

    @Test
    void testFindByDeletedFalse() {
        // Create test data
        createCustomerGroup("VIP Customers", "High-value customers", false);
        createCustomerGroup("Regular Customers", "Normal customers", false);
        createCustomerGroup("Deleted Group", "This group is deleted", true);

        // Execute the query
        List<CustomerGroup> activeGroups = customerGroupRepository.findByDeletedFalse();

        // Verify results
        assertEquals(2, activeGroups.size());
        assertTrue(activeGroups.stream().anyMatch(group -> group.getName().equals("VIP Customers")));
        assertTrue(activeGroups.stream().anyMatch(group -> group.getName().equals("Regular Customers")));
        assertFalse(activeGroups.stream().anyMatch(group -> group.getName().equals("Deleted Group")));
    }

    @Test
    void testFindByNameAndDeletedFalse() {
        // Create test data
        createCustomerGroup("VIP Customers", "High-value customers", false);
        createCustomerGroup("Regular Customers", "Normal customers", false);

        // Execute the query
        Optional<CustomerGroup> group = customerGroupRepository.findByNameAndDeletedFalse("VIP Customers");

        // Verify results
        assertTrue(group.isPresent());
        assertEquals("VIP Customers", group.get().getName());
        assertEquals("High-value customers", group.get().getDescription());
    }

    @Test
    void testFindByNameAndDeletedFalse_NotFound() {
        // Create test data
        createCustomerGroup("VIP Customers", "High-value customers", false);

        // Execute the query
        Optional<CustomerGroup> group = customerGroupRepository.findByNameAndDeletedFalse("NonExistentGroup");

        // Verify results
        assertFalse(group.isPresent());
    }

    @Test
    void testFindByNameAndDeletedTrue() {
        // Create test data
        createCustomerGroup("VIP Customers", "High-value customers", false);
        createCustomerGroup("Deleted Group", "This group is deleted", true);

        // Execute the query
        Optional<CustomerGroup> group = customerGroupRepository.findByNameAndDeletedTrue("Deleted Group");

        // Verify results
        assertTrue(group.isPresent());
        assertEquals("Deleted Group", group.get().getName());
        assertTrue(group.get().isDeleted());
    }

    @Test
    void testFindByNameAndDeletedTrue_NotFound() {
        // Create test data
        createCustomerGroup("VIP Customers", "High-value customers", false);
        createCustomerGroup("Deleted Group", "This group is deleted", true);

        // Execute the query
        Optional<CustomerGroup> group = customerGroupRepository.findByNameAndDeletedTrue("VIP Customers");

        // Verify results
        assertFalse(group.isPresent());
    }
}
