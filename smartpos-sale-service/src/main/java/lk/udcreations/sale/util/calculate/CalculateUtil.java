package lk.udcreations.sale.util.calculate;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Objects;

public class CalculateUtil {

	private CalculateUtil() {
		throw new IllegalStateException("Utility class");
	}

	// BigDecimal
	public static BigDecimal getSum(BigDecimal bd1, BigDecimal bd2) {
		return checkNull(bd1).add(checkNull(bd2));
	}

	public static BigDecimal getSum(BigDecimal... values) {
		return Arrays.stream(values).filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add);
	}

	public static boolean isEquals(BigDecimal bd1, BigDecimal bd2) {
		return checkNull(bd1).stripTrailingZeros().compareTo(checkNull(bd2).stripTrailingZeros()) == 0;
	}

	public static boolean isLessThanOrEqual(BigDecimal bd1, BigDecimal bd2) {
		return checkNull(bd1).stripTrailingZeros().compareTo(checkNull(bd2).stripTrailingZeros()) <= 0;
	}

	public static BigDecimal applyDiscount(BigDecimal orgPrice, int per) {

		if (orgPrice == null || per < 0 || per > 100) {
			throw new IllegalArgumentException("Invalid input: originalPrice or discountPercentage");
		}
		return orgPrice.multiply(BigDecimal.valueOf(per)).divide(BigDecimal.valueOf(100));
	}

	public static BigDecimal checkNull(BigDecimal bd) {
		return bd == null ? BigDecimal.ZERO : bd;
	}

	// Integer
	public static boolean isEquals(Integer int1, Integer int2) {
		return checkNull(int1).equals(checkNull(int2));
	}

	private static Integer checkNull(Integer value) {
		return value == null ? 0 : value;
	}

	public static int getSum(int i1, int i2) {
		return i1 + i2;
	}

}
