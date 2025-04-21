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

import lk.udcreations.user.entity.Role;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Transactional
class RoleIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testGetAllRoles() throws Exception {
        // Should return all roles including deleted ones (3 roles from data.sql)
        mockMvc.perform(get("/api/v1/role/all"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(3))
            .andExpect(jsonPath("$[0].roleName").value("Admin"))
            .andExpect(jsonPath("$[1].roleName").value("User"))
            .andExpect(jsonPath("$[2].roleName").value("DeletedRole"));
    }

    @Test
    void testGetAllExistRoles() throws Exception {
        // Should return only non-deleted roles (2 roles from data.sql)
        mockMvc.perform(get("/api/v1/role"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$[0].roleName").value("Admin"))
            .andExpect(jsonPath("$[1].roleName").value("User"));
    }

    @Test
    void testGetRoleById() throws Exception {
        // Get Admin role (ID 1)
        mockMvc.perform(get("/api/v1/role/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.roleId").value(1))
            .andExpect(jsonPath("$.roleName").value("Admin"))
            .andExpect(jsonPath("$.description").value("Administrator role"))
            .andExpect(jsonPath("$.enabled").value(true))
            .andExpect(jsonPath("$.deleted").value(false));
    }

    @Test
    void testGetRoleByIdNotFound() throws Exception {
        // Try to get a non-existent role
        mockMvc.perform(get("/api/v1/role/999"))
            .andExpect(status().isNotFound());
    }

    @Test
    void testCreateRole() throws Exception {
        Role newRole = new Role();
        newRole.setRoleName("Manager");
        newRole.setDescription("Manager role");
        newRole.setEnabled(true);

        mockMvc.perform(post("/api/v1/role")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newRole)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.roleName").value("Manager"))
            .andExpect(jsonPath("$.description").value("Manager role"))
            .andExpect(jsonPath("$.enabled").value(true))
            .andExpect(jsonPath("$.deleted").value(false));

        // Verify the role was added to the database
        mockMvc.perform(get("/api/v1/role"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(3));
    }

    @Test
    void testCreateRoleWithInvalidData() throws Exception {
        Role invalidRole = new Role();
        // Missing required roleName
        invalidRole.setDescription("Invalid role");
        invalidRole.setEnabled(true);

        mockMvc.perform(post("/api/v1/role")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRole)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void testUpdateRole() throws Exception {
        Role updatedRole = new Role();
        updatedRole.setRoleName("Updated Admin");
        updatedRole.setDescription("Updated description");
        updatedRole.setEnabled(false);

        mockMvc.perform(put("/api/v1/role/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedRole)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.roleId").value(1))
            .andExpect(jsonPath("$.roleName").value("Updated Admin"))
            .andExpect(jsonPath("$.description").value("Updated description"))
            .andExpect(jsonPath("$.enabled").value(false));

        // Verify the role was updated in the database
        mockMvc.perform(get("/api/v1/role/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.roleName").value("Updated Admin"));
    }

    @Test
    void testUpdateRoleNotFound() throws Exception {
        Role updatedRole = new Role();
        updatedRole.setRoleName("Updated Role");
        updatedRole.setDescription("Updated description");
        updatedRole.setEnabled(true);

        mockMvc.perform(put("/api/v1/role/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedRole)))
            .andExpect(status().isNotFound());
    }

    @Test
    void testDeleteRole() throws Exception {
        // Soft delete the Admin role (ID 1)
        mockMvc.perform(delete("/api/v1/role/1"))
            .andExpect(status().isNoContent());

        // Verify the role is no longer returned in the active roles list
        mockMvc.perform(get("/api/v1/role"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(1))
            .andExpect(jsonPath("$[0].roleName").value("User"));

        // But it should still be in the all role lists
        mockMvc.perform(get("/api/v1/role/all"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(3));

        // And it should be marked as deleted
        mockMvc.perform(get("/api/v1/role/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.deleted").value(true));
    }

    @Test
    void testDeleteRoleNotFound() throws Exception {
        mockMvc.perform(delete("/api/v1/role/999"))
            .andExpect(status().isNotFound());
    }
}
