package lk.udcreations.customer.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import lk.udcreations.customer.entity.CustomerGroup;

@DataJpaTest
@ActiveProfiles("test")
class CustomerGroupRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private CustomerGroupRepository customerGroupRepository;

    @BeforeEach
    void setUp() {
        // Create and persist test customer groups
        CustomerGroup vipGroup = new CustomerGroup();
        vipGroup.setName("VIP Customers");
        vipGroup.setDescription("Exclusive group for high-value customers");
        vipGroup.setEnabled(true);
        vipGroup.setDeleted(false);
        vipGroup.setCreatedAt(LocalDateTime.now());
        vipGroup.setUpdatedAt(LocalDateTime.now());
        vipGroup.setCreatedUser(1);
        vipGroup.setUpdatedUser(1);
        entityManager.persist(vipGroup);

        CustomerGroup wholesaleGroup = new CustomerGroup();
        wholesaleGroup.setName("Wholesale Buyers");
        wholesaleGroup.setDescription("Customers who buy in bulk with special discounts");
        wholesaleGroup.setEnabled(true);
        wholesaleGroup.setDeleted(false);
        wholesaleGroup.setCreatedAt(LocalDateTime.now());
        wholesaleGroup.setUpdatedAt(LocalDateTime.now());
        wholesaleGroup.setCreatedUser(1);
        wholesaleGroup.setUpdatedUser(1);
        entityManager.persist(wholesaleGroup);

        CustomerGroup retailGroup = new CustomerGroup();
        retailGroup.setName("Retail Customers");
        retailGroup.setDescription("Regular customers purchasing at standard rates");
        retailGroup.setEnabled(true);
        retailGroup.setDeleted(false);
        retailGroup.setCreatedAt(LocalDateTime.now());
        retailGroup.setUpdatedAt(LocalDateTime.now());
        retailGroup.setCreatedUser(1);
        retailGroup.setUpdatedUser(1);
        entityManager.persist(retailGroup);

        CustomerGroup loyaltyGroup = new CustomerGroup();
        loyaltyGroup.setName("Loyalty Members");
        loyaltyGroup.setDescription("Customers enrolled in our loyalty program");
        loyaltyGroup.setEnabled(true);
        loyaltyGroup.setDeleted(false);
        loyaltyGroup.setCreatedAt(LocalDateTime.now());
        loyaltyGroup.setUpdatedAt(LocalDateTime.now());
        loyaltyGroup.setCreatedUser(1);
        loyaltyGroup.setUpdatedUser(1);
        entityManager.persist(loyaltyGroup);

        CustomerGroup corporateGroup = new CustomerGroup();
        corporateGroup.setName("Corporate Clients");
        corporateGroup.setDescription("Businesses with corporate purchase agreements");
        corporateGroup.setEnabled(true);
        corporateGroup.setDeleted(false);
        corporateGroup.setCreatedAt(LocalDateTime.now());
        corporateGroup.setUpdatedAt(LocalDateTime.now());
        corporateGroup.setCreatedUser(1);
        corporateGroup.setUpdatedUser(1);
        entityManager.persist(corporateGroup);

        // Flush the changes to the database
        entityManager.flush();
    }

    @Test
    void testFindByDeletedFalse() {
        // This test uses the data created in setUp()
        List<CustomerGroup> activeGroups = customerGroupRepository.findByDeletedFalse();

        // There should be 5 active customer groups created in setUp()
        assertEquals(5, activeGroups.size());

        // Verify some of the expected groups are present
        assertTrue(activeGroups.stream().anyMatch(group -> group.getName().equals("VIP Customers")));
        assertTrue(activeGroups.stream().anyMatch(group -> group.getName().equals("Wholesale Buyers")));
        assertTrue(activeGroups.stream().anyMatch(group -> group.getName().equals("Retail Customers")));
    }

    @Test
    void testFindByNameAndDeletedFalse() {
        Optional<CustomerGroup> group = customerGroupRepository.findByNameAndDeletedFalse("VIP Customers");

        assertTrue(group.isPresent());
        assertEquals("VIP Customers", group.get().getName());
        assertEquals("Exclusive group for high-value customers", group.get().getDescription());
    }

    @Test
    void testFindByNameAndDeletedFalse_NotFound() {
        Optional<CustomerGroup> group = customerGroupRepository.findByNameAndDeletedFalse("NonExistentGroup");

        assertFalse(group.isPresent());
    }

    @Test
    void testFindByNameAndDeletedTrue() {
        // Create a deleted customer group for testing
        CustomerGroup deletedGroup = new CustomerGroup();
        deletedGroup.setName("Deleted Group");
        deletedGroup.setDescription("This group is deleted");
        deletedGroup.setEnabled(true);
        deletedGroup.setDeleted(true);
        deletedGroup.setCreatedAt(LocalDateTime.now());
        deletedGroup.setUpdatedAt(LocalDateTime.now());
        deletedGroup.setDeletedAt(LocalDateTime.now());
        deletedGroup.setCreatedUser(1);
        deletedGroup.setUpdatedUser(1);
        deletedGroup.setDeletedUser(1);

        customerGroupRepository.save(deletedGroup);

        Optional<CustomerGroup> foundGroup = customerGroupRepository.findByNameAndDeletedTrue("Deleted Group");

        assertTrue(foundGroup.isPresent());
        assertEquals("Deleted Group", foundGroup.get().getName());
        assertTrue(foundGroup.get().isDeleted());
    }

    @Test
    void testFindByNameAndDeletedTrue_NotFound() {
        // VIP Customers is not deleted in data.sql
        Optional<CustomerGroup> deletedGroup = customerGroupRepository.findByNameAndDeletedTrue("VIP Customers");

        assertFalse(deletedGroup.isPresent());
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

        assertTrue(foundGroup.isPresent());
        assertEquals("Test Group", foundGroup.get().getName());
        assertEquals("Test Description", foundGroup.get().getDescription());
    }
}
