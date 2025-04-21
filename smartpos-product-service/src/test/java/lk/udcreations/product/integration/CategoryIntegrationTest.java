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

import lk.udcreations.common.dto.category.CategoryDTO;
import lk.udcreations.product.config.TestConfig;
import lk.udcreations.product.entity.Category;
import lk.udcreations.product.repository.CategoryRepository;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class CategoryIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testGetAllCategories() throws Exception {
        // Perform GET request to retrieve all categories
        mockMvc.perform(get("/api/v1/category/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                // We expect at least the categories from data.sql
                .andExpect(jsonPath("$[?(@.name=='Electronics')]").exists())
                .andExpect(jsonPath("$[?(@.name=='Clothing')]").exists());
    }

    @Test
    public void testGetAllExistCategories() throws Exception {
        // Perform GET request to retrieve all existing (non-deleted) categories
        mockMvc.perform(get("/api/v1/category"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[?(@.name=='Electronics')]").exists())
                .andExpect(jsonPath("$[?(@.name=='Clothing')]").exists())
                // Deleted category should not be returned
                .andExpect(jsonPath("$[?(@.name=='DeletedCategory')]").doesNotExist());
    }

    @Test
    public void testGetCategoryById() throws Exception {
        // Find a category ID from the repository
        List<Category> categories = categoryRepository.findByNameAndDeletedFalse("Electronics")
                .map(List::of)
                .orElse(List.of());

        if (!categories.isEmpty()) {
            Integer categoryId = categories.get(0).getCategoryId();

            // Perform GET request to retrieve the category by ID
            mockMvc.perform(get("/api/v1/category/" + categoryId))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.name").value("Electronics"));
        }
    }

    @Test
    public void testCreateAndUpdateAndDeleteCategory() throws Exception {
        // Create a new category DTO
        CategoryDTO categoryDTO = new CategoryDTO();
        categoryDTO.setName("Test Integration Category");
        categoryDTO.setDescription("Category created in integration test");
        categoryDTO.setCatPrefix("TIC");
        categoryDTO.setEnabled(true);

        // Perform POST request to create the category
        MvcResult result = mockMvc.perform(post("/api/v1/category")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(categoryDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Test Integration Category"))
                .andReturn();

        // Extract the created category from the response
        String responseContent = result.getResponse().getContentAsString();
        CategoryDTO createdCategory = objectMapper.readValue(responseContent, CategoryDTO.class);
        assertNotNull(createdCategory.getCategoryId());

        // Verify the category was saved in the database
        Optional<Category> savedCategory = categoryRepository.findById(createdCategory.getCategoryId());
        assertTrue(savedCategory.isPresent());
        assertEquals("Test Integration Category", savedCategory.get().getName());
        assertEquals("TIC", savedCategory.get().getCatPrefix());

        // Update the category
        createdCategory.setName("Updated Integration Category");
        createdCategory.setDescription("Updated description");

        // Perform PUT request to update the category
        mockMvc.perform(put("/api/v1/category/" + createdCategory.getCategoryId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createdCategory)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Integration Category"));

        // Verify the category was updated in the database
        Optional<Category> updatedCategory = categoryRepository.findById(createdCategory.getCategoryId());
        assertTrue(updatedCategory.isPresent());
        assertEquals("Updated Integration Category", updatedCategory.get().getName());

        // Perform DELETE request to soft delete the category
        mockMvc.perform(delete("/api/v1/category/" + createdCategory.getCategoryId()))
                .andExpect(status().isNoContent());

        // Verify the category was soft deleted in the database
        Optional<Category> deletedCategory = categoryRepository.findById(createdCategory.getCategoryId());
        assertTrue(deletedCategory.isPresent());
        assertTrue(deletedCategory.get().isDeleted());

        // Verify the category doesn't appear in the list of existing categories
        mockMvc.perform(get("/api/v1/category"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.name=='Updated Integration Category')]").doesNotExist());
    }
}
