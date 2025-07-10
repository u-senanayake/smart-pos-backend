package lk.udcreations.sale.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lk.udcreations.common.dto.sale.CreateSaleDTO;
import lk.udcreations.common.dto.sale.FinalizeSaleDTO;
import lk.udcreations.common.dto.sale.SaleDTO;
import lk.udcreations.common.dto.sale.UpdateSaleDTO;
import lk.udcreations.sale.service.SalesService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/sale")
@Tag(name = "Sales API", description = "Endpoints for managing sales, including creation, update, deletion, and queries.")
public class SalesController {

    private final SalesService salesService;

    public SalesController(SalesService salesService) {
        super();
        this.salesService = salesService;
    }

    /**
     * Create a new sale
     */
    @Operation(
            summary = "Create a new sale",
            description = "Create a new sale record.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Sale created successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = SaleDTO.class))),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input",
                    content = @Content)})
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SaleDTO createSale(
            @Parameter(description = "Sale creation data", required = true) @Valid @RequestBody CreateSaleDTO createSale,
            @Parameter(hidden = true) @AuthenticationPrincipal String loggedInUsername) {
        return salesService.createSale(createSale, loggedInUsername);
    }

    /**
     * Update an existing sale
     */
    @Operation(
            summary = "Update an existing sale",
            description = "Update sale details by sale ID.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Sale updated successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = SaleDTO.class))),
            @ApiResponse(
                    responseCode = "404",
                    description = "Sale not found",
                    content = @Content)})
    @PutMapping("/{saleId}")
    public ResponseEntity<SaleDTO> updateSale(
            @Parameter(description = "ID of the sale to update", required = true) @PathVariable Integer saleId,
            @Parameter(description = "Updated sale data", required = true) @Valid @RequestBody UpdateSaleDTO updatedSale) {
        return ResponseEntity.ok(salesService.updateSale(saleId, updatedSale));
    }

    /**
     * Get all sales
     */
    @Operation(
            summary = "Get all sales",
            description = "Retrieve all sales records.")
    @ApiResponse(
            responseCode = "200",
            description = "List of sales",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = SaleDTO.class)))
    @GetMapping
    public ResponseEntity<List<SaleDTO>> getAllSales() {
        return new ResponseEntity<>(salesService.getAllSales(), HttpStatus.OK);
    }

    /**
     * Get sales by payment status
     */
    @Operation(
            summary = "Get sales by payment status",
            description = "Retrieve sales filtered by payment status.")
    @ApiResponse(
            responseCode = "200",
            description = "List of sales with given payment status",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = SaleDTO.class)))
    @GetMapping("/payment/status/{status}")
    public ResponseEntity<List<SaleDTO>> getSalesByPaymentStatus(
            @Parameter(description = "Payment status to filter by", required = true) @PathVariable String status) {
        List<SaleDTO> salesList = salesService.getSalesByPaymentStatus(status);
        return new ResponseEntity<>(salesList, HttpStatus.OK);
    }

    /**
     * Get draft sales
     */
    @Operation(
            summary = "Get draft sales",
            description = "Retrieve all sales with draft payment status.")
    @ApiResponse(
            responseCode = "200",
            description = "List of draft sales",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = SaleDTO.class)))
    @GetMapping("/payment/draft")
    public ResponseEntity<List<SaleDTO>> getDraftSales() {
        List<SaleDTO> salesList = salesService.getDraftSales();
        return new ResponseEntity<>(salesList, HttpStatus.OK);
    }

    /**
     * Get sales history
     */
    @Operation(
            summary = "Get sales history",
            description = "Retrieve all sales that are not drafts.")
    @ApiResponse(
            responseCode = "200",
            description = "List of non-draft sales",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = SaleDTO.class)))
    @GetMapping("/payment/notdraft")
    public ResponseEntity<List<SaleDTO>> getSalesHistory() {
        List<SaleDTO> salesList = salesService.getSalesHistory();
        return new ResponseEntity<>(salesList, HttpStatus.OK);
    }

    /**
     * Get sales by customer ID
     */
    @Operation(
            summary = "Get sales by customer ID",
            description = "Retrieve all sales for a specific customer.")
    @ApiResponse(
            responseCode = "200",
            description = "List of sales for the customer",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = SaleDTO.class)))
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<SaleDTO>> getSalesByCustomerId(
            @Parameter(description = "Customer ID", required = true) @PathVariable Integer customerId) {
        List<SaleDTO> salesList = salesService.getSalesByCustomerId(customerId);
        return new ResponseEntity<>(salesList, HttpStatus.OK);
    }

    /**
     * Get a sale by ID
     */
    @Operation(
            summary = "Get a sale by ID",
            description = "Retrieve a sale by its ID.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Sale found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = SaleDTO.class))),
            @ApiResponse(
                    responseCode = "404",
                    description = "Sale not found", content = @Content)})
    @GetMapping("/{saleId}")
    public ResponseEntity<SaleDTO> getSaleById(
            @Parameter(description = "ID of the sale to retrieve", required = true) @PathVariable Integer saleId) {
        return ResponseEntity.ok(salesService.getSaleById(saleId));
    }

    /**
     * Finalize a sale
     */
    @Operation(
            summary = "Finalize a sale",
            description = "Finalize a sale by updating its status and details.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Sale finalized successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = SaleDTO.class))),
            @ApiResponse(
                    responseCode = "404",
                    description = "Sale not found",
                    content = @Content)})
    @PutMapping("/finalize/{saleId}")
    public ResponseEntity<SaleDTO> finalizeSale(
            @Parameter(description = "ID of the sale to finalize", required = true) @PathVariable Integer saleId,
            @Parameter(description = "Finalize sale data", required = true) @Valid @RequestBody FinalizeSaleDTO finalizeSale) {
        return ResponseEntity.ok(salesService.finalizeSale(saleId, finalizeSale));
    }

    /**
     * Delete a sale
     */
    @Operation(
            summary = "Delete a sale",
            description = "Delete a sale by its ID.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Sale deleted successfully"),
            @ApiResponse(
                    responseCode = "404",
                    description = "Sale not found",
                    content = @Content)})
    @DeleteMapping("/{saleId}")
    public ResponseEntity<Void> deleteSale(
            @Parameter(description = "ID of the sale to delete", required = true) @PathVariable Integer saleId) {
        try {
            salesService.deleteSale(saleId);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

}
