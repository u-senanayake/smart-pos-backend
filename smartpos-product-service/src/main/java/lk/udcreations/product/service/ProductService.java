package lk.udcreations.product.service;

import static lk.udcreations.product.util.calculate.DateUtils.convertStringToLocalDateTime;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import lk.udcreations.product.constants.ErrorMessages;
import lk.udcreations.common.dto.category.CategoryDTO;
import lk.udcreations.common.dto.distributor.DistributorDTO;
import lk.udcreations.common.dto.inventory.InventoryDTO;
import lk.udcreations.common.dto.product.CreateProductDTO;
import lk.udcreations.common.dto.product.ProductDTO;
import lk.udcreations.common.dto.user.CreatedUpdatedUserDTO;
import lk.udcreations.common.dto.user.UsersDTO;
import lk.udcreations.product.exception.NotFoundException;
import lk.udcreations.product.entity.Category;
import lk.udcreations.product.entity.Distributor;
import lk.udcreations.product.entity.Inventory;
import lk.udcreations.product.entity.Product;
import lk.udcreations.product.repository.CategoryRepository;
import lk.udcreations.product.repository.DistributorRepository;
import lk.udcreations.product.repository.InventoryRepository;
import lk.udcreations.product.repository.ProductRepository;
import lk.udcreations.product.security.AuthUtils;

@Service
public class ProductService {

	private static final Logger LOGGER = LoggerFactory.getLogger(ProductService.class);

	private final ProductRepository productRepository;
	private final InventoryRepository inventoryRepository;
	private final CategoryRepository categoryRepository;
	private final DistributorRepository distributorRepository;
	private final AuthUtils authUtils;
	private final ModelMapper modelMapper;
	
	public ProductService(ProductRepository productRepository, InventoryRepository inventoryRepository,
			CategoryRepository categoryRepository, DistributorRepository distributorRepository, AuthUtils authUtils,
			ModelMapper modelMapper) {
		super();
		this.productRepository = productRepository;
		this.inventoryRepository = inventoryRepository;
		this.categoryRepository = categoryRepository;
		this.distributorRepository = distributorRepository;
		this.authUtils = authUtils;
		this.modelMapper = modelMapper;
	}

	/** Get all products */
	public List<ProductDTO> getAllProducts() {

		LOGGER.debug("Fetching all products from the database.");

		List<Product> products = productRepository.findAll();
		if (products.isEmpty()) {
			LOGGER.warn("No products found in the database.");
		} else {
			LOGGER.info("Successfully fetched {} products from the database.", products.size());
		}

		return products.stream().map(this::convertToDTO).collect(Collectors.toList());
	}

	/** Get all non-deleted product */
	public List<ProductDTO> getAllExistProducts() {

		LOGGER.debug("Fetching all non-deleted roles.");

		List<Product> products = productRepository.findByDeletedFalse();
		if (products.isEmpty()) {
			LOGGER.warn("No active products found in the system.");
		} else {
			LOGGER.info("Fetched {} active products from the database.", products.size());
		}

		return products.stream().map(this::convertToDTO).collect(Collectors.toList());
	}

	/** Get a productDTO by ID */
	public ProductDTO getProductDTOById(Integer id) {

		LOGGER.debug("Fetching product with ID: {}", id);

		Product product = productRepository.findById(id).orElseThrow(() -> {
			String errorMessage = ErrorMessages.PRODUCT_NOT_FOUND + id;
			LOGGER.error("Product fetch failed. {}", errorMessage);
			return new NotFoundException(errorMessage);
		});

		LOGGER.info("Successfully fetched product with ID: {} and name: '{}'", product.getProductId(),
				product.getProductName());
		return convertToDTO(product);
	}
	
	public boolean checkProductDeletedByProductId(String productId) {
		LOGGER.debug("Fetching product with ID: {}", productId);

		Optional<Product> product = productRepository.findByProductIdAndDeletedTrue(productId);

		return product.isPresent();
	}
	
	public boolean checkProductDeletedById(Integer id) {
		LOGGER.debug("Fetching product with ID: {}", id);
		Optional<Product> product = productRepository.findByIdAndDeletedTrue(id);
		return product.isPresent();
	}
	
	public boolean checkProductEnabledByProductId(String productId) {
		LOGGER.debug("Fetching product with ID: {}", productId);

		Optional<Product> product = productRepository.findByProductIdAndEnabledTrue(productId);

		return product.isPresent();
	}
	
	public boolean checkProductEnabledById(Integer id) {
		LOGGER.debug("Fetching product with ID: {}", id);

		Optional<Product> product = productRepository.findByIdAndEnabledTrue(id);

		return product.isPresent();
	}
	
	/** Create a new product */
	@Transactional
	public ProductDTO createProduct(CreateProductDTO createProduct) {

		LOGGER.debug("Attempting to create a new product with name: {}", createProduct.getProductName());

		UsersDTO loggedInUser = authUtils.getLoggedInUser();
		Product savedProduct;

		// Check if the product exists
		Optional<Product> existingProduct = productRepository
				.findByProductNameAndDeletedFalse(createProduct.getProductName());
		if (existingProduct.isPresent()) {
			String errorMessage = ErrorMessages.PRODUCT_NAME_EXISTS;
			LOGGER.error("Product creation failed: {}", errorMessage);
			throw new IllegalArgumentException(errorMessage);
		}

		// Check for soft-deleted category and reactivate it
		Optional<Product> softDeletedProduct = productRepository
				.findByProductNameAndDeletedTrue(createProduct.getProductName());
		if (softDeletedProduct.isPresent()) {
			Product reactivatedProduct = softDeletedProduct.get();

			reactivatedProduct.setSku(createProduct.getSku());
			reactivatedProduct.setDescription(createProduct.getDescription());
			reactivatedProduct.setCategoryId(createProduct.getCategoryId());
			reactivatedProduct.setDistributorId(createProduct.getDistributorId());
			reactivatedProduct.setPrice(createProduct.getPrice());
			reactivatedProduct.setCostPrice(createProduct.getCostPrice());
			reactivatedProduct.setMinPrice(createProduct.getMinPrice());
			reactivatedProduct.setManufactureDate(convertStringToLocalDateTime(createProduct.getManufactureDate()));
			reactivatedProduct.setExpireDate(convertStringToLocalDateTime(createProduct.getExpireDate()));
			reactivatedProduct.setEnabled(createProduct.isEnabled());
			reactivatedProduct.setUpdatedUserId(loggedInUser.getUserId());
			reactivatedProduct.setDeleted(false);
			reactivatedProduct.setDeletedAt(null);
			reactivatedProduct.setDeletedUserId(null);

			savedProduct = productRepository.save(reactivatedProduct);
			LOGGER.info("Soft-deleted product '{}' reactivated successfully.", savedProduct.getProductName());

		} else {
			// Otherwise, create a new category
			Product newProduct = new Product();

			newProduct.setSku(createProduct.getSku());
			newProduct.setProductName(createProduct.getProductName());
			newProduct.setDescription(createProduct.getDescription());
			newProduct.setCategoryId(createProduct.getCategoryId());
			newProduct.setDistributorId(createProduct.getDistributorId());
			newProduct.setPrice(createProduct.getPrice());
			newProduct.setCostPrice(createProduct.getCostPrice());
			newProduct.setMinPrice(createProduct.getMinPrice());

			newProduct.setManufactureDate(convertStringToLocalDateTime(createProduct.getManufactureDate()));
			newProduct.setExpireDate(convertStringToLocalDateTime(createProduct.getExpireDate()));
			newProduct.setEnabled(createProduct.isEnabled());
			newProduct.setUpdatedUserId(loggedInUser.getUserId());
			newProduct.setCreatedUserId(loggedInUser.getUserId());
			newProduct.setDeleted(false);

			savedProduct = productRepository.save(newProduct);
			LOGGER.info("New product '{}' created successfully with ID: {}", savedProduct.getProductName(),
					savedProduct.getProductId());
		}

		// Check for inventory record
		Optional<Inventory> previousInventory = inventoryRepository.findByProductId(savedProduct.getId());
		if (previousInventory.isPresent()) {
			// Update inventory
			Inventory reActivateInventory = previousInventory.get();
			reActivateInventory.setProductId(savedProduct.getId());
			reActivateInventory.setQuantity(createProduct.getInitialStock());
			reActivateInventory.setStockAlertLevel(createProduct.getStockAlertLevel());
			reActivateInventory.setStockWarningLevel(createProduct.getStockWarningLevel());

			Inventory savedInventory = inventoryRepository.save(reActivateInventory);
			LOGGER.info("Inventory for product '{}' reactivated successfully. Inventory ID {}",
					savedProduct.getProductName(), savedInventory.getInventoryId());
		} else {
			// Create inventory
			Inventory inventory = new Inventory();
			inventory.setProductId(savedProduct.getId());
			inventory.setQuantity(createProduct.getInitialStock());
			inventory.setStockAlertLevel(createProduct.getStockAlertLevel());
			inventory.setStockWarningLevel(createProduct.getStockWarningLevel());

			Inventory savedInventory = inventoryRepository.save(inventory);
			LOGGER.info("New inventory created for product '{}', inventory ID: {}", savedProduct.getProductName(),
					savedInventory.getInventoryId());
		}
		return convertToDTO(savedProduct);
	}

	/** Update product */
	@Transactional
	public ProductDTO updateProduct(Integer id, CreateProductDTO updatedProduct) {

		LOGGER.debug("Attempting to update product with ID: {}", id);

		UsersDTO loggedInUser = authUtils.getLoggedInUser();

		return productRepository.findById(id).map(product -> {
			product.setProductName(updatedProduct.getProductName());
			product.setDescription(updatedProduct.getDescription());
			product.setSku(updatedProduct.getSku());
			product.setCategoryId(updatedProduct.getCategoryId());
			product.setDistributorId(updatedProduct.getDistributorId());
			product.setPrice(updatedProduct.getPrice());
			product.setCostPrice(updatedProduct.getCostPrice());
			product.setMinPrice(updatedProduct.getMinPrice());
			product.setManufactureDate(convertStringToLocalDateTime(updatedProduct.getManufactureDate()));
			product.setExpireDate(convertStringToLocalDateTime(updatedProduct.getExpireDate()));
			product.setEnabled(updatedProduct.isEnabled());
			product.setUpdatedUserId(loggedInUser.getUserId());

			Product savedProduct = productRepository.save(product);
			LOGGER.info("Product with ID: {} successfully updated. New name: '{}'", id, savedProduct.getProductName());

			return convertToDTO(savedProduct);
		}).orElseThrow(() -> {
			String errorMessage = ErrorMessages.PRODUCT_NOT_FOUND + id;
			LOGGER.error("Product update failed: {}", errorMessage);
			return new RuntimeException(errorMessage);
		});
	}

	/** Delete a product by ID (soft delete) */
	@Transactional
	public void softDeleteProduct(Integer id) {

		LOGGER.debug("Attempting to soft delete product with ID: {}", id);

		UsersDTO loggedInUser = authUtils.getLoggedInUser();

		productRepository.findById(id).ifPresentOrElse(product -> {
			product.setUpdatedUserId(loggedInUser.getUserId());
			product.setDeleted(true);
			product.setDeletedAt(LocalDateTime.now());
			product.setDeletedUserId(loggedInUser.getUserId());
			productRepository.save(product);

			LOGGER.info("Product with ID: {} has been soft deleted.", id);
		}, () -> {
			String errorMessage = ErrorMessages.PRODUCT_NOT_FOUND + id;
			LOGGER.error("Soft delete failed: {}", errorMessage);
			throw new NotFoundException(errorMessage);
		});
	}

	/** Delete a role by ID */
	@Transactional
	public void deleteProduct(Integer id) {

		LOGGER.debug("Attempting to delete product with ID: {}", id);

		Product product = productRepository.findById(id).orElseThrow(() -> {
			String errorMessage = ErrorMessages.PRODUCT_NOT_FOUND + id;
			LOGGER.error("Product deletion failed: {}", errorMessage);
            return new NotFoundException(errorMessage);
		});

		productRepository.delete(product);
		LOGGER.info("Product with ID: {} has been permanently deleted.", id);
	}

	private ProductDTO convertToDTO(Product product) {

		ProductDTO productDTO = modelMapper.map(product, ProductDTO.class);

		// Set CategoryDTO
		Category category = categoryRepository.findById(product.getCategoryId()).orElseThrow();
		CategoryDTO categoryDTO = modelMapper.map(category, CategoryDTO.class);
		productDTO.setCategory(categoryDTO);

		// Set DistributorDTO
		Distributor distributor = distributorRepository.findById(product.getDistributorId()).orElseThrow();
		DistributorDTO distributorDTO = modelMapper.map(distributor, DistributorDTO.class);
		productDTO.setDistributor(distributorDTO);

		// Set InventoryDTO
		Inventory inventory = inventoryRepository.findByProductId(product.getId()).orElseThrow();
		InventoryDTO inventoryDTO = modelMapper.map(inventory, InventoryDTO.class);
		productDTO.setInventory(inventoryDTO);

		// Set CreatedUserDTO
		UsersDTO createdUser = authUtils.getUserById(product.getCreatedUserId());
		CreatedUpdatedUserDTO createdUserDto = modelMapper.map(createdUser, CreatedUpdatedUserDTO.class);
		productDTO.setCreatedUser(createdUserDto);

		// Set UpdatedUserDTO
		UsersDTO updatedUser = authUtils.getUserById(product.getUpdatedUserId());
		CreatedUpdatedUserDTO updatedUserDto = modelMapper.map(updatedUser, CreatedUpdatedUserDTO.class);
		productDTO.setUpdatedUser(updatedUserDto);

		// Set DeletedUserDTO
		if (category.isDeleted()) {
			UsersDTO deletedUser = authUtils.getUserById(product.getDeletedUserId());
			CreatedUpdatedUserDTO deletedUserDto = modelMapper.map(deletedUser, CreatedUpdatedUserDTO.class);
			productDTO.setDeletedUser(deletedUserDto);
		}

		return productDTO;
	}

}
