package lk.udcreations.customer.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lk.udcreations.common.dto.customer.CustomerDTO;
import lk.udcreations.common.dto.customergroup.CustomerGroupDTO;
import lk.udcreations.customer.entity.Customer;
import lk.udcreations.customer.service.CustomerService;
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

		customer1 = new CustomerDTO();
		customer1.setCustomerGroup(cGroup);
		customer1.setCustomerId(1);
		customer1.setUsername("john_doe");
		customer1.setFirstName("John");
		customer1.setLastName("Doe");
		customer1.setEmail("john@example.com");
		customer1.setPhoneNo1("1234567890");
		customer1.setAddress("123 Street");
		customer1.setEnabled(true);
		customer1.setDeleted(false);
		customer1.setLocked(false);
		customer1.setCreatedAt(LocalDateTime.now());

		customer2 = new CustomerDTO();
		customer2.setCustomerGroup(cGroup);
		customer2.setCustomerId(2);
		customer2.setUsername("jane_doe");
		customer2.setFirstName("Jane");
		customer2.setLastName("Doe");
		customer2.setEmail("jane@example.com");
		customer2.setPhoneNo1("9876543210");
		customer2.setAddress("456 Avenue");
		customer2.setEnabled(true);
		customer2.setDeleted(false);
		customer2.setLocked(false);
		customer2.setCreatedAt(LocalDateTime.now());
	}

	@Test
	void testGetAllCustomers() throws Exception {

		List<CustomerDTO> customers = Arrays.asList(customer1, customer2);

		when(customerService.getAllCustomer()).thenReturn(customers);

		mockMvc.perform(get("/api/v1/customers/all")).andExpect(status().isOk())
				.andExpect(jsonPath("$.size()").value(2)).andExpect(jsonPath("$[0].username").value("john_doe"))
				.andExpect(jsonPath("$[1].username").value("jane_doe"));

		verify(customerService, times(1)).getAllCustomer();
	}

	@Test
	void testGetAllExistCustomers() throws Exception {

		List<CustomerDTO> customers = Arrays.asList(customer1, customer2);

		when(customerService.getAllExistCustomers()).thenReturn(customers);

		mockMvc.perform(get("/api/v1/customers")).andExpect(status().isOk())
				.andExpect(jsonPath("$.size()").value(2)).andExpect(jsonPath("$[0].username").value("john_doe"))
				.andExpect(jsonPath("$[1].username").value("jane_doe"));

		verify(customerService, times(1)).getAllExistCustomers();
	}

	@Test
	void testGetCustomerById() throws Exception {

		when(customerService.getCustomerById(1)).thenReturn(customer1);

		mockMvc.perform(get("/api/v1/customers/1")).andExpect(status().isOk())
				.andExpect(jsonPath("$.username").value("john_doe"));

		verify(customerService, times(1)).getCustomerById(1);
	}

	@Test
	void testGetCustomerByUsername() throws Exception {

		when(customerService.getCustomerByUserName("john_doe")).thenReturn(customer1);

		mockMvc.perform(get("/api/v1/customers/username/john_doe")).andExpect(status().isOk())
				.andExpect(jsonPath("$.customerId").value(1))
				.andExpect(jsonPath("$.firstName").value("John"))
				.andExpect(jsonPath("$.lastName").value("Doe"));

		verify(customerService, times(1)).getCustomerByUserName("john_doe");
	}

	@Test
	void testCreateCustomer() throws Exception {

		CustomerGroupDTO cGroup1 = new CustomerGroupDTO();
		cGroup1.setCustomerGroupId(1);

		Customer newCustomer = new Customer();
		newCustomer.setCustomerGroupId(1);
		newCustomer.setUsername("new_user");
		newCustomer.setFirstName("New");
		newCustomer.setLastName("User");
		newCustomer.setEmail("");
		newCustomer.setPhoneNo1("1112223333");
		newCustomer.setAddress("789 Road");
		newCustomer.setEnabled(true);
		newCustomer.setDeleted(false);
		newCustomer.setLocked(false);

		CustomerDTO createdCustomer = new CustomerDTO();
		createdCustomer.setCustomerGroup(cGroup1);
		createdCustomer.setCustomerId(1);
		createdCustomer.setUsername("new_user");
		createdCustomer.setFirstName("New");
		createdCustomer.setLastName("User");
		createdCustomer.setEmail("");
		createdCustomer.setPhoneNo1("1112223333");
		createdCustomer.setAddress("789 Road");
		createdCustomer.setEnabled(true);
		createdCustomer.setDeleted(false);
		createdCustomer.setLocked(false);

		when(customerService.createCustomer(any(Customer.class), any())).thenReturn(createdCustomer);

		mockMvc.perform(post("/api/v1/customers").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(newCustomer))).andExpect(status().isCreated())
				.andExpect(jsonPath("$.username").value("new_user"));

		verify(customerService, times(1)).createCustomer(any(Customer.class), any());
	}

	@Test
	void testUpdateCustomer() throws Exception {

		Customer updatedCustomer = new Customer();
		updatedCustomer.setCustomerGroupId(1);
		updatedCustomer.setUsername("updated_user");
		updatedCustomer.setFirstName("Updated");
		updatedCustomer.setLastName("User");
		updatedCustomer.setEmail("");
		updatedCustomer.setPhoneNo1("4445556666");
		updatedCustomer.setAddress("999 Blvd");
		updatedCustomer.setEnabled(true);
		updatedCustomer.setDeleted(false);
		updatedCustomer.setLocked(false);

		CustomerDTO updatedCustomerDTO = new CustomerDTO();
		updatedCustomerDTO.setCustomerGroup(cGroup);
		updatedCustomerDTO.setCustomerId(1);
		updatedCustomerDTO.setUsername("updated_user");
		updatedCustomerDTO.setFirstName("Updated");
		updatedCustomerDTO.setLastName("User");
		updatedCustomerDTO.setEmail("");
		updatedCustomerDTO.setPhoneNo1("4445556666");
		updatedCustomerDTO.setAddress("999 Blvd");
		updatedCustomerDTO.setEnabled(true);
		updatedCustomerDTO.setDeleted(false);
		updatedCustomerDTO.setLocked(false);
		updatedCustomerDTO.setCreatedAt(LocalDateTime.now());

		when(customerService.updateCustomer(eq(1), any(Customer.class), any())).thenReturn(updatedCustomerDTO);

		mockMvc.perform(put("/api/v1/customers/1").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(updatedCustomer))).andExpect(status().isOk())
				.andExpect(jsonPath("$.username").value("updated_user"));

		verify(customerService, times(1)).updateCustomer(eq(1), any(Customer.class), any());
	}

	/*@Test
	void testDeleteCustomer() throws Exception {
		doNothing().when(customerService).softDeleteCustomer(1, any());

		mockMvc.perform(delete("/api/v1/customers/1")).andExpect(status().isNoContent());

		verify(customerService, times(1)).softDeleteCustomer(1, any());
	}*/
}
