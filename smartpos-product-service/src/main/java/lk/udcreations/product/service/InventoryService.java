package lk.udcreations.product.service;

import java.util.List;
import java.util.stream.Collectors;

import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import lk.udcreations.product.constants.ErrorMessages;
import lk.udcreations.common.dto.inventory.InventoryDTO;
import lk.udcreations.common.dto.product.ProductDTO;
import lk.udcreations.product.exception.NotFoundException;
import lk.udcreations.product.entity.Inventory;
import lk.udcreations.product.entity.Product;
import lk.udcreations.product.exception.InsufficientStockException;
import lk.udcreations.product.repository.InventoryRepository;
import lk.udcreations.product.repository.ProductRepository;

@Service
public class InventoryService {

	private static final Logger LOGGER = LoggerFactory.getLogger(InventoryService.class);

	private final InventoryRepository inventoryRepository;
	private final ProductRepository productRepository;
	private final ModelMapper modelMapper;

	public InventoryService(InventoryRepository inventoryRepository, ProductRepository productRepository,
			ModelMapper modelMapper) {
		super();
		this.inventoryRepository = inventoryRepository;
		this.productRepository = productRepository;
		this.modelMapper = modelMapper;
	}

	/** Add stock */
	@Transactional
	public InventoryDTO addStock(Integer productId, int quantity) {
		LOGGER.info("Adding {} units to product ID: {}", quantity, productId);

		return inventoryRepository.findByProductId(productId).map(inventory -> {
			inventory.setQuantity(inventory.getQuantity() + quantity);
			Inventory updatedInventory = inventoryRepository.save(inventory);
			LOGGER.info("Stock updated. New quantity for product ID {}: {}", productId, updatedInventory.getQuantity());
			return convertToDTO(updatedInventory);
		}).orElseThrow(() -> {
			String errorMsg = ErrorMessages.INVENTORY_NOT_FOUND + productId;
			LOGGER.error(errorMsg);
			return new NotFoundException(errorMsg);
		});
	}

	/** Decrease stock after a sale or return */
	@Transactional
	public InventoryDTO decreaseStock(Integer productId, int quantity) {
		LOGGER.info("Decreasing {} units from product ID: {}", quantity, productId);

		return inventoryRepository.findByProductId(productId).map(inventory -> {
			if (inventory.getQuantity() < quantity) {
				LOGGER.error(ErrorMessages.INVENTORY_NOT_ENOUGHT_STOCK);
				throw new InsufficientStockException(ErrorMessages.INVENTORY_NOT_ENOUGHT_STOCK);
			}
			inventory.setQuantity(inventory.getQuantity() - quantity);
			Inventory updatedInventory = inventoryRepository.save(inventory);
			LOGGER.info("Stock decreased. New quantity for product ID {}: {}", productId,
					updatedInventory.getQuantity());
			return convertToDTO(updatedInventory);
		}).orElseThrow(() -> {
			String errorMsg = ErrorMessages.INVENTORY_NOT_FOUND + productId;
			LOGGER.error(errorMsg);
			return new NotFoundException(errorMsg);
		});
	}

	/** Get the current stock level */
	public int getStock(Integer productId) {
		LOGGER.info("Fetching stock level for product ID: {}", productId);

		Inventory inventory = inventoryRepository.findByProductId(productId)
				.orElseThrow(() -> {
					String errorMsg = ErrorMessages.INVENTORY_NOT_FOUND + productId;
					LOGGER.error(errorMsg);
					return new NotFoundException(errorMsg);
				});

		LOGGER.info("Current stock for product ID {}: {}", productId, inventory.getQuantity());
		return inventory.getQuantity();
	}

	/** Get product stock details */
	public InventoryDTO getProductStockDetails(Integer productId) {
		Inventory inventory = inventoryRepository.findByProductId(productId)
				.orElseThrow(() -> {
					String errorMsg = ErrorMessages.INVENTORY_NOT_FOUND + productId;
					LOGGER.error(errorMsg);
					return new NotFoundException(errorMsg);
				});
		return convertToDTO(inventory);
	}

	/** Update stock level */
	@Transactional
	public InventoryDTO updateStockLevel(Integer productId, Inventory updatedInventory) {
		LOGGER.info("Updating stock levels for product ID: {}", productId);

		return inventoryRepository.findByProductId(productId).map(inventory -> {
			inventory.setStockAlertLevel(updatedInventory.getStockAlertLevel());
			inventory.setStockWarningLevel(updatedInventory.getStockWarningLevel());
			Inventory updated = inventoryRepository.save(inventory);
			LOGGER.info("Stock levels updated for product ID: {}", productId);
			return convertToDTO(updated);
		}).orElseThrow(() -> {
			String errorMsg = ErrorMessages.INVENTORY_NOT_FOUND + productId;
			LOGGER.error(errorMsg);
			return new NotFoundException(errorMsg);
		});
	}

	/** List all inventory items */
	public List<InventoryDTO> getAllInventoryItems() {
		LOGGER.info("Fetching all inventory items");

		List<Inventory> inventories = inventoryRepository.findAll();
		if (inventories.isEmpty()) {
			LOGGER.warn("No inventories found in the database.");
		} else {
			LOGGER.info("Successfully fetched {} inventories from the database.", inventories.size());
		}
		return inventories.stream().map(this::convertToDTO).collect(Collectors.toList());
	}

	/** Check low stock */
	public List<InventoryDTO> checkStockAlert() {
		LOGGER.info("Checking for stock alert levels");

		List<Inventory> inventories = inventoryRepository.findInventoryBelowStockAlertLevel();
		if (inventories.isEmpty()) {
			LOGGER.warn("No stock alert inventories found in the database.");
		} else {
			LOGGER.info("Successfully fetched {} stock alert inventories from the database.", inventories.size());
		}
		return inventories.stream().map(this::convertToDTO).collect(Collectors.toList());
	}

	/** Check low stock */
	public List<InventoryDTO> checkStockWarning() {
		LOGGER.info("Checking for stock warning levels");

		List<Inventory> inventories = inventoryRepository.findInventoryBelowStockWarningLevel();
		if (inventories.isEmpty()) {
			LOGGER.warn("No stock warning inventories found in the database.");
		} else {
			LOGGER.info("Successfully fetched {} stock warning inventories from the database.", inventories.size());
		}
		return inventories.stream().map(this::convertToDTO).collect(Collectors.toList());
	}

	/** Check if there are enough stocks */
	public boolean checkStockAvailability(Integer productId, int quantity) {
		LOGGER.info("Checking stock availability for product ID: {}", productId);

		Inventory inventory = inventoryRepository.findByProductId(productId)
				.orElseThrow(() -> new NotFoundException(ErrorMessages.INVENTORY_NOT_FOUND + productId));

		boolean isAvailable = inventory.getQuantity() >= quantity;
		LOGGER.info("Stock availability for product ID {}: {}", productId, isAvailable ? "Available" : "Not Available");
		return isAvailable;
	}

	private InventoryDTO convertToDTO(Inventory inventory) {

		InventoryDTO inventoryDto = modelMapper.map(inventory, InventoryDTO.class);

		// Set ProdcutDTO
		Product product = productRepository.findById(inventory.getProductId()).orElseThrow();
		ProductDTO productDto = modelMapper.map(product, ProductDTO.class);
		inventoryDto.setProduct(productDto);

		return inventoryDto;
	}

}
