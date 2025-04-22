package lk.udcreations.product.exception;

import java.io.Serial;

public class InsufficientStockException extends RuntimeException {

	@Serial
	private static final long serialVersionUID = 1L;

	public InsufficientStockException(String message) {
		super(message);
	}
}
