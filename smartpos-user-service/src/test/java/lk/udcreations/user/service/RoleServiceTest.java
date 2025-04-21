package lk.udcreations.user.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import lk.udcreations.common.dto.role.RoleDTO;
import lk.udcreations.user.entity.Role;
import lk.udcreations.user.exception.NotFoundException;
import lk.udcreations.user.repository.RoleRepository;
import lk.udcreations.user.util.relationcheck.RoleCheck;

@ExtendWith(MockitoExtension.class)
public class RoleServiceTest {

	@Mock
	private RoleRepository roleRepository;

	@Mock
	private RoleCheck roleCheck;

	@Mock
	private ModelMapper modelMapper;

	@InjectMocks
	private RoleService roleService;

	private Role role1;
	private Role role2;
	private RoleDTO roleDTO1;
	private RoleDTO roleDTO2;

	@BeforeEach
	void setUp() {
		role1 = new Role();
		role1.setRoleId(1);
		role1.setRoleName("Admin");

		role2 = new Role();
		role2.setRoleId(2);
		role2.setRoleName("User");

		roleDTO1 = new RoleDTO();
		roleDTO1.setRoleId(1);
		roleDTO1.setRoleName("Admin");

		roleDTO2 = new RoleDTO();
		roleDTO2.setRoleId(2);
		roleDTO2.setRoleName("User");
	}

	@Test
	void testGetAllRoles() {
		when(roleRepository.findAll()).thenReturn(Arrays.asList(role1, role2));
		when(modelMapper.map(role1, RoleDTO.class)).thenReturn(roleDTO1);
		when(modelMapper.map(role2, RoleDTO.class)).thenReturn(roleDTO2);

		List<RoleDTO> roles = roleService.getAllRoles();

		assertEquals(2, roles.size());
		assertEquals("Admin", roles.get(0).getRoleName());
		assertEquals("User", roles.get(1).getRoleName());
	}

	@Test
	void testGetAllExistRoles() {
		// Setup roles with deleted flag
		role1.setDeleted(false);
		role2.setDeleted(false);
		
		when(roleRepository.findByDeletedFalse()).thenReturn(Arrays.asList(role1, role2));
		when(modelMapper.map(role1, RoleDTO.class)).thenReturn(roleDTO1);
		when(modelMapper.map(role2, RoleDTO.class)).thenReturn(roleDTO2);

		List<RoleDTO> roles = roleService.getAllExistRoles();

		assertEquals(2, roles.size());
		assertEquals("Admin", roles.get(0).getRoleName());
		assertEquals("User", roles.get(1).getRoleName());
		verify(roleRepository, times(1)).findByDeletedFalse();
	}

	@Test
	void testGetAllExistRoles_NoRolesFound() {
		when(roleRepository.findByDeletedFalse()).thenReturn(List.of());

		List<RoleDTO> roles = roleService.getAllExistRoles();

		assertTrue(roles.isEmpty());
		verify(roleRepository, times(1)).findByDeletedFalse();
	}

	@Test
	void testGetRoleById_RoleExists() {
		Integer roleId = 1;
		when(roleRepository.findById(roleId)).thenReturn(Optional.of(role1));
		when(modelMapper.map(role1, RoleDTO.class)).thenReturn(roleDTO1);

		RoleDTO roleDTO = roleService.getRoleById(roleId);

		assertNotNull(roleDTO);
		assertEquals("Admin", roleDTO.getRoleName());

		verify(roleRepository, times(1)).findById(roleId);
	}

	@Test
	void testGetRoleById_RoleNotFound() {
		Integer roleId = 1;
		when(roleRepository.findById(roleId)).thenReturn(Optional.empty());

		Exception exception = assertThrows(NotFoundException.class, () -> roleService.getRoleById(roleId));

		assertTrue(exception.getMessage().contains("Role not found"));
		verify(roleRepository, times(1)).findById(roleId);
	}

	@Test
	void testCreateRole_NewRole() {
		Role newRole = new Role();
		newRole.setRoleName("Manager");

		Role savedRole = new Role();
		savedRole.setRoleId(3);
		savedRole.setRoleName("Manager");

		RoleDTO savedRoleDTO = new RoleDTO();
		savedRoleDTO.setRoleId(3);
		savedRoleDTO.setRoleName("Manager");

		when(roleCheck.isRoleNameExists(newRole)).thenReturn(Boolean.FALSE);
		when(roleRepository.findByRoleNameAndDeletedTrue(newRole.getRoleName())).thenReturn(Optional.empty());
		when(roleRepository.save(newRole)).thenReturn(savedRole);
		when(modelMapper.map(savedRole, RoleDTO.class)).thenReturn(savedRoleDTO);

		RoleDTO roleDTO = roleService.createRole(newRole);

		assertNotNull(roleDTO);
		assertEquals("Manager", roleDTO.getRoleName());
		verify(roleCheck, times(1)).isRoleNameExists(newRole);
		verify(roleRepository, times(1)).save(newRole);
	}

	@Test
	void testCreateRole_SoftDeletedRoleExists() {
		Role newRole = new Role();
		newRole.setRoleName("Manager");

		Role softDeletedRole = new Role();
		softDeletedRole.setRoleId(3);
		softDeletedRole.setRoleName("Manager");
		softDeletedRole.setDeleted(true);

		RoleDTO softDeletedRoleDTO = new RoleDTO();
		softDeletedRoleDTO.setRoleId(3);
		softDeletedRoleDTO.setRoleName("Manager");

		when(roleCheck.isRoleNameExists(newRole)).thenReturn(Boolean.FALSE);
		when(roleRepository.findByRoleNameAndDeletedTrue("Manager")).thenReturn(Optional.of(softDeletedRole));
		when(roleRepository.save(any(Role.class))).thenReturn(softDeletedRole);
		when(modelMapper.map(softDeletedRole, RoleDTO.class)).thenReturn(softDeletedRoleDTO);

		RoleDTO roleDTO = roleService.createRole(newRole);

		assertNotNull(roleDTO);
		assertEquals("Manager", roleDTO.getRoleName());
		verify(roleRepository, times(1)).findByRoleNameAndDeletedTrue("Manager");
		verify(roleRepository, times(1)).save(any(Role.class));
	}

	@Test
	void testUpdateRole_RoleExists() {
		Integer roleId = 1;
		
		Role existingRole = new Role();
		existingRole.setRoleId(1);
		existingRole.setRoleName("Admin");

		Role updatedRole = new Role();
		updatedRole.setRoleId(1);
		updatedRole.setRoleName("Super Admin");

		RoleDTO updatedRoleDTO = new RoleDTO();
		updatedRoleDTO.setRoleId(1);
		updatedRoleDTO.setRoleName("Super Admin");

		when(roleRepository.findById(roleId)).thenReturn(Optional.of(existingRole));
		when(roleRepository.save(any(Role.class))).thenReturn(updatedRole);
		when(modelMapper.map(updatedRole, RoleDTO.class)).thenReturn(updatedRoleDTO);

		RoleDTO roleDTO = roleService.updateRole(roleId, updatedRole);

		assertNotNull(roleDTO);
		assertEquals("Super Admin", roleDTO.getRoleName());
		verify(roleRepository, times(1)).findById(roleId);
		verify(roleRepository, times(1)).save(existingRole);
	}

	@Test
	void testUpdateRole_RoleNotFound() {
		Integer roleId = 1;
		
		Role updatedRole = new Role();
		updatedRole.setRoleId(1);
		updatedRole.setRoleName("Super Admin");

		when(roleRepository.findById(roleId)).thenReturn(Optional.empty());

		Exception exception = assertThrows(NotFoundException.class, () -> roleService.updateRole(roleId, updatedRole));

		assertTrue(exception.getMessage().contains("Role not found"));
		verify(roleRepository, times(1)).findById(roleId);
	}

	@Test
	void testSoftDeleteRole_RoleExists() {
		Integer roleId = 1;
		
		Role existingRole = new Role();
		existingRole.setRoleId(1);
		existingRole.setRoleName("Admin");
		existingRole.setDeleted(false);
		existingRole.setEnabled(true);

		when(roleRepository.findById(roleId)).thenReturn(Optional.of(existingRole));

		roleService.softDeleteRole(roleId);

		assertTrue(existingRole.isDeleted());
		assertFalse(existingRole.isEnabled());
		verify(roleRepository, times(1)).findById(roleId);
		verify(roleRepository, times(1)).save(existingRole);
	}

	@Test
	void testSoftDeleteRole_RoleNotFound() {
		Integer roleId = 1;
		
		when(roleRepository.findById(roleId)).thenReturn(Optional.empty());

		Exception exception = assertThrows(NotFoundException.class, () -> roleService.softDeleteRole(roleId));

		assertTrue(exception.getMessage().contains("Role not found"));
		verify(roleRepository, times(1)).findById(roleId);
	}

	@Test
	void testDeleteRole_RoleExists() {
		Integer roleId = 1;
		
		when(roleRepository.findById(roleId)).thenReturn(Optional.of(role1));

		roleService.deleteRole(roleId);

		verify(roleRepository, times(1)).findById(roleId);
		verify(roleRepository, times(1)).delete(role1);
	}

	@Test
	void testDeleteRole_RoleNotFound() {
		Integer roleId = 1;
		
		when(roleRepository.findById(roleId)).thenReturn(Optional.empty());

		Exception exception = assertThrows(NotFoundException.class, () -> roleService.deleteRole(roleId));

		assertTrue(exception.getMessage().contains("Role not found"));
		verify(roleRepository, times(1)).findById(roleId);
		verify(roleRepository, times(0)).delete(any(Role.class));
	}
}