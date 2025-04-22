package lk.udcreations.sale.exception;

import java.io.Serial;

public class TotalAmountException extends RuntimeException {
	
	@Serial
	private static final long serialVersionUID = 1L;

	public TotalAmountException (String msg) {
		super(msg);
	}
}
