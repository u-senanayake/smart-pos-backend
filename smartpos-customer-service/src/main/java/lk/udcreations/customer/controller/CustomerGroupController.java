package lk.udcreations.customer.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lk.udcreations.common.dto.customergroup.CustomerGroupDTO;
import lk.udcreations.customer.entity.CustomerGroup;
import lk.udcreations.customer.service.CustomerGroupService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/customergroup")
@Tag(name = "Customer Group API", description = "Endpoints for managing customer groups")
public class CustomerGroupController {

    private final CustomerGroupService customerGroupService;

    public CustomerGroupController(CustomerGroupService customerGroupService) {
        super();
        this.customerGroupService = customerGroupService;
    }

    /**
     * Get all customer group
     */
    @Operation(summary = "Get all customer groups", description = "Retrieve all customer groups, including soft-deleted ones.")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved customer groups")
    @GetMapping("/all")
    public ResponseEntity<List<CustomerGroupDTO>> getAllCustomerGroups() {
        return ResponseEntity.ok(customerGroupService.getAllCustomerGroups());
    }

    /**
     * Get all non-deleted customer groups
     */
    @Operation(summary = "Get active customer groups", description = "Retrieve all non-deleted customer groups.")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved active customer groups")
    @GetMapping
    public ResponseEntity<List<CustomerGroupDTO>> getAllExistCustomerGroups() {
        return ResponseEntity.ok(customerGroupService.getAllExistCustomerGroups());
    }

    /**
     * Get a customer group by ID
     */
    @Operation(summary = "Get a customer group by ID", description = "Retrieve customer group details by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Customer group found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CustomerGroupDTO.class))),
            @ApiResponse(responseCode = "404", description = "Customer group not found", content = @Content)})
    @GetMapping("/{customerGroupId}")
    public ResponseEntity<CustomerGroupDTO> getCustomerGroupById(
            @Parameter(description = "ID of the customer group to retrieve") @PathVariable Integer customerGroupId) {
        return ResponseEntity.ok(customerGroupService.getCustomerGroupById(customerGroupId));
    }

    /**
     * Create a new customer group
     */
    @Operation(summary = "Create a new customer group", description = "Add a new customer group to the system.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Customer group created successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CustomerGroupDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content)})
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CustomerGroupDTO createCustomerGroup(
            @Parameter(description = "Customer group object to be created") @Valid @RequestBody CustomerGroup customerGroup, @AuthenticationPrincipal String loggedInUsername) {
        return customerGroupService.createCustomerGroup(customerGroup, loggedInUsername);
    }

    /**
     * Update a customer group
     */
    @Operation(summary = "Update a customer group", description = "Update customer group details by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Customer group updated successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CustomerGroupDTO.class))),
            @ApiResponse(responseCode = "404", description = "Customer group not found", content = @Content)})
    @PutMapping("/{customerGroupId}")
    public ResponseEntity<CustomerGroupDTO> updateCustomerGroup(
            @Parameter(description = "ID of the customer group to update") @PathVariable Integer customerGroupId,
            @Parameter(description = "Updated customer group details") @Valid @RequestBody CustomerGroup updatedCustomerGroup, @AuthenticationPrincipal String loggedInUsername) {
        return ResponseEntity.ok(customerGroupService.updateCustomerGroup(customerGroupId, updatedCustomerGroup, loggedInUsername));
    }

    /**
     * Delete a customer group
     */
    @Operation(summary = "Soft delete a customer group", description = "Soft delete a customer group by marking it as deleted.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Customer group soft-deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Customer group not found", content = @Content)})
    @DeleteMapping("/{customerGroupId}")
    public ResponseEntity<Void> deleteCustomerGroup(
            @Parameter(description = "ID of the customer group to delete") @PathVariable Integer customerGroupId, @AuthenticationPrincipal String loggedInUsername) {
        customerGroupService.softDeleteCustomerGroup(customerGroupId, loggedInUsername);
        return ResponseEntity.noContent().build();
    }

}
