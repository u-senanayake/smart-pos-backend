package lk.udcreations.customer.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lk.udcreations.common.dto.customer.CustomerDTO;
import lk.udcreations.customer.entity.Customer;
import lk.udcreations.customer.service.CustomerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/customers")
@Tag(name = "Customer API", description = "Endpoints for managing customers")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        super();
        this.customerService = customerService;
    }

    /**
     * Get all customers.
     */
    @Operation(
            summary = "Get all customers",
            description = "Retrieve all customers, including soft-deleted ones.")
    @ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved customers")
    @GetMapping("/all")
    public ResponseEntity<List<CustomerDTO>> getAllCustomers() {
        return ResponseEntity.ok(customerService.getAllCustomer());
    }

    /**
     * Get all existed customers.
     */
    @Operation(
            summary = "Get active customers",
            description = "Retrieve all non-deleted customers.")
    @ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved active customers")
    @GetMapping
    public ResponseEntity<List<CustomerDTO>> getAllExistCustomers() {
        return ResponseEntity.ok(customerService.getAllExistCustomers());
    }

    /**
     * Get customer by ID.
     */
    @Operation(
            summary = "Get a customer by ID",
            description = "Retrieve customer details by their ID.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Customer found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CustomerDTO.class))),
            @ApiResponse(
                    responseCode = "404",
                    description = "Customer not found",
                    content = @Content)})
    @GetMapping("/{id}")
    public ResponseEntity<CustomerDTO> getCustomerById(
            @Parameter(description = "ID of the customer to retrieve") @PathVariable Integer id) {
        return ResponseEntity.ok(customerService.getCustomerById(id));
    }

    /**
     * Get a customer by username.
     */
    @Operation(
            summary = "Get a customer by username",
            description = "Retrieve customer details by username.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Customer found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CustomerDTO.class))),
            @ApiResponse(
                    responseCode = "404",
                    description = "Customer not found",
                    content = @Content)})
    @GetMapping("/username/{username}")
    public ResponseEntity<CustomerDTO> getCustomerByUsername(
            @Parameter(description = "Username of the customer to retrieve") @PathVariable String username) {
        return ResponseEntity.ok(customerService.getCustomerByUserName(username));
    }

    /**
     * Create a new customer.
     */
    @Operation(
            summary = "Create a new customer",
            description = "Add a new customer to the system.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Customer created successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CustomerDTO.class))),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input data",
                    content = @Content)})
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CustomerDTO createCustomer(
            @Parameter(description = "Customer object to be created") @Valid @RequestBody Customer customer,
            @AuthenticationPrincipal String loggedInUsername) {
        return customerService.createCustomer(customer, loggedInUsername);
    }

    /**
     * Update an existing customer.
     */
    @Operation(
            summary = "Update a customer",
            description = "Update customer details by their ID.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Customer updated successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CustomerDTO.class))),
            @ApiResponse(
                    responseCode = "404",
                    description = "Customer not found",
                    content = @Content)})
    @PutMapping("/{id}")
    public ResponseEntity<CustomerDTO> updateCustomer(
            @Parameter(description = "ID of the customer to update") @PathVariable Integer id,
            @Parameter(description = "Updated customer details") @Valid @RequestBody Customer updatedCustomer,
            @AuthenticationPrincipal String loggedInUsername) {
        return ResponseEntity.ok(customerService.updateCustomer(id, updatedCustomer, loggedInUsername));
    }

    /**
     * Delete a customer by ID (soft delete).
     */
    @Operation(
            summary = "Soft delete a customer",
            description = "Soft delete a customer by marking them as deleted.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Customer soft-deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Customer not found", content = @Content)})
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCustomer(
            @Parameter(description = "ID of the customer to delete") @PathVariable Integer id, @AuthenticationPrincipal String loggedInUsername) {
        customerService.softDeleteCustomer(id, loggedInUsername);
        return ResponseEntity.noContent().build();
    }
}
