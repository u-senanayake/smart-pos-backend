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

import lk.udcreations.common.dto.role.RoleDTO;
import lk.udcreations.user.entity.Role;
import lk.udcreations.user.service.RoleService;

class RoleControllerTest {

	@Mock
	private RoleService roleService;

	@InjectMocks
	private RoleController roleController;

	private MockMvc mockMvc;
	private ObjectMapper objectMapper;
	private Role role;
	private RoleDTO roleDTO;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		mockMvc = MockMvcBuilders.standaloneSetup(roleController).build();
		objectMapper = new ObjectMapper();
		objectMapper.findAndRegisterModules(); // Java 8 time module support

		// Common Role object
		role = new Role();
		role.setRoleId(1);
		role.setRoleName("Admin");
		role.setDescription("Administrator role");
		role.setEnabled(true);
		role.setCreatedAt(LocalDateTime.now());
		role.setDeleted(false);

		// Common RoleDTO object
		roleDTO = new RoleDTO();
		roleDTO.setRoleId(1);
		roleDTO.setRoleName("Admin");
		roleDTO.setDescription("Administrator role");
		roleDTO.setEnabled(true);
		roleDTO.setDeleted(false);
		roleDTO.setCreatedAt(LocalDateTime.now());
	}

	@Test
	void testGetAllRoles() throws Exception {
		RoleDTO role2 = new RoleDTO();
		role2.setRoleId(2);
		role2.setRoleName("User");
		role2.setDescription("Standard user role");
		role2.setEnabled(true);
		role2.setDeleted(false);
		role2.setCreatedAt(LocalDateTime.now());

		List<RoleDTO> roles = Arrays.asList(roleDTO, role2);

		when(roleService.getAllRoles()).thenReturn(roles);

		mockMvc.perform(get("/api/v1/role/all")).andExpect(status().isOk()).andExpect(jsonPath("$.size()").value(2))
				.andExpect(jsonPath("$[0].roleName").value("Admin")).andExpect(jsonPath("$[1].roleName").value("User"));

		verify(roleService, times(1)).getAllRoles();
	}

	@Test
	void testGetAllExistRoles() throws Exception {
		RoleDTO role2 = new RoleDTO();
		role2.setRoleId(2);
		role2.setRoleName("User");
		role2.setDescription("Standard user role");
		role2.setEnabled(true);
		role2.setDeleted(false);
		role2.setCreatedAt(LocalDateTime.now());

		List<RoleDTO> roles = Arrays.asList(roleDTO, role2);

		when(roleService.getAllExistRoles()).thenReturn(roles);

		mockMvc.perform(get("/api/v1/role")).andExpect(status().isOk()).andExpect(jsonPath("$.size()").value(2))
				.andExpect(jsonPath("$[0].roleName").value("Admin")).andExpect(jsonPath("$[1].roleName").value("User"));

		verify(roleService, times(1)).getAllExistRoles();
	}

	@Test
	void testGetRoleById() throws Exception {
		when(roleService.getRoleById(1)).thenReturn(roleDTO);

		mockMvc.perform(get("/api/v1/role/1")).andExpect(status().isOk())
				.andExpect(jsonPath("$.roleName").value("Admin"));

		verify(roleService, times(1)).getRoleById(1);
	}

	@Test
	void testCreateRole() throws Exception {
		when(roleService.createRole(any(Role.class))).thenReturn(roleDTO);

		mockMvc.perform(post("/api/v1/role").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(role))).andExpect(status().isCreated())
				.andExpect(jsonPath("$.roleName").value("Admin"));

		verify(roleService, times(1)).createRole(any(Role.class));
	}

	@Test
	void testUpdateRole() throws Exception {
		role.setRoleName("Updated Admin");
		role.setDescription("Updated description");

		roleDTO.setRoleName("Updated Admin");
		roleDTO.setDescription("Updated description");

		when(roleService.updateRole(eq(1), any(Role.class))).thenReturn(roleDTO);

		mockMvc.perform(put("/api/v1/role/1").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(role))).andExpect(status().isOk())
				.andExpect(jsonPath("$.roleName").value("Updated Admin"));

		verify(roleService, times(1)).updateRole(eq(1), any(Role.class));
	}

	@Test
	void testDeleteRole() throws Exception {
		doNothing().when(roleService).softDeleteRole(1);

		mockMvc.perform(delete("/api/v1/role/1")).andExpect(status().isNoContent());

		verify(roleService, times(1)).softDeleteRole(1);
	}
}
