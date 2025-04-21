package lk.udcreations.user.exception;

import java.io.Serial;

public class InvalidInputException extends RuntimeException {

	@Serial
	private static final long serialVersionUID = 1L;

	public InvalidInputException(String message) {
		super(message);
	}
}
