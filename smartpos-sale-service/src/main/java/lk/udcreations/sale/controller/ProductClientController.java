package lk.udcreations.sale.controller;

import org.springframework.stereotype.Component;

import lk.udcreations.common.dto.inventory.InventoryDTO;
import lk.udcreations.common.dto.inventory.StockDTO;
import lk.udcreations.common.dto.product.ProductDTO;
import lk.udcreations.sale.config.ProductServiceClient;

@Component
public class ProductClientController {

	private final ProductServiceClient productServiceClient;

	public ProductClientController(ProductServiceClient productServiceClient) {
		super();
		this.productServiceClient = productServiceClient;
	}

	public ProductDTO getProductById(Integer id) {
		return productServiceClient.getProductById(id);
	}

	public boolean checkProductDeletedByProductId(String productId) {
		return productServiceClient.checkProductDeletedByProductId(productId);
	}

	public boolean checkProductDeletedById(Integer id) {
		return productServiceClient.checkProductDeletedById(id);
	}

	public boolean checkProductEnabledByProductId(String productId) {
		return productServiceClient.checkProductEnabledByProductId(productId);
	}

	public boolean checkProductEnabledById(Integer id) {
		return productServiceClient.checkProductEnabledById(id);
	}

	public InventoryDTO addStock(Integer productId, StockDTO quantity) {
		return productServiceClient.addStock(productId, quantity);
	}

	public boolean checkStockAvailability(Integer productId, Integer quantity) {
		return productServiceClient.checkStockAvailability(productId, quantity);
	}
}
