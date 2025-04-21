package lk.udcreations.customer.controller;

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

import lk.udcreations.customer.controller.CustomergroupController;
import lk.udcreations.common.dto.customergroup.CustomerGroupDTO;
import lk.udcreations.customer.entity.CustomerGroup;
import lk.udcreations.customer.service.CustomerGroupService;

class CustomergroupControllerTest {

	@Mock
	private CustomerGroupService customerGroupService;

	@InjectMocks
	private CustomergroupController customergroupController;

	private MockMvc mockMvc;
	private ObjectMapper objectMapper;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		mockMvc = MockMvcBuilders.standaloneSetup(customergroupController).build();
		objectMapper = new ObjectMapper();
		objectMapper.findAndRegisterModules(); // Register Java 8 date/time support
	}

	@Test
    void testGetAllCustomerGroups() throws Exception {

		CustomerGroupDTO group1 = new CustomerGroupDTO(1, "VIP Customers", "High-value customers", true, false,
				LocalDateTime.now(), null, null, null, null, null);
		CustomerGroupDTO group2 = new CustomerGroupDTO(2, "Regular Customers", "Normal customers", true, false,
				LocalDateTime.now(), null, null, null, null, null);

        List<CustomerGroupDTO> customerGroups = Arrays.asList(group1, group2);

        when(customerGroupService.getAllCustomerGroups()).thenReturn(customerGroups);

        mockMvc.perform(get("/api/v1/customergroup/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].name").value("VIP Customers"))
                .andExpect(jsonPath("$[1].name").value("Regular Customers"));

        verify(customerGroupService, times(1)).getAllCustomerGroups();
    }

    @Test
    void testGetAllExistCustomerGroups() throws Exception {
        CustomerGroupDTO group1 = new CustomerGroupDTO(1, "VIP Customers", "High-value customers", true, false,
                LocalDateTime.now(), null, null, null, null, null);
        CustomerGroupDTO group2 = new CustomerGroupDTO(2, "Regular Customers", "Normal customers", true, false,
                LocalDateTime.now(), null, null, null, null, null);

        List<CustomerGroupDTO> customerGroups = Arrays.asList(group1, group2);

        when(customerGroupService.getAllExistCustomerGroups()).thenReturn(customerGroups);

        mockMvc.perform(get("/api/v1/customergroup"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].name").value("VIP Customers"))
                .andExpect(jsonPath("$[1].name").value("Regular Customers"));

        verify(customerGroupService, times(1)).getAllExistCustomerGroups();
    }

	@Test
    void testGetCustomerGroupById() throws Exception {

		CustomerGroupDTO group = new CustomerGroupDTO(1, "VIP Customers", "High-value customers", true, false,
                LocalDateTime.now(), null, null, null, null, null);

        when(customerGroupService.getCustomerGroupById(1)).thenReturn(group);

        mockMvc.perform(get("/api/v1/customergroup/1"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.name").value("VIP Customers"));

		verify(customerGroupService, times(1)).getCustomerGroupById(1);
	}

	@Test
	void testCreateCustomerGroup() throws Exception {

		CustomerGroup newGroup = new CustomerGroup(null, "Gold Members", "Premium customers", true, false, null, null,
				null, null, null, null);

		CustomerGroupDTO createdGroup = new CustomerGroupDTO(1, "Gold Members", "Premium customers", true, false,
				LocalDateTime.now(), null, null, null, null, null);

		when(customerGroupService.createCustomerGroup(any(CustomerGroup.class))).thenReturn(createdGroup);

		mockMvc.perform(post("/api/v1/customergroup")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(newGroup))).andExpect(status().isCreated())
				.andExpect(jsonPath("$.name").value("Gold Members"));

		verify(customerGroupService, times(1)).createCustomerGroup(any(CustomerGroup.class));
	}

	@Test
	void testUpdateCustomerGroup() throws Exception {
		CustomerGroup updatedGroup = new CustomerGroup(null, "Updated VIP", "Updated description", true, false, null,
				null, null, null, null, null);

		CustomerGroupDTO updatedGroupDTO = new CustomerGroupDTO(1, "Updated VIP", "Updated description", true, false,
				LocalDateTime.now(), null, null, null, null, null);

		when(customerGroupService.updateCustomerGroup(eq(1), any(CustomerGroup.class))).thenReturn(updatedGroupDTO);

		mockMvc.perform(put("/api/v1/customergroup/1")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(updatedGroup))).andExpect(status().isOk())
				.andExpect(jsonPath("$.name").value("Updated VIP"));

		verify(customerGroupService, times(1)).updateCustomerGroup(eq(1), any(CustomerGroup.class));
	}

	@Test
	void testDeleteCustomerGroup() throws Exception {
		doNothing().when(customerGroupService).softDeleteCustomerGroup(1);

		mockMvc.perform(delete("/api/v1/customergroup/1")).andExpect(status().isNoContent());

		verify(customerGroupService, times(1)).softDeleteCustomerGroup(1);
	}
}
