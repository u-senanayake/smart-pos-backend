package lk.udcreations.sale.exception;

public class TotalAmountException extends RuntimeException {
	
	private static final long serialVersionUID = 1L;

	public TotalAmountException (String msg) {
		super(msg);
	}
}
