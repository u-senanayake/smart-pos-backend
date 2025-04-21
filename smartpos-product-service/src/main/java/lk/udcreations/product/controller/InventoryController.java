package lk.udcreations.product.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lk.udcreations.common.dto.inventory.InventoryDTO;
import lk.udcreations.common.dto.inventory.StockDTO;
import lk.udcreations.product.entity.Inventory;
import lk.udcreations.product.service.InventoryService;

@RestController
@RequestMapping("/api/v1/inventory")
@Tag(name = "Inventory API", description = "Endpoints for managing product inventory")
public class InventoryController {

	private final InventoryService inventoryService;

	public InventoryController(InventoryService inventoryService) {
		super();
		this.inventoryService = inventoryService;
	}

	@Operation(summary = "Add stock", description = "Add stock to a product by product ID.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Stock added successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = InventoryDTO.class))),
			@ApiResponse(responseCode = "404", description = "Product not found", content = @Content) })
	@PostMapping("/addStock/{productId}")
	public ResponseEntity<InventoryDTO> addStock(
			@Parameter(description = "Product ID to add stock") @PathVariable Integer productId,
			@Parameter(description = "Quantity to add") @RequestBody StockDTO quantity) {
		return ResponseEntity.ok(inventoryService.addStock(productId, quantity.getQuantity()));
	}

	@Operation(summary = "Decrease stock", description = "Decrease stock for a product by product ID.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Stock decreased successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = InventoryDTO.class))),
			@ApiResponse(responseCode = "400", description = "Insufficient stock", content = @Content) })
	@PostMapping("/decreaseStock/{productId}")
	public ResponseEntity<InventoryDTO> decreaseStock(
			@Parameter(description = "Product ID to decrease stock") @PathVariable Integer productId,
			@Parameter(description = "Quantity to decrease") @RequestBody StockDTO quantity) {
		return ResponseEntity.ok(inventoryService.decreaseStock(productId, quantity.getQuantity()));
	}

	@Operation(summary = "Get stock level", description = "Fetch the current stock level of a product.")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Stock level fetched successfully"),
			@ApiResponse(responseCode = "404", description = "Product not found", content = @Content) })
	@GetMapping("/getStockLevel/{productId}")
	public ResponseEntity<Integer> getStockLevel(
			@Parameter(description = "Product ID to check stock level") @PathVariable Integer productId) {
		return ResponseEntity.ok(inventoryService.getStock(productId));
	}

	@Operation(summary = "Get product stock details", description = "Retrieve detailed stock information for a product.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Stock details fetched successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = InventoryDTO.class))),
			@ApiResponse(responseCode = "404", description = "Product not found", content = @Content) })
	@GetMapping("/getProductStockDetails/{productId}")
	public ResponseEntity<InventoryDTO> getProductStockDetails(
			@Parameter(description = "Product ID to get stock details") @PathVariable Integer productId) {
		return ResponseEntity.ok(inventoryService.getProductStockDetails(productId));
	}

	@Operation(summary = "Update stock level", description = "Update stock alert and warning levels for a product.")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Stock level updated successfully"),
			@ApiResponse(responseCode = "404", description = "Product not found", content = @Content) })
	@PutMapping("/updateStock/{productId}")
	public ResponseEntity<InventoryDTO> updateStockLevel(
			@Parameter(description = "Product ID to update stock") @PathVariable Integer productId,
			@Parameter(description = "Updated stock details") @RequestBody Inventory inventory) {
		return ResponseEntity.ok(inventoryService.updateStockLevel(productId, inventory));
	}

	@Operation(summary = "Get all inventory items", description = "Retrieve all inventory items.")
	@ApiResponse(responseCode = "200", description = "Successfully retrieved inventory list")
	@GetMapping("/all")
	public ResponseEntity<List<InventoryDTO>> getAllInventoryItems() {
		return ResponseEntity.ok(inventoryService.getAllInventoryItems());
	}

	@Operation(summary = "Check stock alerts", description = "Retrieve products below stock alert levels.")
	@ApiResponse(responseCode = "200", description = "Successfully retrieved stock alerts")
	@GetMapping("/alerts")
	public ResponseEntity<List<InventoryDTO>> checkStockAlert() {
		return ResponseEntity.ok(inventoryService.checkStockAlert());
	}

	@Operation(summary = "Check stock warnings", description = "Retrieve products below stock warning levels.")
	@ApiResponse(responseCode = "200", description = "Successfully retrieved stock warnings")
	@GetMapping("/warnings")
	public ResponseEntity<List<InventoryDTO>> checkStockWarning() {
		return ResponseEntity.ok(inventoryService.checkStockWarning());
	}

	
	@GetMapping("/checkStockAvailability/{productId}/{quantity}")
	public boolean checkStockAvailability(
			@PathVariable Integer productId, @PathVariable Integer quantity) {
		return inventoryService.checkStockAvailability(productId, quantity);
	}

}
