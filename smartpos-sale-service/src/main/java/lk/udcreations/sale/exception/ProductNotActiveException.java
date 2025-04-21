package lk.udcreations.sale.exception;

public class ProductNotActiveException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public ProductNotActiveException(String message) {
		super(message);
	}
}
