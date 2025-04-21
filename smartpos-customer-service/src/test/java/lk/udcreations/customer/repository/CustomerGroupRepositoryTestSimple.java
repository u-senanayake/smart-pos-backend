package lk.udcreations.customer.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;

import lk.udcreations.customer.entity.CustomerGroup;

@DataJpaTest
@TestPropertySource(properties = {
    "spring.jpa.hibernate.ddl-auto=create-drop",
    "spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE",
    "spring.datasource.driverClassName=org.h2.Driver",
    "spring.datasource.username=sa",
    "spring.datasource.password=",
    "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect"
})
class CustomerGroupRepositoryTestSimple {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private CustomerGroupRepository customerGroupRepository;

    private CustomerGroup createAndPersistCustomerGroup(String name, String description, boolean deleted) {
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
        
        return entityManager.persist(group);
    }

    @Test
    void testFindByDeletedFalse() {
        // Create test data
        createAndPersistCustomerGroup("VIP Customers", "High-value customers", false);
        createAndPersistCustomerGroup("Regular Customers", "Normal customers", false);
        createAndPersistCustomerGroup("Deleted Group", "This group is deleted", true);
        
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
        createAndPersistCustomerGroup("VIP Customers", "High-value customers", false);
        createAndPersistCustomerGroup("Regular Customers", "Normal customers", false);
        
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
        createAndPersistCustomerGroup("VIP Customers", "High-value customers", false);
        
        // Execute the query
        Optional<CustomerGroup> group = customerGroupRepository.findByNameAndDeletedFalse("NonExistentGroup");
        
        // Verify results
        assertFalse(group.isPresent());
    }

    @Test
    void testFindByNameAndDeletedTrue() {
        // Create test data
        createAndPersistCustomerGroup("VIP Customers", "High-value customers", false);
        createAndPersistCustomerGroup("Deleted Group", "This group is deleted", true);
        
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
        createAndPersistCustomerGroup("VIP Customers", "High-value customers", false);
        createAndPersistCustomerGroup("Deleted Group", "This group is deleted", true);
        
        // Execute the query
        Optional<CustomerGroup> group = customerGroupRepository.findByNameAndDeletedTrue("VIP Customers");
        
        // Verify results
        assertFalse(group.isPresent());
    }
    
    @Test
    void testSaveAndFindById() {
        // Create a new customer group
        CustomerGroup newGroup = new CustomerGroup();
        newGroup.setName("Test Group");
        newGroup.setDescription("Test Description");
        newGroup.setEnabled(true);
        newGroup.setDeleted(false);
        newGroup.setCreatedUser(1);
        newGroup.setUpdatedUser(1);
        
        // Save the group
        CustomerGroup savedGroup = customerGroupRepository.save(newGroup);
        
        // Verify the group was saved with an ID
        assertTrue(savedGroup.getCustomerGroupId() > 0);
        
        // Find the group by ID
        Optional<CustomerGroup> foundGroup = customerGroupRepository.findById(savedGroup.getCustomerGroupId());
        
        // Verify results
        assertTrue(foundGroup.isPresent());
        assertEquals("Test Group", foundGroup.get().getName());
        assertEquals("Test Description", foundGroup.get().getDescription());
    }
}