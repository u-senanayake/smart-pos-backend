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

import lk.udcreations.product.controller.DistributorController;
import lk.udcreations.common.dto.distributor.DistributorDTO;
import lk.udcreations.product.service.DistributorService;

class DistributorControllerTest {

	@Mock
	private DistributorService distributorService;

	@InjectMocks
	private DistributorController distributorController;

	private MockMvc mockMvc;
	private ObjectMapper objectMapper;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		mockMvc = MockMvcBuilders.standaloneSetup(distributorController).build();
		objectMapper = new ObjectMapper();
		objectMapper.findAndRegisterModules();
	}

	@Test
	void testGetAllDistributors() throws Exception {
		DistributorDTO distributor1 = new DistributorDTO();
		distributor1.setDistributorId(1);
		distributor1.setCompanyName("Company A");
		distributor1.setEmail("companyA@example.com");
		distributor1.setPhoneNo1("1234567890");
		distributor1.setEnabled(true);
		distributor1.setDeleted(false);
		distributor1.setCreatedAt(LocalDateTime.now());

		DistributorDTO distributor2 = new DistributorDTO();
		distributor2.setDistributorId(2);
		distributor2.setCompanyName("Company B");
		distributor2.setEmail("companyB@example.com");
		distributor2.setPhoneNo1("0987654321");
		distributor2.setEnabled(true);
		distributor2.setDeleted(false);
		distributor2.setCreatedAt(LocalDateTime.now());

		List<DistributorDTO> distributors = Arrays.asList(distributor1, distributor2);

		when(distributorService.getAllDistributors()).thenReturn(distributors);

		mockMvc.perform(get("/api/v1/distributor/all")).andExpect(status().isOk())
				.andExpect(jsonPath("$.size()").value(2)).andExpect(jsonPath("$[0].companyName").value("Company A"))
				.andExpect(jsonPath("$[1].companyName").value("Company B"));

		verify(distributorService, times(1)).getAllDistributors();
	}

	@Test
	void testGetAllExistDistributors() throws Exception {
		DistributorDTO distributor1 = new DistributorDTO();
		distributor1.setDistributorId(1);
		distributor1.setCompanyName("Company A");
		distributor1.setEmail("companyA@example.com");
		distributor1.setPhoneNo1("1234567890");
		distributor1.setEnabled(true);
		distributor1.setDeleted(false);
		distributor1.setCreatedAt(LocalDateTime.now());

		DistributorDTO distributor2 = new DistributorDTO();
		distributor2.setDistributorId(2);
		distributor2.setCompanyName("Company B");
		distributor2.setEmail("companyB@example.com");
		distributor2.setPhoneNo1("0987654321");
		distributor2.setEnabled(true);
		distributor2.setDeleted(false);
		distributor2.setCreatedAt(LocalDateTime.now());

		List<DistributorDTO> distributors = Arrays.asList(distributor1, distributor2);

		when(distributorService.getAllExistDistributors()).thenReturn(distributors);

		mockMvc.perform(get("/api/v1/distributor")).andExpect(status().isOk())
				.andExpect(jsonPath("$.size()").value(2)).andExpect(jsonPath("$[0].companyName").value("Company A"))
				.andExpect(jsonPath("$[1].companyName").value("Company B"));

		verify(distributorService, times(1)).getAllExistDistributors();
	}

	@Test
	void testGetDistributorById() throws Exception {
		DistributorDTO distributor = new DistributorDTO();
		distributor.setDistributorId(1);
		distributor.setCompanyName("Company A");
		distributor.setEmail("companyA@example.com");
		distributor.setPhoneNo1("1234567890");
		distributor.setEnabled(true);
		distributor.setDeleted(false);
		distributor.setCreatedAt(LocalDateTime.now());

		when(distributorService.getDistributorById(1)).thenReturn(distributor);

		mockMvc.perform(get("/api/v1/distributor/1")).andExpect(status().isOk())
				.andExpect(jsonPath("$.companyName").value("Company A"));

		verify(distributorService, times(1)).getDistributorById(1);
	}

	@Test
	void testCreateDistributor() throws Exception {
		DistributorDTO distributorDTO = new DistributorDTO();
		distributorDTO.setDistributorId(1);
		distributorDTO.setCompanyName("Company A");
		distributorDTO.setEmail("companyA@example.com");
		distributorDTO.setPhoneNo1("1234567890");
		distributorDTO.setEnabled(true);
		distributorDTO.setDeleted(false);
		distributorDTO.setCreatedAt(LocalDateTime.now());

		when(distributorService.createDistributor(any())).thenReturn(distributorDTO);

		mockMvc.perform(post("/api/v1/distributor").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(distributorDTO))).andExpect(status().isCreated())
				.andExpect(jsonPath("$.companyName").value("Company A"));

		verify(distributorService, times(1)).createDistributor(any());
	}

	@Test
	void testUpdateDistributor() throws Exception {
		DistributorDTO updatedDistributorDTO = new DistributorDTO();
		updatedDistributorDTO.setDistributorId(1);
		updatedDistributorDTO.setCompanyName("Updated Company A");
		updatedDistributorDTO.setEmail("updatedA@example.com");
		updatedDistributorDTO.setPhoneNo1("1234567890");
		updatedDistributorDTO.setEnabled(true);
		updatedDistributorDTO.setDeleted(false);
		updatedDistributorDTO.setUpdatedAt(LocalDateTime.now());

		when(distributorService.updateDistributor(eq(1), any())).thenReturn(updatedDistributorDTO);

		mockMvc.perform(put("/api/v1/distributor/1").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(updatedDistributorDTO))).andExpect(status().isOk())
				.andExpect(jsonPath("$.companyName").value("Updated Company A"));

		verify(distributorService, times(1)).updateDistributor(eq(1), any());
	}

	@Test
	void testDeleteDistributor() throws Exception {
		doNothing().when(distributorService).softDeleteDistributor(1);

		mockMvc.perform(delete("/api/v1/distributor/1")).andExpect(status().isNoContent());

		verify(distributorService, times(1)).softDeleteDistributor(1);
	}
}
