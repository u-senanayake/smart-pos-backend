package lk.udcreations.product.constants;

public class ErrorMessages {

	// Category-related error messages
	public static final String CATEGORY_NOT_FOUND = "Category not found with ID: ";
	public static final String CATEGORY_NAME_EXISTS = "Category name already exists.";

	// Brand-related error messages
	public static final String BRAND_NOT_FOUND = "Brand not found with ID: ";
	public static final String BRAND_NAME_EXISTS = "Brand name already exists.";

	// Product-related error messages
	public static final String PRODUCT_NOT_FOUND = "Product not found with ID: ";
	public static final String PRODUCT_NAME_EXISTS = "Product name already exists.";

	// Distributor-related error messages
	public static final String DISTRIBUTOR_NOT_FOUND = "Distributor not found with ID: ";
	public static final String DISTRIBUTOR_NAME_EXISTS = "Distributor name already exists.";

	// Inventory-related error messages
	public static final String INVENTORY_NOT_FOUND = "Inventory not found with ID: ";
	public static final String INVENTORY_NOT_ENOUGHT_STOCK = "Not enough stock available";

	// Product-related error messages
	public static final String PRODUCT_NOT_ACTIVE = "This product cannot sell.";

    // General error messages
    public static final String INVALID_INPUT = "Invalid input provided.";
    public static final String UNEXPECTED_ERROR = "An unexpected error occurred.";
	public static final String EMAIL_EXISTS = "Email already exists.";

    private ErrorMessages() {
		// Prevent instantiation
	}
}
