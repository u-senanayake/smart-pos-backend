package lk.udcreations.user.constants;

public class ErrorMessages {

	// Role-related error messages
    public static final String ROLE_NOT_FOUND = "Role not found with ID: ";
    public static final String ROLE_NAME_EXISTS = "Role name already exists.";

	// User-related error messages
	public static final String USER_NOT_FOUND = "User not found with ID: ";
	public static final String USER_NAME_EXISTS = "Username already exists.";

    // General error messages
    public static final String INVALID_INPUT = "Invalid input provided.";
    public static final String UNEXPECTED_ERROR = "An unexpected error occurred.";
	public static final String EMAIL_EXISTS = "Email already exists.";

    private ErrorMessages() {
		// Prevent instantiation
	}
}

