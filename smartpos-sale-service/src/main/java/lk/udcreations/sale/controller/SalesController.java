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

import jakarta.validation.Valid;
import lk.udcreations.common.dto.sale.CreateSaleDTO;
import lk.udcreations.common.dto.sale.FinalizeSaleDTO;
import lk.udcreations.common.dto.sale.SaleDTO;
import lk.udcreations.common.dto.sale.UpdateSaleDTO;
import lk.udcreations.sale.service.SalesService;

@RestController
@RequestMapping("/api/v1/sale")
public class SalesController {

	private final SalesService salesService;

	public SalesController(SalesService salesService) {
		super();
		this.salesService = salesService;
	}

	/** Create a new sale */
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public SaleDTO createSale(@Valid @RequestBody CreateSaleDTO createSale) {
		return salesService.createSale(createSale);
	}

	/** Update an existing sale */
	@PutMapping("/{saleId}")
	public ResponseEntity<SaleDTO> updateSale(@PathVariable Integer saleId,
			@Valid @RequestBody UpdateSaleDTO updatedSale) {

		return ResponseEntity.ok(salesService.updateSale(saleId, updatedSale));
	}

	/** Get all sales */
	@GetMapping
	public ResponseEntity<List<SaleDTO>> getAllSales() {
		return new ResponseEntity<>(salesService.getAllSales(), HttpStatus.OK);
	}

	/** Get sales by payment status */
	@GetMapping("/payment/status/{status}")
	public ResponseEntity<List<SaleDTO>> getSalesByPaymentStatus(@PathVariable String status) {
		List<SaleDTO> salesList = salesService.getSalesByPaymentStatus(status);
		return new ResponseEntity<>(salesList, HttpStatus.OK);
	}
	
	/** Get draft sales */
	@GetMapping("/payment/draft")
	public ResponseEntity<List<SaleDTO>> getDraftSales() {
		List<SaleDTO> salesList = salesService.getDraftSales();
		return new ResponseEntity<>(salesList, HttpStatus.OK);
	}

	/** Get sales history*/
	@GetMapping("/payment/notdraft")
	public ResponseEntity<List<SaleDTO>> getSalesHistory() {
		List<SaleDTO> salesList = salesService.getSalesHistory();
		return new ResponseEntity<>(salesList, HttpStatus.OK);
	}

	/** Get sales by customer ID */
	@GetMapping("/customer/{customerId}")
	public ResponseEntity<List<SaleDTO>> getSalesByCustomerId(@PathVariable Integer customerId) {
		List<SaleDTO> salesList = salesService.getSalesByCustomerId(customerId);
		return new ResponseEntity<>(salesList, HttpStatus.OK);
	}

	/** Get a sale by ID */
	@GetMapping("/{saleId}")
	public ResponseEntity<SaleDTO> getSaleById(@PathVariable Integer saleId) {
		return ResponseEntity.ok(salesService.getSaleById(saleId));
	}

	/** Create a new sale */
	@PutMapping("/finalize/{saleId}")
	public ResponseEntity<SaleDTO> finalizeSale(@PathVariable Integer saleId,
			@Valid @RequestBody FinalizeSaleDTO finalizeSale) {
		return ResponseEntity.ok(salesService.finalizeSale(saleId, finalizeSale));

	}

	/** Delete a sale */
	@DeleteMapping("/{saleId}")
	public ResponseEntity<Void> deleteSale(@PathVariable Integer saleId) {
		try {
			salesService.deleteSale(saleId);
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		} catch (RuntimeException e) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}



}
