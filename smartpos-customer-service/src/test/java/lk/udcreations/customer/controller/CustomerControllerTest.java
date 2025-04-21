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

import lk.udcreations.common.dto.customer.CustomerDTO;
import lk.udcreations.common.dto.customergroup.CustomerGroupDTO;
import lk.udcreations.customer.entity.Customer;
import lk.udcreations.customer.entity.CustomerGroup;
import lk.udcreations.customer.service.CustomerService;

class CustomerControllerTest {

	@Mock
	private CustomerService customerService;

	@InjectMocks
	private CustomerController customerController;

	private MockMvc mockMvc;
	private ObjectMapper objectMapper;

	CustomerDTO customer1;
	CustomerDTO customer2;
	CustomerGroupDTO cGroup;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		mockMvc = MockMvcBuilders.standaloneSetup(customerController).build();
		objectMapper = new ObjectMapper();
		objectMapper.findAndRegisterModules(); // Register Java 8 time support

		cGroup = new CustomerGroupDTO();
		cGroup.setCustomerGroupId(1);
		cGroup.setName("Group Name");
		cGroup.setDescription("Description");

	}

	@Test
	void testGetAllCustomers() throws Exception {

		customer1 = new CustomerDTO(cGroup, 1, "john_doe", "John", "Doe", "john@example.com", "1234567890",
				"123 Street", true, false, false, LocalDateTime.now(), null, null, null, null, null);

		customer2 = new CustomerDTO(cGroup, 2, "jane_doe", "Jane", "Doe", "jane@example.com", "9876543210",
				"456 Avenue", true, false, false, LocalDateTime.now(), null, null, null, null, null);

		List<CustomerDTO> customers = Arrays.asList(customer1, customer2);

		when(customerService.getAllCustomer()).thenReturn(customers);

		mockMvc.perform(get("/api/v1/customers/all")).andExpect(status().isOk())
				.andExpect(jsonPath("$.size()").value(2)).andExpect(jsonPath("$[0].username").value("john_doe"))
				.andExpect(jsonPath("$[1].username").value("jane_doe"));

		verify(customerService, times(1)).getAllCustomer();
	}

	@Test
	void testGetAllExistCustomers() throws Exception {
		customer1 = new CustomerDTO(cGroup, 1, "john_doe", "John", "Doe", "john@example.com", "1234567890",
				"123 Street", true, false, false, LocalDateTime.now(), null, null, null, null, null);

		customer2 = new CustomerDTO(cGroup, 2, "jane_doe", "Jane", "Doe", "jane@example.com", "9876543210",
				"456 Avenue", true, false, false, LocalDateTime.now(), null, null, null, null, null);

		List<CustomerDTO> customers = Arrays.asList(customer1, customer2);

		when(customerService.getAllExistCustomers()).thenReturn(customers);

		mockMvc.perform(get("/api/v1/customers")).andExpect(status().isOk())
				.andExpect(jsonPath("$.size()").value(2)).andExpect(jsonPath("$[0].username").value("john_doe"))
				.andExpect(jsonPath("$[1].username").value("jane_doe"));

		verify(customerService, times(1)).getAllExistCustomers();
	}

	@Test
	void testGetCustomerById() throws Exception {

		CustomerDTO customer = new CustomerDTO(cGroup, 1, "john_doe", "John", "Doe", "john@example.com", "1234567890",
				"123 Street", true, false, false, LocalDateTime.now(), null, null, null, null, null);

		when(customerService.getCustomerById(1)).thenReturn(customer);

		mockMvc.perform(get("/api/v1/customers/1")).andExpect(status().isOk())
				.andExpect(jsonPath("$.username").value("john_doe"));

		verify(customerService, times(1)).getCustomerById(1);
	}

	@Test
	void testGetCustomerByUsername() throws Exception {
		CustomerDTO customer = new CustomerDTO(cGroup, 1, "john_doe", "John", "Doe", "john@example.com", "1234567890",
				"123 Street", true, false, false, LocalDateTime.now(), null, null, null, null, null);

		when(customerService.getCustomerByUserName("john_doe")).thenReturn(customer);

		mockMvc.perform(get("/api/v1/customers/username/john_doe")).andExpect(status().isOk())
				.andExpect(jsonPath("$.customerId").value(1))
				.andExpect(jsonPath("$.firstName").value("John"))
				.andExpect(jsonPath("$.lastName").value("Doe"));

		verify(customerService, times(1)).getCustomerByUserName("john_doe");
	}

	@Test
	void testCreateCustomer() throws Exception {

		CustomerGroup cGroup1 = new CustomerGroup(1);

		Customer newCustomer = new Customer(null, 1, "new_user", "New", "User", "new@example.com", "1112223333",
				"789 Road", true, false, false, null, null, null, null, null, null);

		CustomerDTO createdCustomer = new CustomerDTO(cGroup, 1, "new_user", "New", "User", "new@example.com",
				"1112223333", "789 Road", true, false, false, null, null, null, null, null, null);

		when(customerService.createCustomer(any(Customer.class))).thenReturn(createdCustomer);

		mockMvc.perform(post("/api/v1/customers").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(newCustomer))).andExpect(status().isCreated())
				.andExpect(jsonPath("$.username").value("new_user"));

		verify(customerService, times(1)).createCustomer(any(Customer.class));
	}

	@Test
	void testUpdateCustomer() throws Exception {

		Customer updatedCustomer = new Customer(null, 1, "updated_user", "Updated", "User", "updated@example.com",
				"4445556666", "999 Blvd", true, false, false, null, null, null, null, null, null);

		CustomerDTO updatedCustomerDTO = new CustomerDTO(cGroup, 1, "updated_user", "Updated", "User",
				"updated@example.com", "4445556666", "999 Blvd", true, false, false, LocalDateTime.now(), null, null,
				null, null, null);

		when(customerService.updateCustomer(eq(1), any(Customer.class))).thenReturn(updatedCustomerDTO);

		mockMvc.perform(put("/api/v1/customers/1").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(updatedCustomer))).andExpect(status().isOk())
				.andExpect(jsonPath("$.username").value("updated_user"));

		verify(customerService, times(1)).updateCustomer(eq(1), any(Customer.class));
	}

	@Test
	void testDeleteCustomer() throws Exception {
		doNothing().when(customerService).softDeleteCustomer(1);

		mockMvc.perform(delete("/api/v1/customers/1")).andExpect(status().isNoContent());

		verify(customerService, times(1)).softDeleteCustomer(1);
	}
}
