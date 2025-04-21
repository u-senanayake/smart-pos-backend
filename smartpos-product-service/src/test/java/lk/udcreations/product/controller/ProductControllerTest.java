package lk.udcreations.product.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;

import lk.udcreations.product.controller.ProductController;
import lk.udcreations.common.dto.product.CreateProductDTO;
import lk.udcreations.common.dto.product.ProductDTO;
import lk.udcreations.product.service.ProductService;

class ProductControllerTest {

	@Mock
	private ProductService productService;

	@InjectMocks
	private ProductController productController;

	private MockMvc mockMvc;
	private ObjectMapper objectMapper;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		mockMvc = MockMvcBuilders.standaloneSetup(productController).build();
		objectMapper = new ObjectMapper();
		objectMapper.findAndRegisterModules(); // Register Java 8 time modules
	}

	@Test
	void testGetAllProducts() throws Exception {
		ProductDTO product1 = new ProductDTO();
		product1.setId(1);
		product1.setProductName("Product 1");
		product1.setDescription("Description 1");
		product1.setPrice(new BigDecimal("100.00"));
		product1.setCreatedAt(LocalDateTime.now());

		ProductDTO product2 = new ProductDTO();
		product2.setId(2);
		product2.setProductName("Product 2");
		product2.setDescription("Description 2");
		product2.setPrice(new BigDecimal("200.00"));
		product2.setCreatedAt(LocalDateTime.now());

		List<ProductDTO> products = Arrays.asList(product1, product2);

		when(productService.getAllProducts()).thenReturn(products);

		mockMvc.perform(get("/api/v1/product/all")).andExpect(status().isOk()).andExpect(jsonPath("$.size()").value(2))
				.andExpect(jsonPath("$[0].productName").value("Product 1"))
				.andExpect(jsonPath("$[1].productName").value("Product 2"));

		verify(productService, times(1)).getAllProducts();
	}

	@Test
	void testGetAllExistProducts() throws Exception {
		ProductDTO product1 = new ProductDTO();
		product1.setId(1);
		product1.setProductName("Product 1");
		product1.setDescription("Description 1");
		product1.setPrice(new BigDecimal("100.00"));
		product1.setCreatedAt(LocalDateTime.now());

		ProductDTO product2 = new ProductDTO();
		product2.setId(2);
		product2.setProductName("Product 2");
		product2.setDescription("Description 2");
		product2.setPrice(new BigDecimal("200.00"));
		product2.setCreatedAt(LocalDateTime.now());

		List<ProductDTO> products = Arrays.asList(product1, product2);

		when(productService.getAllExistProducts()).thenReturn(products);

		mockMvc.perform(get("/api/v1/product")).andExpect(status().isOk()).andExpect(jsonPath("$.size()").value(2))
				.andExpect(jsonPath("$[0].productName").value("Product 1"))
				.andExpect(jsonPath("$[1].productName").value("Product 2"));

		verify(productService, times(1)).getAllExistProducts();
	}

	@Test
	void testGetProductById() throws Exception {
		ProductDTO product = new ProductDTO();
		product.setId(1);
		product.setProductName("Product 1");
		product.setDescription("Description 1");
		product.setPrice(new BigDecimal("100.00"));
		product.setCreatedAt(LocalDateTime.now());

		when(productService.getProductDTOById(1)).thenReturn(product);

		mockMvc.perform(get("/api/v1/product/1")).andExpect(status().isOk())
				.andExpect(jsonPath("$.productName").value("Product 1"));

		verify(productService, times(1)).getProductDTOById(1);
	}

	@Test
	void testCreateProduct() throws Exception {
		CreateProductDTO createProductDTO = new CreateProductDTO();
		createProductDTO.setProductName("New Product");
		createProductDTO.setDescription("New Description");
		createProductDTO.setPrice(new BigDecimal("150.00"));
		createProductDTO.setSku("NEWSKU123");
		createProductDTO.setEnabled(true);

		ProductDTO productDTO = new ProductDTO();
		productDTO.setId(1);
		productDTO.setProductName("New Product");
		productDTO.setDescription("New Description");
		productDTO.setPrice(new BigDecimal("150.00"));

		when(productService.createProduct(any(CreateProductDTO.class))).thenReturn(productDTO);

		mockMvc.perform(post("/api/v1/product").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(createProductDTO))).andExpect(status().isCreated())
				.andExpect(jsonPath("$.productName").value("New Product"));

		verify(productService, times(1)).createProduct(any(CreateProductDTO.class));
	}

	@Test
	void testUpdateProduct() throws Exception {
		CreateProductDTO updateProductDTO = new CreateProductDTO();
		updateProductDTO.setProductName("Updated Product");
		updateProductDTO.setDescription("Updated Description");
		updateProductDTO.setPrice(new BigDecimal("200.00"));
		updateProductDTO.setSku("UPDATEDSKU");

		ProductDTO updatedProductDTO = new ProductDTO();
		updatedProductDTO.setId(1);
		updatedProductDTO.setProductName("Updated Product");
		updatedProductDTO.setDescription("Updated Description");
		updatedProductDTO.setPrice(new BigDecimal("200.00"));

		when(productService.updateProduct(eq(1), any(CreateProductDTO.class))).thenReturn(updatedProductDTO);

		mockMvc.perform(put("/api/v1/product/1").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(updateProductDTO))).andExpect(status().isOk())
				.andExpect(jsonPath("$.productName").value("Updated Product"));

		verify(productService, times(1)).updateProduct(eq(1), any(CreateProductDTO.class));
	}

	@Test
	void testDeleteProduct() throws Exception {
		doNothing().when(productService).softDeleteProduct(1);

		mockMvc.perform(delete("/api/v1/product/1")).andExpect(status().isNoContent());

		verify(productService, times(1)).softDeleteProduct(1);
	}

	@Test
	void testCheckProductDeletedByProductId() throws Exception {
		String productId = "PROD123";
		when(productService.checkProductDeletedByProductId(productId)).thenReturn(true);

		mockMvc.perform(get("/api/v1/product/productId/deleted/" + productId))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$").value(true));

		verify(productService, times(1)).checkProductDeletedByProductId(productId);
	}

	@Test
	void testCheckProductDeletedById() throws Exception {
		Integer id = 1;
		when(productService.checkProductDeletedById(id)).thenReturn(false);

		mockMvc.perform(get("/api/v1/product/id/deleted/" + id))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$").value(false));

		verify(productService, times(1)).checkProductDeletedById(id);
	}

	@Test
	void testCheckProductEnabledByProductId() throws Exception {
		String productId = "PROD123";
		when(productService.checkProductEnabledByProductId(productId)).thenReturn(true);

		mockMvc.perform(get("/api/v1/product/productId/enabled/" + productId))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$").value(true));

		verify(productService, times(1)).checkProductEnabledByProductId(productId);
	}

	@Test
	void testCheckProductEnabledById() throws Exception {
		Integer id = 1;
		when(productService.checkProductEnabledById(id)).thenReturn(true);

		mockMvc.perform(get("/api/v1/product/id/enabled/" + id))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$").value(true));

		verify(productService, times(1)).checkProductEnabledById(id);
	}
}
