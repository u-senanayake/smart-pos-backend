package lk.udcreations.product.exception;

import java.io.Serial;

public class DiscountMismatchException extends RuntimeException {

	@Serial
	private static final long serialVersionUID = 1L;

	public DiscountMismatchException(String msg) {
		super(msg);
	}

}
