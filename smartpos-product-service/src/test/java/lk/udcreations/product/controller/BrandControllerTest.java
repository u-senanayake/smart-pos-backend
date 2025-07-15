package lk.udcreations.product.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lk.udcreations.common.dto.brand.BrandDTO;
import lk.udcreations.common.dto.user.UsersDTO;
import lk.udcreations.product.config.UserServiceClient;
import lk.udcreations.product.service.BrandService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class BrandControllerTest {

	@Mock
	private BrandService brandService;

	@InjectMocks
	private BrandController brandController;

	private MockMvc mockMvc;
	private ObjectMapper objectMapper;
	private final String adminUsername = "admin";
	private final Integer adminUserId = 999;
	@Mock
	UserServiceClient userServiceClient;
	String jwtToken = "Usena eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbl91c2VyIiwiZXhwIjoxNzUyNTcwODUwfQ.PQ7WueBT5-5PUPktMonbf5fDZNudkqowFT7eG-m6JNs";
	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		mockMvc = MockMvcBuilders.standaloneSetup(brandController).build();
		objectMapper = new ObjectMapper();
		objectMapper.findAndRegisterModules(); // Register modules for Java 8 Date/Time

		UsersDTO mockUser = new UsersDTO();
		mockUser.setUserId(adminUserId);

		when(userServiceClient.getUserDetails(adminUsername)).thenReturn(mockUser);
	}

	@Test
	void testGetAllBrands() throws Exception {
		BrandDTO brand1 = new BrandDTO();
		brand1.setBrandId(1);
		brand1.setName("Nike");
		brand1.setDescription("Sportswear brand");
		brand1.setEnabled(true);
		brand1.setDeleted(false);
		brand1.setCreatedAt(LocalDateTime.now());

		BrandDTO brand2 = new BrandDTO();
		brand2.setBrandId(2);
		brand2.setName("Adidas");
		brand2.setDescription("Another sportswear brand");
		brand2.setEnabled(true);
		brand2.setDeleted(false);
		brand2.setCreatedAt(LocalDateTime.now());

		List<BrandDTO> brands = Arrays.asList(brand1, brand2);

		when(brandService.getAllBrands()).thenReturn(brands);

		mockMvc.perform(get("/api/v1/brand/all")).andExpect(status().isOk()).andExpect(jsonPath("$.size()").value(2))
				.andExpect(jsonPath("$[0].name").value("Nike")).andExpect(jsonPath("$[1].name").value("Adidas"));

		verify(brandService, times(1)).getAllBrands();
	}

	@Test
	void testGetAllExistBrands() throws Exception {
		BrandDTO brand1 = new BrandDTO();
		brand1.setBrandId(1);
		brand1.setName("Nike");
		brand1.setDescription("Sportswear brand");
		brand1.setEnabled(true);
		brand1.setDeleted(false);
		brand1.setCreatedAt(LocalDateTime.now());

		BrandDTO brand2 = new BrandDTO();
		brand2.setBrandId(2);
		brand2.setName("Adidas");
		brand2.setDescription("Another sportswear brand");
		brand2.setEnabled(true);
		brand2.setDeleted(false);
		brand2.setCreatedAt(LocalDateTime.now());

		List<BrandDTO> brands = Arrays.asList(brand1, brand2);

		when(brandService.getAllExistBrands()).thenReturn(brands);

		mockMvc.perform(get("/api/v1/brand")).andExpect(status().isOk()).andExpect(jsonPath("$.size()").value(2))
				.andExpect(jsonPath("$[0].name").value("Nike")).andExpect(jsonPath("$[1].name").value("Adidas"));

		verify(brandService, times(1)).getAllExistBrands();
	}

	@Test
	void testGetBrandById() throws Exception {
		BrandDTO brand = new BrandDTO();
		brand.setBrandId(1);
		brand.setName("Nike");
		brand.setDescription("Sportswear brand");
		brand.setEnabled(true);
		brand.setDeleted(false);
		brand.setCreatedAt(LocalDateTime.now());

		when(brandService.getBrandById(1)).thenReturn(brand);

		mockMvc.perform(get("/api/v1/brand/1")).andExpect(status().isOk()).andExpect(jsonPath("$.name").value("Nike"));

		verify(brandService, times(1)).getBrandById(1);
	}

	@Test
	void testCreateBrand() throws Exception {
		BrandDTO brandDTO = new BrandDTO();
		brandDTO.setBrandId(1);
		brandDTO.setName("Nike");
		brandDTO.setDescription("Sportswear brand");
		brandDTO.setEnabled(true);
		brandDTO.setDeleted(false);
		brandDTO.setCreatedAt(LocalDateTime.now());

		when(brandService.createBrand(any(), any())).thenReturn(brandDTO);

		mockMvc.perform(post("/api/v1/brand").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(brandDTO))).andExpect(status().isCreated())
				.andExpect(jsonPath("$.name").value("Nike"));

		verify(brandService, times(1)).createBrand(any(), any());
	}

	@Test
	void testUpdateBrand() throws Exception {
		BrandDTO updatedBrandDTO = new BrandDTO();
		updatedBrandDTO.setBrandId(1);
		updatedBrandDTO.setName("Updated Nike");
		updatedBrandDTO.setDescription("Updated description");
		updatedBrandDTO.setEnabled(true);
		updatedBrandDTO.setDeleted(false);
		updatedBrandDTO.setUpdatedAt(LocalDateTime.now());

		when(brandService.updateBrand(eq(1), any(), any())).thenReturn(updatedBrandDTO);

		mockMvc.perform(put("/api/v1/brand/1").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(updatedBrandDTO))).andExpect(status().isOk())
				.andExpect(jsonPath("$.name").value("Updated Nike"));

		verify(brandService, times(1)).updateBrand(eq(1), any(), any());
	}

	/*@Test
	void testDeleteBrand() throws Exception {
		doNothing().when(brandService).softDeleteBrand(adminUserId, adminUsername);

//		mockMvc.perform(delete("/api/v1/brand/" + adminUserId)).andExpect(status().isNoContent());

		mockMvc.perform(
						delete("/api/v1/brand/" + adminUserId)
								.header("Auth", jwtToken)
								.principal(() -> adminUsername) // Simulates @AuthenticationPrincipal
				)
				.andExpect(status().isNoContent());

		verify(brandService, times(1)).softDeleteBrand(adminUserId, adminUsername);
	}*/
}
