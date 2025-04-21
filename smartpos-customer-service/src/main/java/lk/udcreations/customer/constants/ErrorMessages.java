package lk.udcreations.customer.constants;

public class ErrorMessages {

	// Role-related error messages
    public static final String ROLE_NOT_FOUND = "Role not found with ID: ";
    public static final String ROLE_NAME_EXISTS = "Role name already exists.";

	// User-related error messages
	public static final String USER_NOT_FOUND = "User not found with ID: ";
	public static final String USER_NAME_EXISTS = "Username already exists.";

	
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

		// Customer groups-related error messages
		public static final String CUSTOMERGROUP_NOT_FOUND = "Customer group not found with ID: ";
		public static final String CUSTOMERGROUP_NAME_EXISTS = "Customer group name already exists.";

		// Customer-related error messages
		public static final String CUSTOMER_NOT_FOUND = "Customer not found with ID: ";
		public static final String CUSTOMER_NAME_EXISTS = "Customer name already exists.";

		// Sale-related error messages
		public static final String SALE_NOT_FOUND = "Sale not found with ID: ";
		public static final String TOTAL_AMOUNT_NOT_MATCH = "Total amount is not matching.";
		public static final String PAYMENT_AMOUNT_NOT_MATCH = "Payment amount is not matching.";
		public static final String ITEM_COUNT_NOT_MATCH = "Item count is not matching.";

		// Sale item-related error messages
		public static final String SALESITEM_NOT_FOUND = "Sale item not found with ID: ";

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

