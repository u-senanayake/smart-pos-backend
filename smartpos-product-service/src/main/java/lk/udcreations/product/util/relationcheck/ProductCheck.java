package lk.udcreations.product.util.relationcheck;

import static lk.udcreations.product.util.calculate.CalculateUtil.isEquals;

import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import lk.udcreations.common.dto.salesitems.CreateSalesItemDTO;
import lk.udcreations.product.util.calculate.Calculate;
import lk.udcreations.product.util.calculate.CalculateUtil;
import lk.udcreations.product.entity.Product;
import lk.udcreations.product.exception.UnitPriceMismatchException;
import lk.udcreations.product.repository.ProductRepository;
@Component
public class ProductCheck {

	private static final Logger LOGGER = LoggerFactory.getLogger(ProductCheck.class);

	private final ProductRepository repository;

	public ProductCheck(ProductRepository repository) {
		super();
		this.repository = repository;
	}

	public boolean isProductDeleted(Product product) {
		return repository.findByIdAndDeletedTrue(product.getId()).isPresent()
				|| repository.findByProductIdAndDeletedTrue(product.getProductId()).isPresent();
	}

	public boolean isProductEnabled(Product product) {
		return repository.findByIdAndEnabledTrue(product.getId()).isPresent()
				|| repository.findByProductIdAndEnabledTrue(product.getProductId()).isPresent();
	}

	public boolean isUnitPriceMatch(Product product, CreateSalesItemDTO item) {
		return isEquals(product.getPrice(), item.getPricePerUnit());
	}

	public boolean isDiscountMatch(Product product, CreateSalesItemDTO item) {

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

	public boolean isTotalMatch(Product product, CreateSalesItemDTO item) {
		BigDecimal total = item.getTotalPrice();
		BigDecimal calcTotal = Calculate.calculateItemTotal(item.getPricePerUnit(), item.getItemDiscountPer(),
				item.getItemDiscountVal(), item.getQuantity());
		LOGGER.info("Checking total price. Received total: {}, calculated total: {}", total, calcTotal);
		return CalculateUtil.isEquals(total, calcTotal);
	}
}
