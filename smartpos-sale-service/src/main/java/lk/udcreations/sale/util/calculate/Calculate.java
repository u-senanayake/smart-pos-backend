package lk.udcreations.sale.util.calculate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lk.udcreations.sale.entity.SalesItems;

public class Calculate {

	private static final Logger LOGGER = LoggerFactory.getLogger(Calculate.class);

	private Calculate() {
		throw new IllegalStateException("Utility class");
	}

	// Sale
	public static BigDecimal calcTotalPayments(BigDecimal cash, BigDecimal credit, BigDecimal qr, BigDecimal cheque,
			BigDecimal due) {
		return CalculateUtil.getSum(cash, credit, qr, cheque, due);
	}

	public static int calcTotalQuantity(List<SalesItems> items) {
		return items.stream().mapToInt(SalesItems::getQuantity).sum();
	}

	public static BigDecimal calcTotalAmount(List<SalesItems> items) {
		return items.stream().map(SalesItems::getTotalPrice).reduce(BigDecimal.ZERO, BigDecimal::add);
	}

	// Sale Item
	public static BigDecimal calculateDiscountedPrice(BigDecimal unitPrice, int discPer, BigDecimal discVal) {

		LOGGER.debug("Calculating discounted price");

		if (unitPrice == null || discVal == null) {
			throw new IllegalArgumentException("unitPrice, discPer, and discVal cannot be null");
		}

		// Calculate percentage discount: (unitPrice / 100) * (100 - discPer)
		BigDecimal percentageDiscount = unitPrice
				.multiply(BigDecimal.valueOf(100).subtract(BigDecimal.valueOf(discPer)))
				.divide(BigDecimal.valueOf(100), RoundingMode.HALF_UP);
		LOGGER.info("Percentage discount: {} ", percentageDiscount);

		// Calculate selling unit price: unitPrice - percentageDiscount - discVal
		BigDecimal flatDiscounted = percentageDiscount.subtract(discVal);
		LOGGER.info("Flat discount: {} ", discVal);
		LOGGER.info("Flat discounted value: {} ", flatDiscounted);
		return flatDiscounted;
	}

	public static BigDecimal calculateItemTotal(BigDecimal unitPrice, int discPer, BigDecimal discVal, int qty) {
		if (unitPrice == null || discVal == null) {
			throw new IllegalArgumentException("unitPrice, discPer, and discVal cannot be null");
		}
		return calculateDiscountedPrice(unitPrice, discPer, discVal).multiply(BigDecimal.valueOf(qty));

	}

}
