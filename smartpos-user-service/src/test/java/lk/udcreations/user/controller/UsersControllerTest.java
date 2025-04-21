package lk.udcreations.user.controller;

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

import lk.udcreations.common.dto.user.UsersDTO;
import lk.udcreations.user.entity.Role;
import lk.udcreations.user.entity.Users;
import lk.udcreations.user.service.UsersService;

class UsersControllerTest {

	@Mock
	private UsersService usersService;

	@InjectMocks
	private UsersController usersController;

	private MockMvc mockMvc;
	private ObjectMapper objectMapper;
	private Users user;
	private UsersDTO userDTO;
	private Role role;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		mockMvc = MockMvcBuilders.standaloneSetup(usersController).build();
		objectMapper = new ObjectMapper();
		objectMapper.findAndRegisterModules(); // Java 8 date/time module support

		// Common Role object
		role = new Role();
		role.setRoleId(1);
		role.setRoleName("Admin");
		role.setDescription("Administrator role");
		role.setEnabled(true);
		role.setCreatedAt(LocalDateTime.now());
		role.setDeleted(false);

		// Common User object
		user = new Users();
		user.setUserId(1);
		user.setUsername("john_doe");
		user.setFirstName("John");
		user.setLastName("Doe");
		user.setEmail("john@example.com");
		user.setPhoneNo1("1234567890");
		user.setAddress("123 Street");
		user.setRoleId(1);
		user.setEnabled(true);
		user.setDeleted(false);
		user.setCreatedAt(LocalDateTime.now());

		// Common UsersDTO object
		userDTO = new UsersDTO();
		userDTO.setUserId(1);
		userDTO.setUsername("john_doe");
		userDTO.setFirstName("John");
		userDTO.setLastName("Doe");
		userDTO.setEmail("john@example.com");
		userDTO.setPhoneNo1("1234567890");
		userDTO.setAddress("123 Street");
		userDTO.setEnabled(true);
		userDTO.setDeleted(false);
		userDTO.setCreatedAt(LocalDateTime.now());
	}

	@Test
	void testGetAllUsers() throws Exception {
		UsersDTO user2 = new UsersDTO();
		user2.setUserId(2);
		user2.setUsername("jane_doe");
		user2.setFirstName("Jane");
		user2.setLastName("Doe");
		user2.setEmail("jane@example.com");
		user2.setPhoneNo1("9876543210");
		user2.setAddress("456 Avenue");
		user2.setEnabled(true);
		user2.setDeleted(false);
		user2.setCreatedAt(LocalDateTime.now());

		List<UsersDTO> users = Arrays.asList(userDTO, user2);

		when(usersService.getAllUsers()).thenReturn(users);

		mockMvc.perform(get("/api/v1/users/all")).andExpect(status().isOk()).andExpect(jsonPath("$.size()").value(2))
				.andExpect(jsonPath("$[0].username").value("john_doe"))
				.andExpect(jsonPath("$[1].username").value("jane_doe"));

		verify(usersService, times(1)).getAllUsers();
	}

	@Test
	void testGetAllExistUsers() throws Exception {
		UsersDTO user2 = new UsersDTO();
		user2.setUserId(2);
		user2.setUsername("jane_doe");
		user2.setFirstName("Jane");
		user2.setLastName("Doe");
		user2.setEmail("jane@example.com");
		user2.setPhoneNo1("9876543210");
		user2.setAddress("456 Avenue");
		user2.setEnabled(true);
		user2.setDeleted(false);
		user2.setCreatedAt(LocalDateTime.now());

		List<UsersDTO> users = Arrays.asList(userDTO, user2);

		when(usersService.getAllExistUsers()).thenReturn(users);

		mockMvc.perform(get("/api/v1/users")).andExpect(status().isOk()).andExpect(jsonPath("$.size()").value(2))
				.andExpect(jsonPath("$[0].username").value("john_doe"))
				.andExpect(jsonPath("$[1].username").value("jane_doe"));

		verify(usersService, times(1)).getAllExistUsers();
	}

	@Test
	void testGetUserById() throws Exception {
		when(usersService.getUserById(1)).thenReturn(userDTO);

		mockMvc.perform(get("/api/v1/users/1")).andExpect(status().isOk())
				.andExpect(jsonPath("$.username").value("john_doe"));

		verify(usersService, times(1)).getUserById(1);
	}

	@Test
	void testGetUserByUsername() throws Exception {
		when(usersService.getUserUsername("john_doe")).thenReturn(userDTO);

		mockMvc.perform(get("/api/v1/users/username/john_doe")).andExpect(status().isOk())
				.andExpect(jsonPath("$.username").value("john_doe"))
				.andExpect(jsonPath("$.firstName").value("John"))
				.andExpect(jsonPath("$.lastName").value("Doe"))
				.andExpect(jsonPath("$.email").value("john@example.com"));

		verify(usersService, times(1)).getUserUsername("john_doe");
	}

	@Test
	void testCreateUser() throws Exception {
		when(usersService.createUser(any(Users.class))).thenReturn(userDTO);

		mockMvc.perform(post("/api/v1/users").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(user))).andExpect(status().isCreated())
				.andExpect(jsonPath("$.username").value("john_doe"));

		verify(usersService, times(1)).createUser(any(Users.class));
	}

	@Test
	void testUpdateUser() throws Exception {
		user.setUsername("updated_user");
		user.setEmail("updated@example.com");

		userDTO.setUsername("updated_user");
		userDTO.setEmail("updated@example.com");

		when(usersService.updateUser(eq(1), any(Users.class))).thenReturn(userDTO);

		mockMvc.perform(put("/api/v1/users/1").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(user))).andExpect(status().isOk())
				.andExpect(jsonPath("$.username").value("updated_user"));

		verify(usersService, times(1)).updateUser(eq(1), any(Users.class));
	}

	@Test
	void testDeleteUser() throws Exception {
		doNothing().when(usersService).deleteUser(1);

		mockMvc.perform(delete("/api/v1/users/1")).andExpect(status().isNoContent());

		verify(usersService, times(1)).deleteUser(1);
	}
}
