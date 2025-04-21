package lk.udcreations.user.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.TestPropertySource;

import lk.udcreations.common.dto.role.RoleDTO;
import lk.udcreations.common.dto.user.CreatedUpdatedUserDTO;
import lk.udcreations.common.dto.user.UsersDTO;
import lk.udcreations.user.constants.ErrorMessages;
import lk.udcreations.user.entity.Role;
import lk.udcreations.user.entity.Users;
import lk.udcreations.user.exception.NotFoundException;
import lk.udcreations.user.repository.RoleRepository;
import lk.udcreations.user.repository.UserRepository;
import lk.udcreations.user.security.AuthUtils;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@TestPropertySource("classpath:application-test.properties")
class UsersServiceTest {

	@Mock
	private ModelMapper modelMapper;

	@Mock
	private UserRepository userRepository;

	@Mock
	private RoleRepository roleRepository;

	@Mock
	private PasswordEncoder passwordEncoder;

	@Mock
	private AuthUtils authUtils;

	@InjectMocks
	private UsersService usersService;

    private Users user1;
	private Users user2;
	private UsersDTO userDTO1;
	private UsersDTO userDTO2;
	private Role role;
    private RoleDTO roleDTO;
    private CreatedUpdatedUserDTO createdUpdatedUserDTO;

	@BeforeEach
	void setUp() {
		// Mock a logged-in admin user
        Users adminUser = new Users();
		adminUser.setUserId(999);
		adminUser.setUsername("admin");
		adminUser.setFirstName("Admin");
		adminUser.setLastName("User");
		adminUser.setEmail("admin@example.com");
		adminUser.setAddress("Admin Address");
		adminUser.setPhoneNo1("1111111111");
		adminUser.setRoleId(1);
		adminUser.setPassword("password");
		adminUser.setEnabled(true);
		adminUser.setLocked(false);
		adminUser.setDeleted(false);

		// Set up test users
		user1 = new Users();
		user1.setUserId(1);
		user1.setUsername("user1");
		user1.setFirstName("User");
		user1.setLastName("One");
		user1.setEmail("user1@example.com");
		user1.setAddress("Address 1");
		user1.setPhoneNo1("1234567890");
		user1.setRoleId(1);
		user1.setPassword("password");
		user1.setEnabled(true);
		user1.setLocked(false);
		user1.setDeleted(false);
		user1.setCreatedUserId(999);
		user1.setUpdatedUserId(999);

		user2 = new Users();
		user2.setUserId(2);
		user2.setUsername("user2");
		user2.setFirstName("User");
		user2.setLastName("Two");
		user2.setEmail("user2@example.com");
		user2.setAddress("Address 2");
		user2.setPhoneNo1("1234567891");
		user2.setRoleId(2);
		user2.setPassword("password");
		user2.setEnabled(true);
		user2.setLocked(false);
		user2.setDeleted(false);
		user2.setCreatedUserId(999);
		user2.setUpdatedUserId(999);

		// Set up DTOs
		userDTO1 = new UsersDTO();
		userDTO1.setUserId(1);
		userDTO1.setUsername("user1");
		userDTO1.setFirstName("User");
		userDTO1.setLastName("One");
		userDTO1.setEmail("user1@example.com");

		userDTO2 = new UsersDTO();
		userDTO2.setUserId(2);
		userDTO2.setUsername("user2");
		userDTO2.setFirstName("User");
		userDTO2.setLastName("Two");
		userDTO2.setEmail("user2@example.com");

		// Set up roles
		role = new Role();
		role.setRoleId(1);
		role.setRoleName("Admin");

        Role role2 = new Role();
		role2.setRoleId(2);
		role2.setRoleName("User");

		roleDTO = new RoleDTO();
		roleDTO.setRoleId(1);
		roleDTO.setRoleName("Admin");

        RoleDTO roleDTO2 = new RoleDTO();
		roleDTO2.setRoleId(2);
		roleDTO2.setRoleName("User");

		// Set up CreatedUpdatedUserDTO
		createdUpdatedUserDTO = new CreatedUpdatedUserDTO();
		createdUpdatedUserDTO.setUserId(999);
		createdUpdatedUserDTO.setUsername("admin");

		// Set up common mocks
		when(authUtils.getLoggedInUser()).thenReturn(adminUser);
		when(userRepository.findById(999)).thenReturn(Optional.of(adminUser));
		when(userRepository.findById(1)).thenReturn(Optional.of(user1));
		when(userRepository.findById(2)).thenReturn(Optional.of(user2));
		when(userRepository.findById(null)).thenThrow(new IllegalArgumentException("User ID cannot be null"));
		when(roleRepository.findByRoleId(1)).thenReturn(Optional.of(role));
		when(roleRepository.findByRoleId(2)).thenReturn(Optional.of(role2));
		when(modelMapper.map(role, RoleDTO.class)).thenReturn(roleDTO);
		when(modelMapper.map(role2, RoleDTO.class)).thenReturn(roleDTO2);
	}

	@Test
	void testGetAllUsers() {
		// Arrange
		when(userRepository.findAll()).thenReturn(Arrays.asList(user1, user2));
		when(modelMapper.map(user1, UsersDTO.class)).thenReturn(userDTO1);
		when(modelMapper.map(user2, UsersDTO.class)).thenReturn(userDTO2);
		when(modelMapper.map(any(Users.class), eq(CreatedUpdatedUserDTO.class))).thenReturn(createdUpdatedUserDTO);
		when(modelMapper.map(role, RoleDTO.class)).thenReturn(roleDTO);

		// Act
		List<UsersDTO> result = usersService.getAllUsers();

		// Assert
		assertEquals(2, result.size());
		assertEquals("user1", result.get(0).getUsername());
		assertEquals("user2", result.get(1).getUsername());
		verify(userRepository, times(1)).findAll();
	}

	@Test
	void testGetAllUsers_NoUsersFound() {
		// Arrange
		when(userRepository.findAll()).thenReturn(Collections.emptyList());

		// Act
		List<UsersDTO> result = usersService.getAllUsers();

		// Assert
		assertTrue(result.isEmpty());
		verify(userRepository, times(1)).findAll();
	}

	@Test
	void testGetAllExistUsers() {
		// Arrange
		when(userRepository.findByDeletedFalse()).thenReturn(Arrays.asList(user1, user2));
		when(modelMapper.map(user1, UsersDTO.class)).thenReturn(userDTO1);
		when(modelMapper.map(user2, UsersDTO.class)).thenReturn(userDTO2);
		when(modelMapper.map(any(Users.class), eq(CreatedUpdatedUserDTO.class))).thenReturn(createdUpdatedUserDTO);
		when(modelMapper.map(role, RoleDTO.class)).thenReturn(roleDTO);

		// Act
		List<UsersDTO> result = usersService.getAllExistUsers();

		// Assert
		assertEquals(2, result.size());
		assertEquals("user1", result.get(0).getUsername());
		assertEquals("user2", result.get(1).getUsername());
		verify(userRepository, times(1)).findByDeletedFalse();
	}

	@Test
	void testGetAllExistUsers_NoUsersFound() {
		// Arrange
		when(userRepository.findByDeletedFalse()).thenReturn(Collections.emptyList());

		// Act
		List<UsersDTO> result = usersService.getAllExistUsers();

		// Assert
		assertTrue(result.isEmpty());
		verify(userRepository, times(1)).findByDeletedFalse();
	}

	@Test
	void testGetUserById_UserExists() {
		// Arrange
		when(userRepository.findByUserIdAndDeletedFalse(1)).thenReturn(Optional.of(user1));
		when(modelMapper.map(user1, UsersDTO.class)).thenReturn(userDTO1);
		when(modelMapper.map(any(Users.class), eq(CreatedUpdatedUserDTO.class))).thenReturn(createdUpdatedUserDTO);
		when(modelMapper.map(role, RoleDTO.class)).thenReturn(roleDTO);

		// Act
		UsersDTO result = usersService.getUserById(1);

		// Assert
		assertNotNull(result);
		assertEquals("user1", result.getUsername());
		verify(userRepository, times(1)).findByUserIdAndDeletedFalse(1);
	}

	@Test
	void testGetUserById_UserNotFound() {
		// Arrange
		when(userRepository.findByUserIdAndDeletedFalse(1)).thenReturn(Optional.empty());

		// Act & Assert
		Exception exception = assertThrows(NotFoundException.class, () -> usersService.getUserById(1));

		// Assert
		assertTrue(exception.getMessage().contains(ErrorMessages.USER_NOT_FOUND));
		verify(userRepository, times(1)).findByUserIdAndDeletedFalse(1);
	}

	@Test
	void testGetUserUsername_UserExists() {
		// Arrange
		when(userRepository.findByUsername("user1")).thenReturn(Optional.of(user1));
		when(modelMapper.map(user1, UsersDTO.class)).thenReturn(userDTO1);
		when(modelMapper.map(any(Users.class), eq(CreatedUpdatedUserDTO.class))).thenReturn(createdUpdatedUserDTO);
		when(modelMapper.map(role, RoleDTO.class)).thenReturn(roleDTO);

		// Act
		UsersDTO result = usersService.getUserUsername("user1");

		// Assert
		assertNotNull(result);
		assertEquals("user1", result.getUsername());
		verify(userRepository, times(1)).findByUsername("user1");
	}

	@Test
	void testGetUserUsername_UserNotFound() {
		// Arrange
		when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

		// Act & Assert
		Exception exception = assertThrows(NotFoundException.class, () -> usersService.getUserUsername("nonexistent"));

		// Assert
		assertTrue(exception.getMessage().contains(ErrorMessages.USER_NOT_FOUND));
		verify(userRepository, times(1)).findByUsername("nonexistent");
	}

	@Test
	void testCreateUser_NewUser() {
		// Arrange
		Users newUser = new Users();
		newUser.setUsername("newuser");
		newUser.setFirstName("New");
		newUser.setLastName("User");
		newUser.setEmail("newuser@example.com");
		newUser.setPassword("password");
		newUser.setRoleId(1);

		Users savedUser = new Users();
		savedUser.setUserId(3);
		savedUser.setUsername("newuser");
		savedUser.setFirstName("New");
		savedUser.setLastName("User");
		savedUser.setEmail("newuser@example.com");
		savedUser.setPassword("encodedPassword");
		savedUser.setRoleId(1);
		savedUser.setCreatedUserId(999);
		savedUser.setUpdatedUserId(999);

		UsersDTO savedUserDTO = new UsersDTO();
		savedUserDTO.setUserId(3);
		savedUserDTO.setUsername("newuser");

		when(userRepository.findByUsernameAndDeletedFalse("newuser")).thenReturn(Optional.empty());
		when(userRepository.existsByEmailAndDeletedFalse("newuser@example.com")).thenReturn(Boolean.FALSE);
		when(userRepository.findByUsernameAndDeletedTrue("newuser")).thenReturn(Optional.empty());
		when(passwordEncoder.encode("password")).thenReturn("encodedPassword");
		when(userRepository.save(any(Users.class))).thenReturn(savedUser);
		when(modelMapper.map(savedUser, UsersDTO.class)).thenReturn(savedUserDTO);
		when(modelMapper.map(any(Users.class), eq(CreatedUpdatedUserDTO.class))).thenReturn(createdUpdatedUserDTO);
		when(modelMapper.map(role, RoleDTO.class)).thenReturn(roleDTO);

		// Act
		UsersDTO result = usersService.createUser(newUser);

		// Assert
		assertNotNull(result);
		assertEquals("newuser", result.getUsername());
		verify(userRepository, times(1)).findByUsernameAndDeletedFalse("newuser");
		verify(userRepository, times(1)).existsByEmailAndDeletedFalse("newuser@example.com");
		verify(userRepository, times(1)).findByUsernameAndDeletedTrue("newuser");
		verify(userRepository, times(1)).save(any(Users.class));
	}

	@Test
	void testCreateUser_ReactivateDeletedUser() {
		// Arrange
		Users newUser = new Users();
		newUser.setUsername("deleteduser");
		newUser.setFirstName("Deleted");
		newUser.setLastName("User");
		newUser.setEmail("deleteduser@example.com");
		newUser.setPassword("password");
		newUser.setRoleId(1);

		Users deletedUser = new Users();
		deletedUser.setUserId(4);
		deletedUser.setUsername("deleteduser");
		deletedUser.setFirstName("Deleted");
		deletedUser.setLastName("User");
		deletedUser.setEmail("deleteduser@example.com");
		deletedUser.setRoleId(1);
		deletedUser.setCreatedUserId(999);
		deletedUser.setUpdatedUserId(999);
		deletedUser.setDeleted(true);

		Users reactivatedUser = new Users();
		reactivatedUser.setUserId(4);
		reactivatedUser.setUsername("deleteduser");
		reactivatedUser.setFirstName("Deleted");
		reactivatedUser.setLastName("User");
		reactivatedUser.setEmail("deleteduser@example.com");
		reactivatedUser.setPassword("encodedPassword");
		reactivatedUser.setRoleId(1);
		reactivatedUser.setDeleted(false);
		reactivatedUser.setCreatedUserId(999);
		reactivatedUser.setUpdatedUserId(999);

		UsersDTO reactivatedUserDTO = new UsersDTO();
		reactivatedUserDTO.setUserId(4);
		reactivatedUserDTO.setUsername("deleteduser");

		when(userRepository.findByUsernameAndDeletedFalse("deleteduser")).thenReturn(Optional.empty());
		when(userRepository.existsByEmailAndDeletedFalse("deleteduser@example.com")).thenReturn(Boolean.FALSE);
		when(userRepository.findByUsernameAndDeletedTrue("deleteduser")).thenReturn(Optional.of(deletedUser));
		when(userRepository.findById(4)).thenReturn(Optional.of(deletedUser));
		when(passwordEncoder.encode("password")).thenReturn("encodedPassword");
		when(userRepository.save(any(Users.class))).thenReturn(reactivatedUser);
		when(modelMapper.map(reactivatedUser, UsersDTO.class)).thenReturn(reactivatedUserDTO);
		when(modelMapper.map(any(Users.class), eq(CreatedUpdatedUserDTO.class))).thenReturn(createdUpdatedUserDTO);
		when(modelMapper.map(role, RoleDTO.class)).thenReturn(roleDTO);

		// Act
		UsersDTO result = usersService.createUser(newUser);

		// Assert
		assertNotNull(result);
		assertEquals("deleteduser", result.getUsername());
		verify(userRepository, times(1)).findByUsernameAndDeletedFalse("deleteduser");
		verify(userRepository, times(1)).existsByEmailAndDeletedFalse("deleteduser@example.com");
		verify(userRepository, times(1)).findByUsernameAndDeletedTrue("deleteduser");
		verify(userRepository, times(1)).save(any(Users.class));
	}

	@Test
	void testCreateUser_UsernameExists() {
		// Arrange
		Users newUser = new Users();
		newUser.setUsername("user1");
		newUser.setEmail("newuser@example.com");

		when(userRepository.findByUsernameAndDeletedFalse("user1")).thenReturn(Optional.of(user1));

		// Act & Assert
		Exception exception = assertThrows(IllegalArgumentException.class, () -> usersService.createUser(newUser));

		// Assert
		assertTrue(exception.getMessage().contains(ErrorMessages.USER_NAME_EXISTS));
		verify(userRepository, times(1)).findByUsernameAndDeletedFalse("user1");
		verify(userRepository, never()).save(any(Users.class));
	}

	@Test
	void testCreateUser_EmailExists() {
		// Arrange
		Users newUser = new Users();
		newUser.setUsername("newuser");
		newUser.setEmail("user1@example.com");

		when(userRepository.findByUsernameAndDeletedFalse("newuser")).thenReturn(Optional.empty());
		when(userRepository.existsByEmailAndDeletedFalse("user1@example.com")).thenReturn(Boolean.TRUE);

		// Act & Assert
		Exception exception = assertThrows(IllegalArgumentException.class, () -> usersService.createUser(newUser));

		// Assert
		assertTrue(exception.getMessage().contains(ErrorMessages.EMAIL_EXISTS));
		verify(userRepository, times(1)).findByUsernameAndDeletedFalse("newuser");
		verify(userRepository, times(1)).existsByEmailAndDeletedFalse("user1@example.com");
		verify(userRepository, never()).save(any(Users.class));
	}

	@Test
	void testUpdateUser_UserExists() {
		// Arrange
		Integer userId = 1;

		Users updatedUser = new Users();
		updatedUser.setUserId(userId);
		updatedUser.setUsername("user1");
		updatedUser.setFirstName("Updated");
		updatedUser.setLastName("User");
		updatedUser.setEmail("updated@example.com");
		updatedUser.setAddress("Updated Address");
		updatedUser.setPhoneNo1("9876543210");
		updatedUser.setRoleId(2);
		updatedUser.setPassword("newpassword");
		updatedUser.setEnabled(true);
		updatedUser.setLocked(false);

		Users savedUser = new Users();
		savedUser.setUserId(userId);
		savedUser.setUsername("user1");
		savedUser.setFirstName("Updated");
		savedUser.setLastName("User");
		savedUser.setEmail("updated@example.com");
		savedUser.setAddress("Updated Address");
		savedUser.setPhoneNo1("9876543210");
		savedUser.setRoleId(2);
		savedUser.setPassword("encodedNewPassword");
		savedUser.setEnabled(true);
		savedUser.setLocked(false);
		savedUser.setCreatedUserId(999);
		savedUser.setUpdatedUserId(999);

		UsersDTO updatedUserDTO = new UsersDTO();
		updatedUserDTO.setUserId(userId);
		updatedUserDTO.setUsername("user1");
		updatedUserDTO.setFirstName("Updated");
		updatedUserDTO.setLastName("User");
		updatedUserDTO.setEmail("updated@example.com");

		when(userRepository.findById(userId)).thenReturn(Optional.of(user1));
		when(passwordEncoder.encode("newpassword")).thenReturn("encodedNewPassword");
		when(userRepository.save(any(Users.class))).thenReturn(savedUser);
		when(modelMapper.map(savedUser, UsersDTO.class)).thenReturn(updatedUserDTO);
		when(modelMapper.map(any(Users.class), eq(CreatedUpdatedUserDTO.class))).thenReturn(createdUpdatedUserDTO);
		when(modelMapper.map(role, RoleDTO.class)).thenReturn(roleDTO);

		// Act
		UsersDTO result = usersService.updateUser(userId, updatedUser);

		// Assert
		assertNotNull(result);
		assertEquals("user1", result.getUsername());
		assertEquals("Updated", result.getFirstName());
		assertEquals("User", result.getLastName());
		assertEquals("updated@example.com", result.getEmail());
		verify(userRepository, times(1)).findById(userId);
		verify(userRepository, times(1)).save(any(Users.class));
	}

	@Test
	void testUpdateUser_UserNotFound() {
		// Arrange
		Integer userId = 999;
		Users updatedUser = new Users();
		updatedUser.setUsername("nonexistent");

		when(userRepository.findById(userId)).thenReturn(Optional.empty());

		// Act & Assert
		Exception exception = assertThrows(RuntimeException.class, () -> usersService.updateUser(userId, updatedUser));

		// Assert
		assertTrue(exception.getMessage().contains(ErrorMessages.USER_NOT_FOUND));
		verify(userRepository, times(1)).findById(userId);
		verify(userRepository, never()).save(any(Users.class));
	}

	@Test
	void testDeleteUser_UserExists() {
		// Arrange
		Integer userId = 1;

		when(userRepository.findById(userId)).thenReturn(Optional.of(user1));

		// Act
		usersService.deleteUser(userId);

		// Assert
		assertTrue(user1.isDeleted());
		assertFalse(user1.isEnabled());
		verify(userRepository, times(1)).findById(userId);
		verify(userRepository, times(1)).save(user1);
	}

	@Test
	void testDeleteUser_UserNotFound() {
		// Arrange
		Integer userId = 999;

		when(userRepository.findById(userId)).thenReturn(Optional.empty());

		// Act & Assert
		Exception exception = assertThrows(NotFoundException.class, () -> usersService.deleteUser(userId));

		// Assert
		assertTrue(exception.getMessage().contains(ErrorMessages.USER_NOT_FOUND));
		verify(userRepository, times(1)).findById(userId);
		verify(userRepository, never()).save(any(Users.class));
	}
}
