package lk.udcreations.sale.exception;

import java.io.Serial;

public class TotalQuantityException extends RuntimeException {

	@Serial
	private static final long serialVersionUID = 1L;

	public TotalQuantityException(String msg) {
		super(msg);
	}

}
