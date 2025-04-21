package lk.udcreations.product.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
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

import lk.udcreations.common.dto.brand.BrandDTO;
import lk.udcreations.product.TestProductServiceApplication;
import lk.udcreations.product.entity.Brand;
import lk.udcreations.product.repository.BrandRepository;

@SpringBootTest(classes = TestProductServiceApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class BrandIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BrandRepository brandRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testGetAllBrands() throws Exception {
        // Perform GET request to retrieve all brands
        mockMvc.perform(get("/api/v1/brand/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                // We expect at least the brands from data.sql
                .andExpect(jsonPath("$[?(@.name=='Nike')]").exists())
                .andExpect(jsonPath("$[?(@.name=='Adidas')]").exists());
    }

    @Test
    public void testGetAllExistBrands() throws Exception {
        // Perform GET request to retrieve all existing (non-deleted) brands
        mockMvc.perform(get("/api/v1/brand"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[?(@.name=='Nike')]").exists())
                .andExpect(jsonPath("$[?(@.name=='Adidas')]").exists())
                // Deleted brand should not be returned
                .andExpect(jsonPath("$[?(@.name=='DeletedBrand')]").doesNotExist());
    }

    @Test
    public void testGetBrandById() throws Exception {
        // Find a brand ID from the repository
        Optional<Brand> brandOptional = brandRepository.findByNameAndDeletedFalse("Nike");

        if (brandOptional.isPresent()) {
            Integer brandId = brandOptional.get().getBrandId();

            // Perform GET request to retrieve the brand by ID
            mockMvc.perform(get("/api/v1/brand/" + brandId))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.name").value("Nike"));
        }
    }

    @Test
    public void testCreateAndUpdateAndDeleteBrand() throws Exception {
        // Create a new brand DTO
        BrandDTO brandDTO = new BrandDTO();
        brandDTO.setName("Test Integration Brand");
        brandDTO.setDescription("Brand created in integration test");
        brandDTO.setEnabled(true);

        // Perform POST request to create the brand
        MvcResult result = mockMvc.perform(post("/api/v1/brand")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(brandDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Test Integration Brand"))
                .andReturn();

        // Extract the created brand from the response
        String responseContent = result.getResponse().getContentAsString();
        BrandDTO createdBrand = objectMapper.readValue(responseContent, BrandDTO.class);
        assertNotNull(createdBrand.getBrandId());

        // Verify the brand was saved in the database
        Optional<Brand> savedBrand = brandRepository.findById(createdBrand.getBrandId());
        assertTrue(savedBrand.isPresent());
        assertEquals("Test Integration Brand", savedBrand.get().getName());

        // Update the brand
        createdBrand.setName("Updated Integration Brand");
        createdBrand.setDescription("Updated description");

        // Perform PUT request to update the brand
        mockMvc.perform(put("/api/v1/brand/" + createdBrand.getBrandId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createdBrand)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Integration Brand"));

        // Verify the brand was updated in the database
        Optional<Brand> updatedBrand = brandRepository.findById(createdBrand.getBrandId());
        assertTrue(updatedBrand.isPresent());
        assertEquals("Updated Integration Brand", updatedBrand.get().getName());

        // Perform DELETE request to soft delete the brand
        mockMvc.perform(delete("/api/v1/brand/" + createdBrand.getBrandId()))
                .andExpect(status().isNoContent());

        // Verify the brand was soft deleted in the database
        Optional<Brand> deletedBrand = brandRepository.findById(createdBrand.getBrandId());
        assertTrue(deletedBrand.isPresent());
        assertTrue(deletedBrand.get().isDeleted());

        // Verify the brand doesn't appear in the list of existing brands
        mockMvc.perform(get("/api/v1/brand"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.name=='Updated Integration Brand')]").doesNotExist());
    }
}
