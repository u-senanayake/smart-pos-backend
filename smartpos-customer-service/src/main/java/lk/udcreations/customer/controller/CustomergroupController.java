package lk.udcreations.customer.controller;

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
import lk.udcreations.common.dto.customergroup.CustomerGroupDTO;
import lk.udcreations.customer.entity.CustomerGroup;
import lk.udcreations.customer.service.CustomerGroupService;

@RestController
@RequestMapping("/api/v1/customergroup")
@Tag(name = "Customer Group API", description = "Endpoints for managing customer groups")
public class CustomergroupController {

	private final CustomerGroupService customerGroupService;

	public CustomergroupController(CustomerGroupService customerGroupService) {
		super();
		this.customerGroupService = customerGroupService;
	}

	/** Get all customer group */
	@Operation(summary = "Get all customer groups", description = "Retrieve all customer groups, including soft-deleted ones.")
	@ApiResponse(responseCode = "200", description = "Successfully retrieved customer groups")
	@GetMapping("/all")
	public ResponseEntity<List<CustomerGroupDTO>> getAllCustomerGroups() {
		return ResponseEntity.ok(customerGroupService.getAllCustomerGroups());
	}

	/** Get all non-deleted customer groups */
	@Operation(summary = "Get active customer groups", description = "Retrieve all non-deleted customer groups.")
	@ApiResponse(responseCode = "200", description = "Successfully retrieved active customer groups")
	@GetMapping
	public ResponseEntity<List<CustomerGroupDTO>> getAllExistCustomerGroups() {
		return ResponseEntity.ok(customerGroupService.getAllExistCustomerGroups());
	}

	/** Get a customer group by ID */
	@Operation(summary = "Get a customer group by ID", description = "Retrieve customer group details by its ID.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Customer group found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CustomerGroupDTO.class))),
			@ApiResponse(responseCode = "404", description = "Customer group not found", content = @Content) })
	@GetMapping("/{customerGroupId}")
	public ResponseEntity<CustomerGroupDTO> getCustomerGroupById(
			@Parameter(description = "ID of the customer group to retrieve") @PathVariable Integer customerGroupId) {
		return ResponseEntity.ok(customerGroupService.getCustomerGroupById(customerGroupId));
	}

	/** Create a new customer group */
	@Operation(summary = "Create a new customer group", description = "Add a new customer group to the system.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "201", description = "Customer group created successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CustomerGroupDTO.class))),
			@ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content) })
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public CustomerGroupDTO createCustomerGroup(
			@Parameter(description = "Customer group object to be created") @Valid @RequestBody CustomerGroup customerGroup) {
		return customerGroupService.createCustomerGroup(customerGroup);
	}

	/** Update a customer group */
	@Operation(summary = "Update a customer group", description = "Update customer group details by its ID.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Customer group updated successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CustomerGroupDTO.class))),
			@ApiResponse(responseCode = "404", description = "Customer group not found", content = @Content) })
	@PutMapping("/{customerGroupId}")
	public ResponseEntity<CustomerGroupDTO> updateCustomerGroup(
			@Parameter(description = "ID of the customer group to update") @PathVariable Integer customerGroupId,
			@Parameter(description = "Updated customer group details") @Valid @RequestBody CustomerGroup updatedCustomerGroup) {
		return ResponseEntity.ok(customerGroupService.updateCustomerGroup(customerGroupId, updatedCustomerGroup));
	}

	/** Delete a customer group */
	@Operation(summary = "Soft delete a customer group", description = "Soft delete a customer group by marking it as deleted.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "204", description = "Customer group soft-deleted successfully"),
			@ApiResponse(responseCode = "404", description = "Customer group not found", content = @Content) })
	@DeleteMapping("/{customerGroupId}")
	public ResponseEntity<Void> deleteCustomerGroup(
			@Parameter(description = "ID of the customer group to delete") @PathVariable Integer customerGroupId) {
		customerGroupService.softDeleteCustomerGroup(customerGroupId);
		return ResponseEntity.noContent().build();
	}

}
