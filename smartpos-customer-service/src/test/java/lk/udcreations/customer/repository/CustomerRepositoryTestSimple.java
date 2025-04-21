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
import org.springframework.test.context.TestPropertySource;

import lk.udcreations.customer.entity.Customer;

@DataJpaTest
@TestPropertySource(properties = {
    "spring.jpa.hibernate.ddl-auto=create-drop",
    "spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE",
    "spring.datasource.driverClassName=org.h2.Driver",
    "spring.datasource.username=sa",
    "spring.datasource.password=",
    "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect"
})
class CustomerRepositoryTestSimple {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private CustomerRepository customerRepository;

    private Customer createAndPersistCustomer(String username, String firstName, String lastName, String email, boolean deleted) {
        Customer customer = new Customer();
        customer.setCustomerGroupId(1);
        customer.setUsername(username);
        customer.setFirstName(firstName);
        customer.setLastName(lastName);
        customer.setEmail(email);
        customer.setPhoneNo1("1234567890");
        customer.setEnabled(true);
        customer.setDeleted(deleted);
        customer.setCreatedAt(LocalDateTime.now());
        customer.setUpdatedAt(LocalDateTime.now());
        if (deleted) {
            customer.setDeletedAt(LocalDateTime.now());
            customer.setDeletedUserId(1);
        }
        customer.setCreatedUserId(1);
        customer.setUpdatedUserId(1);
        
        return entityManager.persist(customer);
    }

    @Test
    void testFindByUsernameAndDeletedFalse() {
        // Create test data
        createAndPersistCustomer("john_doe", "John", "Doe", "john.doe@example.com", false);
        createAndPersistCustomer("deleted_user", "Deleted", "User", "deleted.user@example.com", true);
        
        // Execute the query
        Optional<Customer> customer = customerRepository.findByUsernameAndDeletedFalse("john_doe");
        
        // Verify results
        assertTrue(customer.isPresent());
        assertEquals("john_doe", customer.get().getUsername());
        assertEquals("John", customer.get().getFirstName());
        assertEquals("Doe", customer.get().getLastName());
    }

    @Test
    void testFindByUsernameAndDeletedTrue() {
        // Create test data
        createAndPersistCustomer("john_doe", "John", "Doe", "john.doe@example.com", false);
        createAndPersistCustomer("deleted_user", "Deleted", "User", "deleted.user@example.com", true);
        
        // Execute the query
        Optional<Customer> customer = customerRepository.findByUsernameAndDeletedTrue("deleted_user");
        
        // Verify results
        assertTrue(customer.isPresent());
        assertEquals("deleted_user", customer.get().getUsername());
        assertTrue(customer.get().isDeleted());
    }

    @Test
    void testFindByFirstName() {
        // Create test data
        createAndPersistCustomer("john_doe", "John", "Doe", "john.doe@example.com", false);
        createAndPersistCustomer("jane_smith", "Jane", "Smith", "jane.smith@example.com", false);
        
        // Execute the query
        Optional<Customer> customer = customerRepository.findByFirstName("John");
        
        // Verify results
        assertTrue(customer.isPresent());
        assertEquals("John", customer.get().getFirstName());
        assertEquals("Doe", customer.get().getLastName());
    }

    @Test
    void testFindByLastName() {
        // Create test data
        createAndPersistCustomer("john_doe", "John", "Doe", "john.doe@example.com", false);
        createAndPersistCustomer("jane_smith", "Jane", "Smith", "jane.smith@example.com", false);
        
        // Execute the query
        Optional<Customer> customer = customerRepository.findByLastName("Smith");
        
        // Verify results
        assertTrue(customer.isPresent());
        assertEquals("Jane", customer.get().getFirstName());
        assertEquals("Smith", customer.get().getLastName());
    }

    @Test
    void testFindByFirstNameAndLastName() {
        // Create test data
        createAndPersistCustomer("john_doe", "John", "Doe", "john.doe@example.com", false);
        createAndPersistCustomer("jane_smith", "Jane", "Smith", "jane.smith@example.com", false);
        
        // Execute the query
        Optional<Customer> customer = customerRepository.findByFirstNameAndLastName("John", "Doe");
        
        // Verify results
        assertTrue(customer.isPresent());
        assertEquals("John", customer.get().getFirstName());
        assertEquals("Doe", customer.get().getLastName());
    }

    @Test
    void testFindByEmail() {
        // Create test data
        createAndPersistCustomer("john_doe", "John", "Doe", "john.doe@example.com", false);
        
        // Execute the query
        Optional<Customer> customer = customerRepository.findByEmail("john.doe@example.com");
        
        // Verify results
        assertTrue(customer.isPresent());
        assertEquals("john_doe", customer.get().getUsername());
        assertEquals("john.doe@example.com", customer.get().getEmail());
    }

    @Test
    void testFindByDeletedFalse() {
        // Create test data
        createAndPersistCustomer("john_doe", "John", "Doe", "john.doe@example.com", false);
        createAndPersistCustomer("jane_smith", "Jane", "Smith", "jane.smith@example.com", false);
        createAndPersistCustomer("deleted_user", "Deleted", "User", "deleted.user@example.com", true);
        
        // Execute the query
        List<Customer> activeCustomers = customerRepository.findByDeletedFalse();
        
        // Verify results
        assertEquals(2, activeCustomers.size());
        assertTrue(activeCustomers.stream().anyMatch(c -> c.getUsername().equals("john_doe")));
        assertTrue(activeCustomers.stream().anyMatch(c -> c.getUsername().equals("jane_smith")));
        assertFalse(activeCustomers.stream().anyMatch(c -> c.getUsername().equals("deleted_user")));
    }

    @Test
    void testExistsByEmail() {
        // Create test data
        createAndPersistCustomer("john_doe", "John", "Doe", "john.doe@example.com", false);
        
        // Execute the query and verify results
        assertTrue(customerRepository.existsByEmail("john.doe@example.com"));
        assertFalse(customerRepository.existsByEmail("nonexistent@example.com"));
    }

    @Test
    void testExistsByUsername() {
        // Create test data
        createAndPersistCustomer("john_doe", "John", "Doe", "john.doe@example.com", false);
        
        // Execute the query and verify results
        assertTrue(customerRepository.existsByUsername("john_doe"));
        assertFalse(customerRepository.existsByUsername("nonexistent_user"));
    }

    @Test
    void testFindByUsername() {
        // Create test data
        createAndPersistCustomer("john_doe", "John", "Doe", "john.doe@example.com", false);
        
        // Execute the query
        Optional<Customer> customer = customerRepository.findByUsername("john_doe");
        
        // Verify results
        assertTrue(customer.isPresent());
        assertEquals("john_doe", customer.get().getUsername());
        assertEquals("John", customer.get().getFirstName());
        assertEquals("Doe", customer.get().getLastName());
    }
}