package lk.udcreations.user.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
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

@Service
public class UsersService {

	private static final Logger LOGGER = LoggerFactory.getLogger(UsersService.class);

	private final UserRepository userRepository;
	private final RoleRepository roleRepository;
	private final PasswordEncoder passwordEncoder;
	private final AuthUtils authUtils;
	private final ModelMapper modelMapper;


	public UsersService(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder,
			AuthUtils authUtils, ModelMapper modelMapper) {
		super();
		this.userRepository = userRepository;
		this.roleRepository = roleRepository;
		this.passwordEncoder = passwordEncoder;
		this.authUtils = authUtils;
		this.modelMapper = modelMapper;
	}

	/** Get all users */
	public List<UsersDTO> getAllUsers() {

		LOGGER.debug("Fetching all users from the database");

		List<Users> users = userRepository.findAll();
		if (users.isEmpty()) {
			LOGGER.warn("No users found in the database.");
		} else {
			LOGGER.info("Successfully fetched {} users from the database.", users.size());
		}
		return users.stream().map(this::convertToDTO).collect(Collectors.toList());
	}

	/** Get all non-deleted users */
	public List<UsersDTO> getAllExistUsers() {

		LOGGER.debug("Fetching all non-deleted users.");

		List<Users> users = userRepository.findByDeletedFalse();
		if (users.isEmpty()) {
			LOGGER.warn("No active users found in the system.");
		} else {
			LOGGER.info("Successfully fetched {} active users from the database.", users.size());
		}

		return users.stream().map(this::convertToDTO).collect(Collectors.toList());
	}

	/** Get a user by ID. */
	public UsersDTO getUserById(Integer userId) {

		LOGGER.debug("Fetching user with ID: {}", userId);

		Users user = userRepository.findByUserIdAndDeletedFalse(userId)
				.orElseThrow(() -> {
					String errorMessage = ErrorMessages.USER_NOT_FOUND + userId;
					LOGGER.error("User fetch failed. {}", errorMessage);
					return new NotFoundException(errorMessage);
				});
		LOGGER.info("Successfully fetched user with ID: {} and name: '{}'", user.getUserId(), user.getUsername());

		return convertToDTO(user);
	}
	
	public UsersDTO getUserUsername(String username) {

		LOGGER.debug("Fetching user with username: {}", username);

		Users user = userRepository.findByUsername(username)
				.orElseThrow(() -> {
					String errorMessage = ErrorMessages.USER_NOT_FOUND + username;
					LOGGER.error("User fetch failed. {}", errorMessage);
					return new NotFoundException(errorMessage);
				});
		LOGGER.info("Successfully fetched user with ID: {} and name: '{}'", user.getUserId(), user.getUsername());

		return convertToDTO(user);
	}

	/** Create a new user. */
	@Transactional
	public UsersDTO createUser(Users newUser) {

		LOGGER.debug("Attempting to create a new user with username: {}", newUser.getUsername());

		Users savedUser;

		// Check if the user exists
		Optional<Users> existingUser = userRepository.findByUsernameAndDeletedFalse(newUser.getUsername());
		if (existingUser.isPresent()) {
			String errorMessage = ErrorMessages.USER_NAME_EXISTS;
			LOGGER.error("User creation failed: {}", errorMessage);
			throw new IllegalArgumentException(errorMessage);
		}

		if (userRepository.existsByEmailAndDeletedFalse(newUser.getEmail())) {
			String errorMessage = ErrorMessages.EMAIL_EXISTS;
			LOGGER.error("User creation failed: {}", errorMessage);
			throw new IllegalArgumentException(errorMessage);
		}

		Users loggedInUser = authUtils.getLoggedInUser();

		// Check for soft-deleted user and reactivate it
		Optional<Users> softDeletedUser = userRepository.findByUsernameAndDeletedTrue(newUser.getUsername());
		if (softDeletedUser.isPresent()) {

			Users reactivatedUser = softDeletedUser.get();
			reactivatedUser.setFirstName(newUser.getFirstName());
			reactivatedUser.setLastName(newUser.getLastName());
			reactivatedUser.setEmail(newUser.getEmail());
			reactivatedUser.setAddress(newUser.getAddress());
			reactivatedUser.setPhoneNo1(newUser.getPhoneNo1());
			reactivatedUser.setPhoneNo2(newUser.getPhoneNo2());
			reactivatedUser.setRoleId(newUser.getRoleId());
			reactivatedUser.setPassword(encodePassword(newUser.getPassword()));
			reactivatedUser.setEnabled(newUser.isEnabled());
			reactivatedUser.setLocked(newUser.isLocked());
			reactivatedUser.setLocked(newUser.isLocked());
			reactivatedUser.setUpdatedUserId(loggedInUser.getUserId());
			reactivatedUser.setDeleted(false);
			reactivatedUser.setDeletedAt(null);
			reactivatedUser.setDeletedUserId(null);

			savedUser = userRepository.save(reactivatedUser);
			LOGGER.info("Soft-deleted user '{}' reactivated successfully.", newUser.getUsername());
		} else {
			// Otherwise, create a new user
			newUser.setCreatedUserId(loggedInUser.getUserId());
			newUser.setUpdatedUserId(loggedInUser.getUserId());

			savedUser = userRepository.save(newUser);
			LOGGER.info("New user '{}' created successfully with ID: {}", newUser.getUsername(), savedUser.getUserId());
		}
		return convertToDTO(savedUser);
	}

	/** Update an existing user. */
	@Transactional
	public UsersDTO updateUser(Integer userId, Users updatedUser) {

		LOGGER.debug("Attempting to update user with ID: {}", userId);

		// Get the currently logged-in user
		Users loggedInUser = authUtils.getLoggedInUser();

		return userRepository.findById(userId).map(user -> {
			user.setFirstName(updatedUser.getFirstName());
			user.setLastName(updatedUser.getLastName());
			user.setEmail(updatedUser.getEmail());
			user.setAddress(updatedUser.getAddress());
			user.setPhoneNo1(updatedUser.getPhoneNo1());
			user.setPhoneNo2(updatedUser.getPhoneNo2());
			user.setRoleId(updatedUser.getRoleId());
			user.setEnabled(updatedUser.isEnabled());
			user.setLocked(updatedUser.isLocked());
			if (updatedUser.getPassword() != null) {
				user.setPassword(encodePassword(updatedUser.getPassword()));
			}
			user.setUpdatedUserId(loggedInUser.getUserId());
			user.setDeleted(false);
			user.setDeletedAt(null);
			user.setDeletedUserId(null);
			Users savedUser = userRepository.save(user);
			LOGGER.info("User with ID: {} successfully updated. New name: '{}'", userId, updatedUser.getUsername());
			return convertToDTO(savedUser);
		}).orElseThrow(() -> {
			String errorMessage = ErrorMessages.USER_NOT_FOUND + userId;
			LOGGER.error("User update failed: {}", errorMessage);
			return new RuntimeException(errorMessage);
		});

	}

	/** Soft delete a user. */
	@Transactional
	public void deleteUser(Integer userId) {

		LOGGER.debug("Attempting to soft delete user with ID: {}", userId);

		// Get the currently logged-in user
		Users loggedInUser = authUtils.getLoggedInUser();

		userRepository.findById(userId).ifPresentOrElse(user -> {
			user.setDeleted(true);
			user.setDeletedAt(LocalDateTime.now());
			user.setDeletedUserId(loggedInUser.getUserId());
			user.setUpdatedAt(LocalDateTime.now());
			user.setUpdatedUserId(loggedInUser.getUserId());
			user.setEnabled(false);
			userRepository.save(user);
			LOGGER.info("User with ID: {} has been soft deleted.", userId);
		}, () -> {
			String errorMessage = ErrorMessages.USER_NOT_FOUND + userId;
			LOGGER.error("Soft delete failed: {}", errorMessage);
			throw new NotFoundException(errorMessage);
		});
	}

	/** Convert Users entity to UsersDTO. */
	private UsersDTO convertToDTO(Users user) {

		UsersDTO userDTO = modelMapper.map(user, UsersDTO.class);
		
		// Set RoleDTO
		Role role = roleRepository.findByRoleId(user.getRoleId())
				.orElseThrow(() -> new NotFoundException("Role not found"));
		userDTO.setRole(modelMapper.map(role, RoleDTO.class));

		// Set CreatedUserDTO
		Users createdUser = findUserById(user.getCreatedUserId());
		CreatedUpdatedUserDTO createdUserDto = modelMapper.map(createdUser, CreatedUpdatedUserDTO.class);
		userDTO.setCreatedUser(createdUserDto);

		// Set UpdatedUserDTO
		Users updatedUser = findUserById(user.getUpdatedUserId());
		CreatedUpdatedUserDTO updatedUserDto = modelMapper.map(updatedUser, CreatedUpdatedUserDTO.class);
		userDTO.setUpdatedUser(updatedUserDto);

		// Set DeletedUserDTO
		if (user.isDeleted()) {
			Users deletedUser = findUserById(user.getDeletedUserId());
			CreatedUpdatedUserDTO deletedUserDto = modelMapper.map(deletedUser, CreatedUpdatedUserDTO.class);
			userDTO.setDeletedUser(deletedUserDto);
		}

		return userDTO;
	}

	private String encodePassword(String password) {
		return passwordEncoder.encode(password);
	}

	public Users findUserById(Integer userId) {
		return userRepository.findById(userId)
				.orElseThrow(() -> new NotFoundException("User not found with ID: " + userId));
	}
}
