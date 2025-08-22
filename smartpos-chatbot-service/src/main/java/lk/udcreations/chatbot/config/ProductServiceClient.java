package lk.udcreations.chatbot.config;

import java.util.List;

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
	ProductDTO getProductById(@PathVariable Integer id);

	@GetMapping("/api/v1/product/productId/deleted/{productId}")
	boolean checkProductDeletedByProductId(@PathVariable String productId);

	@GetMapping("/api/v1/product/id/deleted/{id}")
	boolean checkProductDeletedById(@PathVariable Integer id);

	@GetMapping("/api/v1/product/productId/enabled/{productId}")
	boolean checkProductEnabledByProductId(@PathVariable String productId);

	@GetMapping("/api/v1/product/id/enabled/{id}")
	boolean checkProductEnabledById(@PathVariable Integer id);

	@PostMapping("/api/v1/inventory/addStock/{productId}")
	InventoryDTO addStock(@PathVariable Integer productId, @RequestBody StockDTO quantity);

	@GetMapping("/api/v1/inventory/checkStockAvailability/{productId}/{quantity}")
	boolean checkStockAvailability(@PathVariable Integer productId, @PathVariable Integer quantity);
	
	@GetMapping("/api/vi/inventory/{productId}")
	InventoryDTO getInventoryByProductId( @PathVariable Integer productId);
	
	 @GetMapping("api/v1/product/sku/{sku}")
	 ProductDTO findBySku(@PathVariable String sku);
	 
	 @GetMapping("api/v1/product/name/search/{name}")
	 List<ProductDTO> searchProductsByName( @PathVariable String name);

}