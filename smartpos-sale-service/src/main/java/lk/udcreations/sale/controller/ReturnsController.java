package lk.udcreations.sale.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lk.udcreations.common.dto.returns.CreateReturnDTO;
import lk.udcreations.common.dto.returns.ReturnDTO;
import lk.udcreations.sale.service.ReturnsService;

@RestController
@RequestMapping("/api/v1/returns")
@Tag(name = "Returns API", description = "Endpoints for managing product returns")
public class ReturnsController {

	private final ReturnsService returnsService;

	public ReturnsController(ReturnsService returnsService) {
		this.returnsService = returnsService;
	}

	@PostMapping
	@Operation(summary = "Process a return", description = "Processes a return for a specific sale item.")
	public ResponseEntity<List<ReturnDTO>> processReturn(
			@Valid @RequestBody List<CreateReturnDTO> createReturnDTOList) {
		List<ReturnDTO> returnDTOList = returnsService.processReturn(createReturnDTOList);
		return ResponseEntity.status(HttpStatus.CREATED).body(returnDTOList);
	}

	@GetMapping("/sale/{saleId}")
	@Operation(summary = "Get returns by sale ID", description = "Fetch all returns for a specific sale.")
	public ResponseEntity<List<ReturnDTO>> getReturnsBySaleId(@PathVariable Integer saleId) {
		return ResponseEntity.ok(returnsService.getReturnsBySaleId(saleId));
	}
}

