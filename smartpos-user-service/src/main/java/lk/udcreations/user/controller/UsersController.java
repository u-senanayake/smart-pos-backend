package lk.udcreations.user.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lk.udcreations.common.dto.user.UsersDTO;
import lk.udcreations.user.entity.Users;
import lk.udcreations.user.service.UsersService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/api/v1/users")
@Tag(name = "User API", description = "Endpoints for managing system users")
public class UsersController {

    private final UsersService usersService;

    public UsersController(UsersService usersService) {
        super();
        this.usersService = usersService;
    }

    /**
     * Get all users
     */
    @Operation(summary = "Get all users", description = "Retrieve all users, including deleted users.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UsersDTO.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)})
    @GetMapping("/all")
    public ResponseEntity<List<UsersDTO>> getAllUsers() {
        return ResponseEntity.ok(usersService.getAllUsers());
    }

    /**
     * Get all existed users
     */
    @Operation(summary = "Get active users", description = "Retrieve all non-deleted users.")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved active users")
    @GetMapping
    public ResponseEntity<List<UsersDTO>> getAllExistUsers() {
        return ResponseEntity.ok(usersService.getAllExistUsers());
    }

    /**
     * Get a user by ID
     */
    @Operation(summary = "Get user by ID", description = "Retrieve user details by their ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UsersDTO.class))),
            @ApiResponse(responseCode = "404", description = "User not found", content = @Content)})
    @GetMapping("/{userId}")
    public ResponseEntity<UsersDTO> getUserById(
            @Parameter(description = "ID of the user to retrieve") @PathVariable Integer userId) {
        return ResponseEntity.ok(usersService.getUserById(userId));
    }

    @Operation(summary = "Get user by username", description = "Retrieve user details by their username.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UsersDTO.class))),
            @ApiResponse(responseCode = "404", description = "User not found", content = @Content)})
    @GetMapping("/username/{username}")
    public ResponseEntity<UsersDTO> getUserUsername(@Parameter(description = "Username of the user to retrieve") @PathVariable String username) {
        return ResponseEntity.ok(usersService.getUserUsername(username));
    }

    /**
     * Create a new user
     */
    @Operation(summary = "Create a new user", description = "Add a new user to the system.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User created successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UsersDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content)})
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<?> createUser(
            @Parameter(description = "User object to be created") @Valid @RequestBody Users user, @AuthenticationPrincipal String loggedInUsername) {
        try {
            UsersDTO createdUser = usersService.createUser(user, loggedInUsername);
            return ResponseEntity.status(201).body(createdUser);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    /**
     * Update a user
     */
    @Operation(summary = "Update a user", description = "Update user details by their ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User updated successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UsersDTO.class))),
            @ApiResponse(responseCode = "404", description = "User not found", content = @Content)})
    @PutMapping("/{userId}")
    public ResponseEntity<UsersDTO> updateUser(
            @Parameter(description = "ID of the user to update") @PathVariable Integer userId,
            @Parameter(description = "Updated user details") @Valid @RequestBody Users updatedUser
            , @AuthenticationPrincipal String loggedInUsername) {
        return ResponseEntity.ok(usersService.updateUser(userId, updatedUser, loggedInUsername));
    }

    /**
     * Delete a user by ID (soft delete)
     */
    @Operation(summary = "Soft delete a user", description = "Mark a user as deleted instead of removing permanently.")
    @ApiResponses(value = {@ApiResponse(responseCode = "204", description = "User soft-deleted successfully"),
            @ApiResponse(responseCode = "404", description = "User not found", content = @Content)})
    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(
            @Parameter(description = "ID of the user to soft delete") @PathVariable Integer userId, @AuthenticationPrincipal String loggedInUsername) {
        usersService.deleteUser(userId, loggedInUsername);
        return ResponseEntity.noContent().build();
    }
}
