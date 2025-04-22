package lk.udcreations.product.exception;

import java.io.Serial;

public class UnitPriceMismatchException extends RuntimeException {

	@Serial
	private static final long serialVersionUID = 1L;

	public UnitPriceMismatchException(String message) {
		super(message);
	}

}
