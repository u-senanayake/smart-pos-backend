package lk.udcreations.user.security;

import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import lk.udcreations.user.entity.Users;
import lk.udcreations.user.repository.UserRepository;

/**
 * Test implementation of AuthUtils that returns a predefined user for testing.
 * This is used to bypass the authentication requirement in tests.
 */
@Component
@Primary
@Profile("test")
public class TestAuthUtils extends AuthUtils {

    public TestAuthUtils(UserRepository userRepository) {
        super(userRepository);
    }

    @Override
    public String getLoggedInUsername() {
        // Return the username of a user that exists in the test data
        return "admin_user";
    }

    @Override
    public Users getLoggedInUser() {
        // Create a mock user for testing
        Users mockUser = new Users();
        mockUser.setUserId(1);
        mockUser.setUsername("admin_user");
        mockUser.setFirstName("Admin");
        mockUser.setLastName("User");
        mockUser.setEmail("admin@example.com");
        mockUser.setRoleId(1);
        mockUser.setEnabled(true);
        mockUser.setDeleted(false);
        
        return mockUser;
    }
}