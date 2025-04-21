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
import org.springframework.test.context.ActiveProfiles;

import lk.udcreations.customer.entity.Customer;

@DataJpaTest
@ActiveProfiles("test")
class CustomerRepositoryTest {

    @Autowired
    private CustomerRepository customerRepository;

    @Test
    void testFindByUsernameAndDeletedFalse() {
        Optional<Customer> customer = customerRepository.findByUsernameAndDeletedFalse("john_doe");

        assertTrue(customer.isPresent());
        assertEquals("john_doe", customer.get().getUsername());
        assertEquals("John", customer.get().getFirstName());
        assertEquals("Doe", customer.get().getLastName());
    }

    @Test
    void testFindByUsernameAndDeletedFalse_NotFound() {
        Optional<Customer> customer = customerRepository.findByUsernameAndDeletedFalse("nonexistent_user");

        assertFalse(customer.isPresent());
    }

    @Test
    void testFindByUsernameAndDeletedTrue() {
        // Create a deleted customer for testing
        Customer deletedCustomer = new Customer();
        deletedCustomer.setCustomerGroupId(1);
        deletedCustomer.setUsername("deleted_user");
        deletedCustomer.setFirstName("Deleted");
        deletedCustomer.setLastName("User");
        deletedCustomer.setEmail("deleted.user@example.com");
        deletedCustomer.setPhoneNo1("1112223333");
        deletedCustomer.setEnabled(true);
        deletedCustomer.setDeleted(true);
        deletedCustomer.setCreatedUserId(1);
        deletedCustomer.setUpdatedUserId(1);
        deletedCustomer.setDeletedUserId(1);

        customerRepository.save(deletedCustomer);

        Optional<Customer> foundCustomer = customerRepository.findByUsernameAndDeletedTrue("deleted_user");

        assertTrue(foundCustomer.isPresent());
        assertEquals("deleted_user", foundCustomer.get().getUsername());
        assertTrue(foundCustomer.get().isDeleted());
    }

    @Test
    void testFindByFirstName() {
        Optional<Customer> customer = customerRepository.findByFirstName("John");

        assertTrue(customer.isPresent());
        assertEquals("John", customer.get().getFirstName());
        assertEquals("Doe", customer.get().getLastName());
    }

    @Test
    void testFindByLastName() {
        Optional<Customer> customer = customerRepository.findByLastName("Doe");

        assertTrue(customer.isPresent());
        assertEquals("John", customer.get().getFirstName());
        assertEquals("Doe", customer.get().getLastName());
    }

    @Test
    void testFindByFirstNameAndLastName() {
        Optional<Customer> customer = customerRepository.findByFirstNameAndLastName("John", "Doe");

        assertTrue(customer.isPresent());
        assertEquals("John", customer.get().getFirstName());
        assertEquals("Doe", customer.get().getLastName());
    }

    @Test
    void testFindByEmail() {
        Optional<Customer> customer = customerRepository.findByEmail("john.doe@example.com");

        assertTrue(customer.isPresent());
        assertEquals("john_doe", customer.get().getUsername());
        assertEquals("john.doe@example.com", customer.get().getEmail());
    }

    @Test
    void testFindByDeletedFalse() {
        List<Customer> activeCustomers = customerRepository.findByDeletedFalse();

        // There should be 5 active customers in data.sql
        assertEquals(5, activeCustomers.size());

        // Verify some of the expected customers are present
        assertTrue(activeCustomers.stream().anyMatch(c -> c.getUsername().equals("john_doe")));
        assertTrue(activeCustomers.stream().anyMatch(c -> c.getUsername().equals("jane_smith")));
        assertTrue(activeCustomers.stream().anyMatch(c -> c.getUsername().equals("michael_brown")));
    }

    @Test
    void testFindByCustomerIdAndDeletedFalse() {
        // Find a customer by username first to get their ID
        Optional<Customer> johnDoe = customerRepository.findByUsername("john_doe");
        assertTrue(johnDoe.isPresent());

        Integer johnDoeId = johnDoe.get().getCustomerId();

        // Now find by ID and deleted=false
        Optional<Customer> customer = customerRepository.findByCustomerIdAndDeletedFalse(johnDoeId);

        assertTrue(customer.isPresent());
        assertEquals("john_doe", customer.get().getUsername());
        assertEquals("John", customer.get().getFirstName());
    }

    @Test
    void testExistsByEmail() {
        assertTrue(customerRepository.existsByEmail("john.doe@example.com"));
        assertFalse(customerRepository.existsByEmail("nonexistent@example.com"));
    }

    @Test
    void testExistsByUsername() {
        assertTrue(customerRepository.existsByUsername("john_doe"));
        assertFalse(customerRepository.existsByUsername("nonexistent_user"));
    }

    @Test
    void testFindByUsername() {
        Optional<Customer> customer = customerRepository.findByUsername("john_doe");

        assertTrue(customer.isPresent());
        assertEquals("john_doe", customer.get().getUsername());
        assertEquals("John", customer.get().getFirstName());
        assertEquals("Doe", customer.get().getLastName());
    }

    @Test
    void testSaveAndFindById() {
        // Create a new customer
        Customer newCustomer = new Customer();
        newCustomer.setCustomerGroupId(1);
        newCustomer.setUsername("test_user");
        newCustomer.setFirstName("Test");
        newCustomer.setLastName("User");
        newCustomer.setEmail("test.user@example.com");
        newCustomer.setPhoneNo1("9998887777");
        newCustomer.setAddress("Test Address");
        newCustomer.setEnabled(true);
        newCustomer.setLocked(false);
        newCustomer.setDeleted(false);
        newCustomer.setCreatedUserId(1);
        newCustomer.setUpdatedUserId(1);

        // Save the customer
        Customer savedCustomer = customerRepository.save(newCustomer);

        // Verify the customer was saved with an ID
        assertTrue(savedCustomer.getCustomerId() > 0);

        // Find the customer by ID
        Optional<Customer> foundCustomer = customerRepository.findById(savedCustomer.getCustomerId());

        assertTrue(foundCustomer.isPresent());
        assertEquals("test_user", foundCustomer.get().getUsername());
        assertEquals("Test", foundCustomer.get().getFirstName());
        assertEquals("User", foundCustomer.get().getLastName());
        assertEquals("test.user@example.com", foundCustomer.get().getEmail());
    }
}
