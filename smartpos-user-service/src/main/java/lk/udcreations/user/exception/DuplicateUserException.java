package lk.udcreations.user.exception;

import java.io.Serial;

public class DuplicateUserException extends RuntimeException {

	@Serial
    private static final long serialVersionUID = 1L;

	public DuplicateUserException(String message) {
        super(message);
    }
}