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
import lk.udcreations.product.entity.Brand;
import lk.udcreations.product.service.BrandService;
import lk.udcreations.common.dto.brand.BrandDTO;

@RestController
@RequestMapping("/api/v1/brand")
@Tag(name = "Brand API", description = "Endpoints for managing product brands")
public class BrandController {

	private final BrandService brandService;

	public BrandController(BrandService brandService) {
		super();
		this.brandService = brandService;
	}

	/** Get all brands */
	@Operation(summary = "Get all brands", description = "Retrieve all brands, including soft-deleted ones.")
	@ApiResponse(responseCode = "200", description = "Successfully retrieved brands")
	@GetMapping("/all")
	public ResponseEntity<List<BrandDTO>> getAllBrands() {
		return ResponseEntity.ok(brandService.getAllBrands());
	}

	/** Get all non-deleted brand */
	@Operation(summary = "Get active brands", description = "Retrieve all non-deleted brands.")
	@ApiResponse(responseCode = "200", description = "Successfully retrieved active brands")
	@GetMapping
	public ResponseEntity<List<BrandDTO>> getAllExistBrands() {
		return ResponseEntity.ok(brandService.getAllExistBrands());
	}

	/** Get a brand by ID */
	@Operation(summary = "Get a brand by ID", description = "Retrieve brand details by its ID.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Brand found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = BrandDTO.class))),
			@ApiResponse(responseCode = "404", description = "Brand not found", content = @Content) })
	@GetMapping("/{brandId}")
	public ResponseEntity<BrandDTO> getBrandById(
			@Parameter(description = "ID of the brand to retrieve") @PathVariable Integer brandId) {
		return ResponseEntity.ok(brandService.getBrandById(brandId));
	}

	/** Create a new brand */
	@Operation(summary = "Create a new brand", description = "Add a new brand to the system.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "201", description = "Brand created successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = BrandDTO.class))),
			@ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content) })
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public BrandDTO createBrand(
			@Parameter(description = "Brand object to be created") @Valid @RequestBody Brand brand) {
		return brandService.createBrand(brand);
	}

	/** Update a Brand */
	@Operation(summary = "Update a brand", description = "Update brand details by its ID.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Brand updated successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = BrandDTO.class))),
			@ApiResponse(responseCode = "404", description = "Brand not found", content = @Content) })
	@PutMapping("/{brandId}")
	public ResponseEntity<BrandDTO> updateBrand(
			@Parameter(description = "ID of the brand to update") @PathVariable Integer brandId,
			@Parameter(description = "Updated brand details") @Valid @RequestBody Brand updatedBrand) {
		return ResponseEntity.ok(brandService.updateBrand(brandId, updatedBrand));
	}

	/** Delete a brand */
	@Operation(summary = "Soft delete a brand", description = "Soft delete a brand by marking it as deleted.")
	@ApiResponses(value = { @ApiResponse(responseCode = "204", description = "Brand soft-deleted successfully"),
			@ApiResponse(responseCode = "404", description = "Brand not found", content = @Content) })
	@DeleteMapping("/{brandId}")
	public ResponseEntity<Void> deleteBrand(
			@Parameter(description = "ID of the brand to delete") @PathVariable Integer brandId) {
		brandService.softDeleteBrand(brandId);
		return ResponseEntity.noContent().build();
	}

}
