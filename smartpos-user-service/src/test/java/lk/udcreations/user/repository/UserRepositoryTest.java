package lk.udcreations.user.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import lk.udcreations.user.entity.Users;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void testFindByUsernameAndDeletedFalse_UserExists() {
        Optional<Users> user = userRepository.findByUsernameAndDeletedFalse("user1");
        assertTrue(user.isPresent());
        assertEquals("user1", user.get().getUsername());
    }

    @Test
    void testFindByUsernameAndDeletedFalse_UserNotExists() {
        Optional<Users> user = userRepository.findByUsernameAndDeletedFalse("nonexistentuser");
        assertFalse(user.isPresent());
    }

    @Test
    void testFindByUsernameAndDeletedTrue() {
        // Get a user and mark as deleted
        Users user = userRepository.findByUsername("user1").get();
        user.setDeleted(true);
        userRepository.save(user);

        // Verify it can be found with findByUsernameAndDeletedTrue
        Optional<Users> deletedUser = userRepository.findByUsernameAndDeletedTrue("user1");
        assertTrue(deletedUser.isPresent());
        assertEquals("user1", deletedUser.get().getUsername());
        assertTrue(deletedUser.get().isDeleted());
    }

    @Test
    void testFindByUsernameAndDeletedTrue_NotFound() {
        // Non-deleted user should not be found with this method
        Optional<Users> user = userRepository.findByUsernameAndDeletedTrue("user1");
        assertFalse(user.isPresent());
    }

    @Test
    void testFindByUserIdAndDeletedFalse_UserExists() {
        // First get a user to find its ID
        Users user = userRepository.findByUsername("user1").get();
        
        // Then test finding by ID
        Optional<Users> foundUser = userRepository.findByUserIdAndDeletedFalse(user.getUserId());
        assertTrue(foundUser.isPresent());
        assertEquals("user1", foundUser.get().getUsername());
    }

    @Test
    void testFindByUserIdAndDeletedFalse_UserNotExists() {
        Optional<Users> foundUser = userRepository.findByUserIdAndDeletedFalse(999);
        assertFalse(foundUser.isPresent());
    }

    @Test
    void testFindByUsername_UserExists() {
        Optional<Users> user = userRepository.findByUsername("user1");
        assertTrue(user.isPresent());
        assertEquals("user1", user.get().getUsername());
    }

    @Test
    void testFindByUsername_UserNotExists() {
        Optional<Users> user = userRepository.findByUsername("nonexistentuser");
        assertFalse(user.isPresent());
    }

    @Test
    void testFindByDeletedFalse() {
        List<Users> users = userRepository.findByDeletedFalse();
        assertEquals(2, users.size()); // "user1" and "user2" should be included
        assertTrue(users.stream().anyMatch(user -> user.getUsername().equals("user1")));
        assertTrue(users.stream().anyMatch(user -> user.getUsername().equals("user2")));
    }

    @Test
    void testExistsByEmailAndDeletedFalse_EmailExists() {
        boolean exists = userRepository.existsByEmailAndDeletedFalse("user1@example.com");
        assertTrue(exists);
    }

    @Test
    void testExistsByEmailAndDeletedFalse_EmailNotExists() {
        boolean exists = userRepository.existsByEmailAndDeletedFalse("nonexistent@example.com");
        assertFalse(exists);
    }

    @Test
    void testExistsByUsername_UsernameExists() {
        boolean exists = userRepository.existsByUsername("user1");
        assertTrue(exists);
    }

    @Test
    void testExistsByUsername_UsernameNotExists() {
        boolean exists = userRepository.existsByUsername("nonexistentuser");
        assertFalse(exists);
    }
}