package lk.udcreations.sale.util.relationcheck;

import org.springframework.stereotype.Component;

import lk.udcreations.sale.controller.ProductClientController;

@Component
public class InventoryCheck {

	private final ProductClientController productClientController;

	public InventoryCheck(ProductClientController productClientController) {
		super();
		this.productClientController = productClientController;
	}

	public boolean checkStockAvailability(Integer productId, int quantity) {
		return productClientController.checkStockAvailability(productId, quantity);
	}
}
