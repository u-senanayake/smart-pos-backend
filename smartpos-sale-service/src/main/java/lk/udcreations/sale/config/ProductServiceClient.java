package lk.udcreations.sale.config;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import lk.udcreations.common.dto.inventory.InventoryDTO;
import lk.udcreations.common.dto.inventory.StockDTO;
import lk.udcreations.common.dto.product.ProductDTO;

@FeignClient(name = "product-service")
public interface ProductServiceClient {

	@GetMapping("/api/v1/product/{id}")
	public ProductDTO getProductById(@PathVariable Integer id);

	@GetMapping("/api/v1/product/productId/deleted/{productId}")
	public boolean checkProductDeletedByProductId(@PathVariable String productId);

	@GetMapping("/api/v1/product/id/deleted/{id}")
	public boolean checkProductDeletedById(@PathVariable Integer id);

	@GetMapping("/api/v1/product/productId/enabled/{productId}")
	public boolean checkProductEnabledByProductId(@PathVariable String productId);

	@GetMapping("/api/v1/product/id/enabled/{id}")
	public boolean checkProductEnabledById(@PathVariable Integer id);

	@PostMapping("/api/v1/inventory/addStock/{productId}")
	public InventoryDTO addStock(@PathVariable Integer productId, @RequestBody StockDTO quantity);

	@GetMapping("/api/v1/inventory/checkStockAvailability/{productId}/{quantity}")
	public boolean checkStockAvailability(@PathVariable Integer productId, @PathVariable Integer quantity);

}
