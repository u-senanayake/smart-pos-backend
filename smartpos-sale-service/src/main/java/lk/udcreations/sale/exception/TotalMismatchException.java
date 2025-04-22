package lk.udcreations.sale.exception;

import java.io.Serial;

public class TotalMismatchException extends RuntimeException {

	@Serial
	private static final long serialVersionUID = 1L;

	public TotalMismatchException(String msg) {
		super(msg);
	}


}
