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

import java.math.BigDecimal;
import java.time.LocalDate;
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

import lk.udcreations.common.dto.product.CreateProductDTO;
import lk.udcreations.common.dto.product.ProductDTO;
import lk.udcreations.product.config.TestConfig;
import lk.udcreations.product.entity.Category;
import lk.udcreations.product.entity.Distributor;
import lk.udcreations.product.entity.Product;
import lk.udcreations.product.repository.CategoryRepository;
import lk.udcreations.product.repository.DistributorRepository;
import lk.udcreations.product.repository.ProductRepository;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class ProductIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private DistributorRepository distributorRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testGetAllProducts() throws Exception {
        // Perform GET request to retrieve all products
        mockMvc.perform(get("/api/v1/product/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                // We expect at least the products from data.sql
                .andExpect(jsonPath("$[?(@.productName=='Product 1')]").exists())
                .andExpect(jsonPath("$[?(@.productName=='Product 2')]").exists());
    }

    @Test
    public void testGetAllExistProducts() throws Exception {
        // Perform GET request to retrieve all existing (non-deleted) products
        mockMvc.perform(get("/api/v1/product"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[?(@.productName=='Product 1')]").exists())
                .andExpect(jsonPath("$[?(@.productName=='Product 2')]").exists())
                // Deleted product should not be returned
                .andExpect(jsonPath("$[?(@.productName=='Deleted Product')]").doesNotExist());
    }

    @Test
    public void testGetProductById() throws Exception {
        // Find a product from the repository
        List<Product> products = productRepository.findByDeletedFalse();

        if (!products.isEmpty()) {
            Product product = products.get(0);
            Integer id = product.getId();

            // Perform GET request to retrieve the product by ID
            mockMvc.perform(get("/api/v1/product/" + id))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(id));
        }
    }

    @Test
    public void testCreateAndUpdateAndDeleteProduct() throws Exception {
        // Get a category and distributor for the product
        Optional<Category> category = categoryRepository.findByNameAndDeletedFalse("Electronics");
        Optional<Distributor> distributor = distributorRepository.findByCompanyNameAndDeletedFalse("Company A");

        if (category.isPresent() && distributor.isPresent()) {
            // Create a new product DTO
            CreateProductDTO createProductDTO = new CreateProductDTO();
            createProductDTO.setProductName("Test Integration Product");
            createProductDTO.setDescription("Product created in integration test");
            createProductDTO.setSku("SKU-TEST001");
            createProductDTO.setCategoryId(category.get().getCategoryId());
            createProductDTO.setDistributorId(distributor.get().getDistributorId());
            createProductDTO.setPrice(new BigDecimal("199.99"));
            createProductDTO.setCostPrice(new BigDecimal("150.00"));
            createProductDTO.setMinPrice(new BigDecimal("180.00"));
            createProductDTO.setManufactureDate(LocalDate.now().minusMonths(1).toString());
            createProductDTO.setExpireDate(LocalDate.now().plusYears(2).toString());
            createProductDTO.setEnabled(true);

            // Perform POST request to create the product
            MvcResult result = mockMvc.perform(post("/api/v1/product")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(createProductDTO)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.productName").value("Test Integration Product"))
                    .andReturn();

            // Extract the created product from the response
            String responseContent = result.getResponse().getContentAsString();
            ProductDTO createdProduct = objectMapper.readValue(responseContent, ProductDTO.class);
            assertNotNull(createdProduct.getId());

            // Verify the product was saved in the database
            Optional<Product> savedProduct = productRepository.findById(createdProduct.getId());
            assertTrue(savedProduct.isPresent());
            assertEquals("Test Integration Product", savedProduct.get().getProductName());
            assertEquals("SKU-TEST001", savedProduct.get().getSku());

            // Update the product
            CreateProductDTO updateProductDTO = new CreateProductDTO();
            updateProductDTO.setProductName("Updated Integration Product");
            updateProductDTO.setDescription("Updated description");
            updateProductDTO.setSku(savedProduct.get().getSku()); // Keep the same SKU
            updateProductDTO.setCategoryId(savedProduct.get().getCategoryId());
            updateProductDTO.setDistributorId(savedProduct.get().getDistributorId());
            updateProductDTO.setPrice(new BigDecimal("209.99"));
            updateProductDTO.setCostPrice(savedProduct.get().getCostPrice());
            updateProductDTO.setMinPrice(savedProduct.get().getMinPrice());
            updateProductDTO.setManufactureDate(savedProduct.get().getManufactureDate().toString());
            updateProductDTO.setExpireDate(savedProduct.get().getExpireDate().toString());
            updateProductDTO.setEnabled(true);

            // Perform PUT request to update the product
            mockMvc.perform(put("/api/v1/product/" + createdProduct.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(updateProductDTO)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.productName").value("Updated Integration Product"))
                    .andExpect(jsonPath("$.price").value(209.99));

            // Verify the product was updated in the database
            Optional<Product> updatedProduct = productRepository.findById(createdProduct.getId());
            assertTrue(updatedProduct.isPresent());
            assertEquals("Updated Integration Product", updatedProduct.get().getProductName());
            assertEquals(0, new BigDecimal("209.99").compareTo(updatedProduct.get().getPrice()));

            // Perform DELETE request to soft delete the product
            mockMvc.perform(delete("/api/v1/product/" + createdProduct.getId()))
                    .andExpect(status().isNoContent());

            // Verify the product was soft deleted in the database
            Optional<Product> deletedProduct = productRepository.findById(createdProduct.getId());
            assertTrue(deletedProduct.isPresent());
            assertTrue(deletedProduct.get().isDeleted());

            // Verify the product doesn't appear in the list of existing products
            mockMvc.perform(get("/api/v1/product"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[?(@.productName=='Updated Integration Product')]").doesNotExist());
        }
    }

    @Test
    public void testProductRelationships() throws Exception {
        // Get products with their relationships
        mockMvc.perform(get("/api/v1/product"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].categoryId").exists())
                .andExpect(jsonPath("$[0].distributorId").exists());

        // Get a specific product to check its relationships
        List<Product> products = productRepository.findByDeletedFalse();

        if (!products.isEmpty()) {
            Product product = products.get(0);
            Integer id = product.getId();

            // Perform GET request to retrieve the product by ID
            mockMvc.perform(get("/api/v1/product/" + id))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.categoryId").exists())
                    .andExpect(jsonPath("$.distributorId").exists());

            // Verify the relationships in the database
            assertNotNull(product.getCategoryId());
            assertNotNull(product.getDistributorId());

            // Verify the category exists
            Optional<Category> category = categoryRepository.findById(product.getCategoryId());
            assertTrue(category.isPresent());

            // Verify the distributor exists
            Optional<Distributor> distributor = distributorRepository.findById(product.getDistributorId());
            assertTrue(distributor.isPresent());
        }
    }
}
