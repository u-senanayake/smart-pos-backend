package lk.udcreations.sale.controller;

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
import lk.udcreations.common.dto.salesitems.CreateSalesItemDTO;
import lk.udcreations.common.dto.salesitems.SalesItemDTO;
import lk.udcreations.sale.service.SalesItemsService;

@RestController
@RequestMapping("/api/v1/salesitem")
@Tag(name = "Sales Item Controller", description = "Manage sales items for the POS system")
public class SalesItemController {
	
	private final SalesItemsService salesItemsService;

	public SalesItemController(SalesItemsService salesItemsService) {
		super();
		this.salesItemsService = salesItemsService;
	}

	/** Create a new sales item */
	@Operation(summary = "Create a new sales item", description = "Add a new sales item to the system.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "201", description = "Sales item created successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = SalesItemDTO.class))),
			@ApiResponse(responseCode = "400", description = "Invalid input", content = @Content) })
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public SalesItemDTO createSalesItem(
			@Valid @RequestBody @Parameter(description = "Details of the sales item to be created", required = true) CreateSalesItemDTO createSaleItem) {
		return salesItemsService.createSalesItem(createSaleItem);
	}

	/** Update sales item */
	@Operation(summary = "Update an existing sales item", description = "Modify the details of an existing sales item.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Sales item updated successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = SalesItemDTO.class))),
			@ApiResponse(responseCode = "404", description = "Sales item not found", content = @Content),
			@ApiResponse(responseCode = "400", description = "Invalid input", content = @Content) })
	@PutMapping("/{salItemId}")
	public ResponseEntity<SalesItemDTO> updateSalesItem(
			@PathVariable @Parameter(description = "ID of the sales item to update", required = true) Integer salItemId,
			@Valid @RequestBody @Parameter(description = "Updated details of the sales item", required = true) CreateSalesItemDTO salesItem) {
		return ResponseEntity.ok(salesItemsService.updateSalesItem(salItemId, salesItem));
	}

	/** Get all sales items by sale ID */
	@Operation(summary = "Get sales items by sale ID", description = "Retrieve all sales items associated with a specific sale.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Sales items retrieved successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = SalesItemDTO.class))),
			@ApiResponse(responseCode = "404", description = "Sale not found", content = @Content) })
	@GetMapping("/sale/{saleId}")
	public ResponseEntity<List<SalesItemDTO>> getSaleItemsBySaleId(
			@PathVariable @Parameter(description = "ID of the sale", required = true) Integer saleId) {
		List<SalesItemDTO> saleItemList = salesItemsService.getAllSaleItemsBySaleId(saleId);
		return new ResponseEntity<>(saleItemList, HttpStatus.OK);
	}
	
	/** Delete a sales item */
	@Operation(summary = "Delete a sales item", description = "Remove a sales item from the system.")
	@ApiResponses(value = { @ApiResponse(responseCode = "204", description = "Sales item deleted successfully"),
			@ApiResponse(responseCode = "404", description = "Sales item not found", content = @Content) })
	@DeleteMapping("/{salItemId}")
	public ResponseEntity<Void> deleteSale(
			@PathVariable @Parameter(description = "ID of the sales item to delete", required = true) Integer salItemId) {
		salesItemsService.deleteSaleItem(salItemId);
		return ResponseEntity.noContent().build();
	}
	

}
