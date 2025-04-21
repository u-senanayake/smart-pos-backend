package lk.udcreations.user.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;

import lk.udcreations.user.entity.Users;
import lk.udcreations.user.repository.UserRepository;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Transactional
class UserIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testGetAllUsers() throws Exception {
        // Should return all users including deleted ones (3 users from data.sql)
        mockMvc.perform(get("/api/v1/users/all"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(3));
    }

    @Test
    void testGetAllExistUsers() throws Exception {
        // Should return only non-deleted users (3 users from data.sql)
        mockMvc.perform(get("/api/v1/users"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(3));
    }

    @Test
    void testGetUserById() throws Exception {
        // Get user1 (ID 1)
        mockMvc.perform(get("/api/v1/users/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.userId").value(1))
            .andExpect(jsonPath("$.username").value("user1"))
            .andExpect(jsonPath("$.firstName").value("User"))
            .andExpect(jsonPath("$.lastName").value("One"))
            .andExpect(jsonPath("$.email").value("user1@example.com"))
            .andExpect(jsonPath("$.address").value("Address1"))
            .andExpect(jsonPath("$.phoneNo1").value("1234567890"))
            .andExpect(jsonPath("$.role.roleId").value(1))
            .andExpect(jsonPath("$.enabled").value(true))
            .andExpect(jsonPath("$.deleted").value(false));
    }

    @Test
    void testGetUserByIdNotFound() throws Exception {
        // Try to get a non-existent user
        mockMvc.perform(get("/api/v1/users/999"))
            .andExpect(status().isNotFound());
    }

    @Test
    void testGetUserByUsername() throws Exception {
        // Get user by username
        mockMvc.perform(get("/api/v1/users/username/user1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.userId").value(1))
            .andExpect(jsonPath("$.username").value("user1"))
            .andExpect(jsonPath("$.firstName").value("User"))
            .andExpect(jsonPath("$.lastName").value("One"))
            .andExpect(jsonPath("$.email").value("user1@example.com"));
    }

    @Test
    void testGetUserByUsernameNotFound() throws Exception {
        // Try to get a non-existent user by username
        mockMvc.perform(get("/api/v1/users/username/nonexistentuser"))
            .andExpect(status().isNotFound());
    }

    @Test
    void testCreateUser() throws Exception {
        Users newUser = new Users();
        newUser.setUsername("testuser");
        newUser.setFirstName("Test");
        newUser.setLastName("User");
        newUser.setEmail("testuser@example.com");
        newUser.setAddress("Test Address");
        newUser.setPhoneNo1("9876543210");
        newUser.setRoleId(2); // User role
        newUser.setPassword("password123");
        newUser.setEnabled(true);

        mockMvc.perform(post("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newUser)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.username").value("testuser"))
            .andExpect(jsonPath("$.firstName").value("Test"))
            .andExpect(jsonPath("$.lastName").value("User"));

        // Verify the user was added to the database
        mockMvc.perform(get("/api/v1/users"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(4)); // Now 4 users (user1, user2, admin_user, testuser)
    }

    @Test
    void testCreateUserWithInvalidData() throws Exception {
        Users invalidUser = new Users();
        // Missing required fields
        invalidUser.setUsername("test"); // Too short (min 5 chars)
        invalidUser.setLastName("User");
        invalidUser.setEmail("invalid-email"); // Invalid email format
        invalidUser.setRoleId(2);
        invalidUser.setPassword("password123");

        mockMvc.perform(post("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidUser)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void testUpdateUser() throws Exception {
        Users updatedUser = new Users();
        updatedUser.setUsername("user1updated");
        updatedUser.setFirstName("Updated");
        updatedUser.setLastName("User");
        updatedUser.setEmail("updated@example.com");
        updatedUser.setAddress("Updated Address");
        updatedUser.setPhoneNo1("9876543210");
        updatedUser.setRoleId(2);
        updatedUser.setPassword("updatedpassword");
        updatedUser.setEnabled(false);

        mockMvc.perform(put("/api/v1/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedUser)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.userId").value(1))
            .andExpect(jsonPath("$.username").value("user1updated"))
            .andExpect(jsonPath("$.firstName").value("Updated"))
            .andExpect(jsonPath("$.lastName").value("User"))
            .andExpect(jsonPath("$.email").value("updated@example.com"))
            .andExpect(jsonPath("$.address").value("Updated Address"))
            .andExpect(jsonPath("$.phoneNo1").value("9876543210"))
            .andExpect(jsonPath("$.role.roleId").value(2))
            .andExpect(jsonPath("$.enabled").value(false));

        // Verify the user was updated in the database
        mockMvc.perform(get("/api/v1/users/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.username").value("user1updated"));
    }

    @Test
    void testUpdateUserNotFound() throws Exception {
        Users updatedUser = new Users();
        updatedUser.setUsername("nonexistentuser");
        updatedUser.setFirstName("Nonexistent");
        updatedUser.setLastName("User");
        updatedUser.setEmail("nonexistent@example.com");
        updatedUser.setAddress("Nonexistent Address");
        updatedUser.setPhoneNo1("9876543210");
        updatedUser.setRoleId(2);
        updatedUser.setPassword("password");
        updatedUser.setEnabled(true);

        mockMvc.perform(put("/api/v1/users/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedUser)))
            .andExpect(status().isNotFound());
    }

    @Test
    void testDeleteUser() throws Exception {
        // Soft delete user1 (ID 1)
        mockMvc.perform(delete("/api/v1/users/1"))
            .andExpect(status().isNoContent());

        // Verify the user is no longer returned in the active users list
        mockMvc.perform(get("/api/v1/users"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(1))
            .andExpect(jsonPath("$[0].username").value("user2"));

        // But it should still be in the all users list
        mockMvc.perform(get("/api/v1/users/all"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(2));

        // And it should be marked as deleted
        mockMvc.perform(get("/api/v1/users/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.deleted").value(true));
    }

    @Test
    void testDeleteUserNotFound() throws Exception {
        mockMvc.perform(delete("/api/v1/users/999"))
            .andExpect(status().isNotFound());
    }
}
