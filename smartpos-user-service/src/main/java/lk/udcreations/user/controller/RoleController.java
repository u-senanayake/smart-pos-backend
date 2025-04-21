package lk.udcreations.user.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lk.udcreations.common.dto.role.RoleDTO;
import lk.udcreations.user.entity.Role;
import lk.udcreations.user.service.RoleService;

@RestController
@RequestMapping("/api/v1/role")
@Tag(name = "Role API", description = "Endpoints for managing system roles")
public class RoleController {

	private final RoleService roleService;

	public RoleController(RoleService roleService) {
		super();
		this.roleService = roleService;
	}

	/** Get all roles */
	@Operation(summary = "Get all roles", description = "Retrieve a list of all roles, including deleted ones.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Successfully retrieved list", content = @Content(mediaType = "application/json", schema = @Schema(implementation = RoleDTO.class))),
			@ApiResponse(responseCode = "500", description = "Internal server error", content = @Content) })
	@GetMapping("/all")
	public ResponseEntity<List<RoleDTO>> getAllRoles() {

		return ResponseEntity.ok(roleService.getAllRoles());
	}

	/** Get all non-deleted roles */
	@Operation(summary = "Get active roles", description = "Retrieve a list of non-deleted roles.")
	@ApiResponse(responseCode = "200", description = "Successfully retrieved active roles")
	@GetMapping
	public ResponseEntity<List<RoleDTO>> getAllExistRoles() {

		return ResponseEntity.ok(roleService.getAllExistRoles());
	}

	/** Get a role by ID */
	@Operation(summary = "Get role by ID", description = "Retrieve role details by its ID.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Role found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = RoleDTO.class))),
			@ApiResponse(responseCode = "404", description = "Role not found", content = @Content) })
	@GetMapping("/{roleId}")
	public ResponseEntity<RoleDTO> getRoleById(
			@Parameter(description = "ID of the role to be retrieved") @PathVariable Integer roleId) {
		return ResponseEntity.ok(roleService.getRoleById(roleId));
	}

	/** Create a new role */
	@Operation(summary = "Create a new role", description = "Create a new role in the system.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "201", description = "Role created successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = RoleDTO.class))),
			@ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content) })
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public RoleDTO createRole(@Parameter(description = "Role object to be created") @Valid @RequestBody Role role) {
		return roleService.createRole(role);
	}

	/** Update a role */
	@Operation(summary = "Update a role", description = "Update an existing role by its ID.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Role updated successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = RoleDTO.class))),
			@ApiResponse(responseCode = "404", description = "Role not found", content = @Content) })
	@PutMapping("/{roleId}")
	public ResponseEntity<RoleDTO> updateRole(
			@Parameter(description = "ID of the role to update") @PathVariable Integer roleId,
			@Parameter(description = "Updated role details") @Valid @RequestBody Role updatedRole) {
		return ResponseEntity.ok(roleService.updateRole(roleId, updatedRole));
	}

	/** Delete a role */
	@Operation(summary = "Delete a role (soft delete)", description = "Soft delete a role by marking it as deleted.")
	@ApiResponses(value = { @ApiResponse(responseCode = "204", description = "Role soft-deleted successfully"),
			@ApiResponse(responseCode = "404", description = "Role not found", content = @Content) })
	@DeleteMapping("/{roleId}")
	public ResponseEntity<Void> deleteRole(
			@Parameter(description = "ID of the role to delete") @PathVariable Integer roleId) {
		roleService.softDeleteRole(roleId);
		return ResponseEntity.noContent().build();
	}
}
