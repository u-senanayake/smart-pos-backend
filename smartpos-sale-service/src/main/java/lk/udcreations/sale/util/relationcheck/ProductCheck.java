package lk.udcreations.sale.util.relationcheck;

import static lk.udcreations.sale.util.calculate.CalculateUtil.isEquals;

import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import lk.udcreations.common.dto.product.ProductDTO;
import lk.udcreations.common.dto.salesitems.CreateSalesItemDTO;
import lk.udcreations.sale.exception.UnitPriceMismatchException;
import lk.udcreations.sale.controller.ProductClientController;
import lk.udcreations.sale.util.calculate.Calculate;
import lk.udcreations.sale.util.calculate.CalculateUtil;

@Component
public class ProductCheck {

	private static final Logger LOGGER = LoggerFactory.getLogger(ProductCheck.class);

	private final ProductClientController productClientController;

	public ProductCheck(ProductClientController productClientController) {
		super();
		this.productClientController = productClientController;
	}

	public boolean isProductDeleted(ProductDTO product) {
		return productClientController.checkProductDeletedById(product.getId())
				|| productClientController.checkProductDeletedByProductId(product.getProductId());
	}

	public boolean isProductEnabled(ProductDTO product) {
		return productClientController.checkProductEnabledById(product.getId())
				|| productClientController.checkProductEnabledByProductId(product.getProductId());
	}

	public boolean isUnitPriceMatch(ProductDTO product, CreateSalesItemDTO item) {
		return isEquals(product.getPrice(), item.getPricePerUnit());
	}

	public boolean isDiscountMatch(ProductDTO product, CreateSalesItemDTO item) {

		if (!isUnitPriceMatch(product, item)) {
			throw new UnitPriceMismatchException("There is a problem with selling price.");
		} else {
			BigDecimal minDisc = product.getMinPrice();
			BigDecimal calcDisc = Calculate.calculateDiscountedPrice(item.getPricePerUnit(), item.getItemDiscountPer(),
					item.getItemDiscountVal());
			LOGGER.info("Checking discount. Product minimum discount: {}, calculated discount: {}", minDisc, calcDisc);
			return CalculateUtil.isLessThanOrEqual(minDisc, calcDisc);
		}
	}

	public boolean isTotalMatch(ProductDTO product, CreateSalesItemDTO item) {
		BigDecimal total = item.getTotalPrice();
		BigDecimal calcTotal = Calculate.calculateItemTotal(item.getPricePerUnit(), item.getItemDiscountPer(),
				item.getItemDiscountVal(), item.getQuantity());
		LOGGER.info("Checking total price. Received total: {}, calculated total: {}", total, calcTotal);
		return CalculateUtil.isEquals(total, calcTotal);
	}
}
