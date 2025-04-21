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

import lk.udcreations.user.entity.Role;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
class RoleRepositoryTest {

    @Autowired
    private RoleRepository roleRepository;

    @Test
    void testFindByRoleName() {
        Optional<Role> role = roleRepository.findByRoleName("Admin");
        assertTrue(role.isPresent());
        assertEquals("Admin", role.get().getRoleName());
    }

    @Test
    void testFindByRoleName_NotFound() {
        Optional<Role> role = roleRepository.findByRoleName("NonExistentRole");
        assertFalse(role.isPresent());
    }

    @Test
    void testFindByRoleId() {
        Optional<Role> role = roleRepository.findByRoleId(1);
        assertTrue(role.isPresent());
        assertEquals("Admin", role.get().getRoleName());
    }

    @Test
    void testFindByRoleId_NotFound() {
        Optional<Role> role = roleRepository.findByRoleId(999);
        assertFalse(role.isPresent());
    }

    @Test
    void testFindByRoleNameAndDeletedTrue() {
        Optional<Role> role = roleRepository.findByRoleNameAndDeletedTrue("DeletedRole");
        assertTrue(role.isPresent());
        assertTrue(role.get().isDeleted());
        assertEquals("DeletedRole", role.get().getRoleName());
    }

    @Test
    void testFindByRoleNameAndDeletedTrue_NotFound() {
        Optional<Role> role = roleRepository.findByRoleNameAndDeletedTrue("Admin");
        assertFalse(role.isPresent());
    }

    @Test
    void testFindByDeletedFalse() {
        List<Role> roles = roleRepository.findByDeletedFalse();
        assertEquals(2, roles.size()); // "Admin" and "User" should be included
        assertTrue(roles.stream().anyMatch(role -> role.getRoleName().equals("Admin")));
        assertTrue(roles.stream().anyMatch(role -> role.getRoleName().equals("User")));
    }

    @Test
    void testFindAll() {
        List<Role> roles = roleRepository.findAll();
        assertEquals(3, roles.size()); // "Admin", "User", and "DeletedRole" should be included
        assertTrue(roles.stream().anyMatch(role -> role.getRoleName().equals("Admin")));
        assertTrue(roles.stream().anyMatch(role -> role.getRoleName().equals("User")));
        assertTrue(roles.stream().anyMatch(role -> role.getRoleName().equals("DeletedRole")));
    }
}