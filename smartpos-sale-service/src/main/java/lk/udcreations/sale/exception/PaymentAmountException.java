package lk.udcreations.sale.exception;

public class PaymentAmountException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public PaymentAmountException(String msg) {
		super(msg);
	}
}
