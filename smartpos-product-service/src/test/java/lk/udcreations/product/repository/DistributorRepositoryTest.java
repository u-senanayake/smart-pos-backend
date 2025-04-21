package lk.udcreations.product.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import lk.udcreations.product.entity.Distributor;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
class DistributorRepositoryTest {

    @Autowired
    private DistributorRepository distributorRepository;

    @AfterEach
    void cleanup() {
        distributorRepository.deleteAll();
    }

    @Test
    void testFindByDeletedFalse() {
        // Get initial count of non-deleted distributors
        List<Distributor> initialActiveDistributors = distributorRepository.findByDeletedFalse();
        int initialCount = initialActiveDistributors.size();

        // Create test distributors
        Distributor distributor1 = new Distributor();
        distributor1.setCompanyName("TestCompany1");
        distributor1.setEmail("test1@example.com");
        distributor1.setPhoneNo1("1234567890");
        distributor1.setAddress("Address 1");
        distributor1.setEnabled(true);
        distributor1.setDeleted(false);

        Distributor distributor2 = new Distributor();
        distributor2.setCompanyName("TestCompany2");
        distributor2.setEmail("test2@example.com");
        distributor2.setPhoneNo1("0987654321");
        distributor2.setAddress("Address 2");
        distributor2.setEnabled(true);
        distributor2.setDeleted(false);

        Distributor distributor3 = new Distributor();
        distributor3.setCompanyName("TestDeletedCompany");
        distributor3.setEmail("test3@example.com");
        distributor3.setPhoneNo1("1122334455");
        distributor3.setAddress("Address 3");
        distributor3.setEnabled(true);
        distributor3.setDeleted(true);

        // Save distributors to repository
        distributorRepository.save(distributor1);
        distributorRepository.save(distributor2);
        distributorRepository.save(distributor3);

        // Test findByDeletedFalse method
        List<Distributor> activeDistributors = distributorRepository.findByDeletedFalse();

        // Verify results
        assertEquals(initialCount + 2, activeDistributors.size());
        assertTrue(activeDistributors.stream().anyMatch(distributor -> distributor.getCompanyName().equals("TestCompany1")));
        assertTrue(activeDistributors.stream().anyMatch(distributor -> distributor.getCompanyName().equals("TestCompany2")));
        assertFalse(activeDistributors.stream().anyMatch(distributor -> distributor.getCompanyName().equals("TestDeletedCompany")));
    }

    @Test
    void testFindByCompanyNameAndDeletedFalse() {
        // Create test distributor
        Distributor distributor = new Distributor();
        distributor.setCompanyName("ActiveCompany");
        distributor.setEmail("active@example.com");
        distributor.setPhoneNo1("1234567890");
        distributor.setAddress("Active Address");
        distributor.setEnabled(true);
        distributor.setDeleted(false);

        // Save distributor to repository
        distributorRepository.save(distributor);

        // Test findByCompanyNameAndDeletedFalse method
        Optional<Distributor> foundDistributor = distributorRepository.findByCompanyNameAndDeletedFalse("ActiveCompany");

        // Verify results
        assertTrue(foundDistributor.isPresent());
        assertEquals("ActiveCompany", foundDistributor.get().getCompanyName());
    }

    @Test
    void testFindByCompanyNameAndDeletedFalse_NotFound() {
        // Test findByCompanyNameAndDeletedFalse method with non-existent distributor
        Optional<Distributor> foundDistributor = distributorRepository.findByCompanyNameAndDeletedFalse("NonExistentCompany");

        // Verify results
        assertFalse(foundDistributor.isPresent());
    }

    @Test
    void testFindByCompanyNameAndDeletedTrue() {
        // Create test distributor
        Distributor distributor = new Distributor();
        distributor.setCompanyName("DeletedCompany");
        distributor.setEmail("deleted@example.com");
        distributor.setPhoneNo1("0987654321");
        distributor.setAddress("Deleted Address");
        distributor.setEnabled(true);
        distributor.setDeleted(true);

        // Save distributor to repository
        distributorRepository.save(distributor);

        // Test findByCompanyNameAndDeletedTrue method
        Optional<Distributor> foundDistributor = distributorRepository.findByCompanyNameAndDeletedTrue("DeletedCompany");

        // Verify results
        assertTrue(foundDistributor.isPresent());
        assertEquals("DeletedCompany", foundDistributor.get().getCompanyName());
    }

    @Test
    void testFindByCompanyNameAndDeletedTrue_NotFound() {
        // Test findByCompanyNameAndDeletedTrue method with non-existent distributor
        Optional<Distributor> foundDistributor = distributorRepository.findByCompanyNameAndDeletedTrue("NonExistentCompany");

        // Verify results
        assertFalse(foundDistributor.isPresent());
    }

    @Test
    void testFindByEmail() {
        // Create test distributor
        Distributor distributor = new Distributor();
        distributor.setCompanyName("EmailTestCompany");
        distributor.setEmail("emailtest@example.com");
        distributor.setPhoneNo1("1122334455");
        distributor.setAddress("Email Test Address");
        distributor.setEnabled(true);
        distributor.setDeleted(false);

        // Save distributor to repository
        distributorRepository.save(distributor);

        // Test findByEmail method
        Optional<Distributor> foundDistributor = distributorRepository.findByEmail("emailtest@example.com");

        // Verify results
        assertTrue(foundDistributor.isPresent());
        assertEquals("EmailTestCompany", foundDistributor.get().getCompanyName());
    }

    @Test
    void testFindByEmail_NotFound() {
        // Test findByEmail method with non-existent email
        Optional<Distributor> foundDistributor = distributorRepository.findByEmail("nonexistent@example.com");

        // Verify results
        assertFalse(foundDistributor.isPresent());
    }
}
