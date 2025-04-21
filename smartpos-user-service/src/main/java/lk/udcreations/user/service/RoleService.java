package lk.udcreations.user.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import lk.udcreations.common.dto.role.RoleDTO;
import lk.udcreations.user.constants.ErrorMessages;
import lk.udcreations.user.entity.Role;
import lk.udcreations.user.exception.NotFoundException;
import lk.udcreations.user.repository.RoleRepository;
import lk.udcreations.user.util.relationcheck.RoleCheck;



@Service
public class RoleService {

	private static final Logger LOGGER = LoggerFactory.getLogger(RoleService.class);

	private final RoleRepository roleRepository;
	private final RoleCheck check;
	private final ModelMapper modelMapper;

	public RoleService(RoleRepository roleRepository, RoleCheck check, ModelMapper modelMapper) {
		super();
		this.roleRepository = roleRepository;
		this.check = check;
		this.modelMapper = modelMapper;
	}


	/** Get all roles */
	public List<RoleDTO> getAllRoles() {

		LOGGER.debug("Fetching all roles from the database.");

		List<Role> roles = roleRepository.findAll();

		if (roles.isEmpty()) {
			LOGGER.warn("No roles found in the database.");
		} else {
			LOGGER.info("Successfully fetched {} roles from the database.", roles.size());
		}

		return roles.stream().map(this::convertToDTO).collect(Collectors.toList());
	}


	/** Get all non-deleted roles */
	public List<RoleDTO> getAllExistRoles() {

		LOGGER.debug("Fetching all non-deleted roles.");

		List<Role> roles = roleRepository.findByDeletedFalse();

		if (roles.isEmpty()) {
			LOGGER.warn("No active roles found in the system.");
		} else {
			LOGGER.info("Fetched {} active roles from the database.", roles.size());
		}

		return roles.stream().map(this::convertToDTO).collect(Collectors.toList());
	}


	/** Get a role by ID */
	public RoleDTO getRoleById(Integer roleId) {

		LOGGER.debug("Fetching role with ID: {}", roleId);

		Role role = roleRepository.findById(roleId).orElseThrow(() -> {
			String errorMessage = ErrorMessages.ROLE_NOT_FOUND + roleId;
			LOGGER.error("Role fetch failed. {}", errorMessage);
			return new NotFoundException(errorMessage);
		});

		LOGGER.info("Successfully fetched role with ID: {} and name: '{}'", role.getRoleId(), role.getRoleName());
		return convertToDTO(role);
	}


	/** Create a new role */
	public RoleDTO createRole(Role role) {

		LOGGER.debug("Attempting to create a new role with name: {}", role.getRoleName());

		Role savedRole = null;

		if (check.isRoleNameExists(role)) {
			String errorMessage = ErrorMessages.ROLE_NAME_EXISTS;
			LOGGER.error("Role creation failed: {}", errorMessage);
			throw new IllegalArgumentException(errorMessage);
		}

		// Check for soft-deleted role and reactivate it
		Optional<Role> softDeletedRole = roleRepository.findByRoleNameAndDeletedTrue(role.getRoleName());
		if (softDeletedRole.isPresent()) {
			Role reactivatedRole = softDeletedRole.get();
			reactivatedRole.setDescription(role.getDescription());
			reactivatedRole.setEnabled(role.isEnabled());
			reactivatedRole.setDeleted(false);
			savedRole = roleRepository.save(reactivatedRole);

			LOGGER.info("Soft-deleted role '{}' reactivated successfully.", role.getRoleName());
		} else {
			// Otherwise, create a new role
			savedRole = roleRepository.save(role);
			LOGGER.info("New role '{}' created successfully with ID: {}", role.getRoleName(), savedRole.getRoleId());
		}

		return convertToDTO(savedRole);
	}


	/** Update role */
	public RoleDTO updateRole(Integer roleId, Role updatedRole) {

		LOGGER.debug("Attempting to update role with ID: {}", roleId);

		return roleRepository.findById(roleId).map(role -> {
			role.setRoleName(updatedRole.getRoleName());
			role.setDescription(updatedRole.getDescription());
			role.setEnabled(updatedRole.isEnabled());

			Role savedRole = roleRepository.save(role);
			LOGGER.info("Role with ID: {} successfully updated. New name: '{}'", roleId, updatedRole.getRoleName());

			return convertToDTO(savedRole);
		}).orElseThrow(() -> {
			String errorMessage = ErrorMessages.ROLE_NOT_FOUND + roleId;
			LOGGER.error("Role update failed: {}", errorMessage);
			return new NotFoundException(errorMessage);
		});
	}


	/** Delete a role by ID (soft delete) */
	public void softDeleteRole(Integer roleId) {

		LOGGER.debug("Attempting to soft delete role with ID: {}", roleId);

		roleRepository.findById(roleId).ifPresentOrElse(role -> {
			role.setDeleted(true);
			role.setDeletedAt(LocalDateTime.now());
			role.setEnabled(false);
			roleRepository.save(role);
			LOGGER.info("Role with ID: {} has been soft deleted.", roleId);
		}, () -> {
			String errorMessage = ErrorMessages.ROLE_NOT_FOUND + roleId;
			LOGGER.error("Soft delete failed: {}", errorMessage);
			throw new NotFoundException(errorMessage);
		});
	}


	/** Delete a role by ID */
	public void deleteRole(Integer roleId) {

		LOGGER.debug("Attempting to delete role with ID: {}", roleId);

		Role role = roleRepository.findById(roleId).orElseThrow(() -> {
			String errorMessage = ErrorMessages.ROLE_NOT_FOUND + roleId;
			LOGGER.error("Role deletion failed: {}", errorMessage);
            return new NotFoundException(errorMessage);
		});

		roleRepository.delete(role);
		LOGGER.info("Role with ID: {} has been permanently deleted.", roleId);
	}

	private RoleDTO convertToDTO(Role role) {

		return modelMapper.map(role, RoleDTO.class);
	}
}
