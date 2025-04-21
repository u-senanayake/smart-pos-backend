package lk.udcreations.product.controller;

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
import lk.udcreations.common.dto.distributor.DistributorDTO;
import lk.udcreations.product.entity.Distributor;
import lk.udcreations.product.service.DistributorService;

@RestController
@RequestMapping("/api/v1/distributor")
@Tag(name = "Distributor API", description = "Endpoints for managing product distributors")
public class DistributorController {

	private final DistributorService distributorService;

	public DistributorController(DistributorService distributorService) {
		super();
		this.distributorService = distributorService;
	}

	/** Get all distributor */
	@Operation(summary = "Get all distributors", description = "Retrieve all distributors, including soft-deleted ones.")
	@ApiResponse(responseCode = "200", description = "Successfully retrieved distributors")
	@GetMapping("/all")
	public ResponseEntity<List<DistributorDTO>> getAllDistributors() {
		return ResponseEntity.ok(distributorService.getAllDistributors());
	}

	/** Get all non-deleted distributor */
	@Operation(summary = "Get active distributors", description = "Retrieve all non-deleted distributors.")
	@ApiResponse(responseCode = "200", description = "Successfully retrieved active distributors")
	@GetMapping
	public ResponseEntity<List<DistributorDTO>> getAllExistDistributors() {
		return ResponseEntity.ok(distributorService.getAllExistDistributors());
	}

	/** Get a distributor by ID */
	@Operation(summary = "Get a distributor by ID", description = "Retrieve distributor details by its ID.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Distributor found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = DistributorDTO.class))),
			@ApiResponse(responseCode = "404", description = "Distributor not found", content = @Content) })
	@GetMapping("/{distributorId}")
	public ResponseEntity<DistributorDTO> getDistributorById(
			@Parameter(description = "ID of the distributor to retrieve") @PathVariable Integer distributorId) {
		return ResponseEntity.ok(distributorService.getDistributorById(distributorId));
	}

	/** Create a new distributor */
	@Operation(summary = "Create a new distributor", description = "Add a new distributor to the system.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "201", description = "Distributor created successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = DistributorDTO.class))),
			@ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content) })
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public DistributorDTO createDistributor(
			@Parameter(description = "Distributor object to be created") @Valid @RequestBody Distributor distributor) {
		return distributorService.createDistributor(distributor);
	}

	/** Update a distributor */
	@Operation(summary = "Update a distributor", description = "Update distributor details by its ID.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Distributor updated successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = DistributorDTO.class))),
			@ApiResponse(responseCode = "404", description = "Distributor not found", content = @Content) })
	@PutMapping("/{distributorId}")
	public ResponseEntity<DistributorDTO> updateDistributor(
			@Parameter(description = "ID of the distributor to update") @PathVariable Integer distributorId,
			@Parameter(description = "Updated distributor details") @Valid @RequestBody Distributor updatedDistributor) {
		return ResponseEntity.ok(distributorService.updateDistributor(distributorId, updatedDistributor));
	}

	/** Delete a distributor */
	@Operation(summary = "Soft delete a distributor", description = "Soft delete a distributor by marking it as deleted.")
	@ApiResponses(value = { @ApiResponse(responseCode = "204", description = "Distributor soft-deleted successfully"),
			@ApiResponse(responseCode = "404", description = "Distributor not found", content = @Content) })
	@DeleteMapping("/{distributorId}")
	public ResponseEntity<Void> deleteDistributor(
			@Parameter(description = "ID of the distributor to delete") @PathVariable Integer distributorId) {
		distributorService.softDeleteDistributor(distributorId);
		return ResponseEntity.noContent().build();
	}

}
