package lk.udcreations.product.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.springframework.context.annotation.Import;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;

import lk.udcreations.common.dto.distributor.DistributorDTO;
import lk.udcreations.product.config.TestConfig;
import lk.udcreations.product.entity.Distributor;
import lk.udcreations.product.repository.DistributorRepository;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class DistributorIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private DistributorRepository distributorRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testGetAllDistributors() throws Exception {
        // Perform GET request to retrieve all distributors
        mockMvc.perform(get("/api/v1/distributor/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                // We expect at least the distributors from data.sql
                .andExpect(jsonPath("$[?(@.companyName=='Company A')]").exists())
                .andExpect(jsonPath("$[?(@.companyName=='Company B')]").exists());
    }

    @Test
    public void testGetAllExistDistributors() throws Exception {
        // Perform GET request to retrieve all existing (non-deleted) distributors
        mockMvc.perform(get("/api/v1/distributor"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[?(@.companyName=='Company A')]").exists())
                .andExpect(jsonPath("$[?(@.companyName=='Company B')]").exists())
                // Deleted distributor should not be returned
                .andExpect(jsonPath("$[?(@.companyName=='Company C')]").doesNotExist());
    }

    @Test
    public void testGetDistributorById() throws Exception {
        // Find a distributor ID from the repository
        List<Distributor> distributors = distributorRepository.findByCompanyNameAndDeletedFalse("Company A")
                .map(List::of)
                .orElse(List.of());

        if (!distributors.isEmpty()) {
            Integer distributorId = distributors.get(0).getDistributorId();

            // Perform GET request to retrieve the distributor by ID
            mockMvc.perform(get("/api/v1/distributor/" + distributorId))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.companyName").value("Company A"));
        }
    }

    @Test
    public void testCreateAndUpdateAndDeleteDistributor() throws Exception {
        // Create a new distributor DTO
        DistributorDTO distributorDTO = new DistributorDTO();
        distributorDTO.setCompanyName("Test Integration Distributor");
        distributorDTO.setEmail("test@integration.com");
        distributorDTO.setPhoneNo1("1234567890");
        distributorDTO.setAddress("Test Address");
        distributorDTO.setEnabled(true);

        // Perform POST request to create the distributor
        MvcResult result = mockMvc.perform(post("/api/v1/distributor")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(distributorDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.companyName").value("Test Integration Distributor"))
                .andReturn();

        // Extract the created distributor from the response
        String responseContent = result.getResponse().getContentAsString();
        DistributorDTO createdDistributor = objectMapper.readValue(responseContent, DistributorDTO.class);
        assertNotNull(createdDistributor.getDistributorId());

        // Verify the distributor was saved in the database
        Optional<Distributor> savedDistributor = distributorRepository.findById(createdDistributor.getDistributorId());
        assertTrue(savedDistributor.isPresent());
        assertEquals("Test Integration Distributor", savedDistributor.get().getCompanyName());
        assertEquals("test@integration.com", savedDistributor.get().getEmail());

        // Update the distributor
        createdDistributor.setCompanyName("Updated Integration Distributor");
        createdDistributor.setEmail("updated@integration.com");

        // Perform PUT request to update the distributor
        mockMvc.perform(put("/api/v1/distributor/" + createdDistributor.getDistributorId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createdDistributor)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.companyName").value("Updated Integration Distributor"))
                .andExpect(jsonPath("$.email").value("updated@integration.com"));

        // Verify the distributor was updated in the database
        Optional<Distributor> updatedDistributor = distributorRepository.findById(createdDistributor.getDistributorId());
        assertTrue(updatedDistributor.isPresent());
        assertEquals("Updated Integration Distributor", updatedDistributor.get().getCompanyName());
        assertEquals("updated@integration.com", updatedDistributor.get().getEmail());

        // Perform DELETE request to soft delete the distributor
        mockMvc.perform(delete("/api/v1/distributor/" + createdDistributor.getDistributorId()))
                .andExpect(status().isNoContent());

        // Verify the distributor was soft deleted in the database
        Optional<Distributor> deletedDistributor = distributorRepository.findById(createdDistributor.getDistributorId());
        assertTrue(deletedDistributor.isPresent());
        assertTrue(deletedDistributor.get().isDeleted());

        // Verify the distributor doesn't appear in the list of existing distributors
        mockMvc.perform(get("/api/v1/distributor"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.companyName=='Updated Integration Distributor')]").doesNotExist());
    }
}
