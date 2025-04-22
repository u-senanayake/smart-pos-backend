package lk.udcreations.sale.exception;

import java.io.Serial;

public class ProductNotActiveException extends RuntimeException {

	@Serial
	private static final long serialVersionUID = 1L;

	public ProductNotActiveException(String message) {
		super(message);
	}
}
