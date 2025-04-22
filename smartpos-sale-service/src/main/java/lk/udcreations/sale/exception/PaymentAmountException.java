package lk.udcreations.sale.exception;

import java.io.Serial;

public class PaymentAmountException extends RuntimeException {

	@Serial
	private static final long serialVersionUID = 1L;

	public PaymentAmountException(String msg) {
		super(msg);
	}
}
