package lk.udcreations.sale.service;

import static lk.udcreations.sale.util.calculate.CalculateUtil.getSum;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import lk.udcreations.sale.constants.ErrorMessages;
import lk.udcreations.common.dto.product.ProductDTO;
import lk.udcreations.common.dto.salesitems.CreateSalesItemDTO;
import lk.udcreations.common.dto.salesitems.SalesItemDTO;
import lk.udcreations.sale.exception.NotFoundException;
import lk.udcreations.sale.exception.DiscountMismatchException;
import lk.udcreations.sale.exception.InsufficientStockException;
import lk.udcreations.sale.exception.ProductNotActiveException;
import lk.udcreations.sale.exception.TotalMismatchException;
import lk.udcreations.sale.exception.UnitPriceMismatchException;
import lk.udcreations.sale.controller.ProductClientController;
import lk.udcreations.sale.entity.Sales;
import lk.udcreations.sale.entity.SalesItems;
import lk.udcreations.sale.repository.SalesItemsRepository;
import lk.udcreations.sale.repository.SalesRepository;
import lk.udcreations.sale.util.relationcheck.InventoryCheck;
import lk.udcreations.sale.util.relationcheck.ProductCheck;

@Service
public class SalesItemsService {

	private static final Logger LOGGER = LoggerFactory.getLogger(SalesItemsService.class);

	private final SalesItemsRepository salesItemsRepository;
	private final SalesRepository salesRepository;
	private final ModelMapper modelMapper;
	private final ProductClientController productClientController;

	private final ProductCheck productCheck;
	private final InventoryCheck inventoryCheck;


	public SalesItemsService(SalesItemsRepository salesItemsRepository, SalesRepository salesRepository,
			ModelMapper modelMapper, ProductClientController productClientController, ProductCheck productCheck,
			InventoryCheck inventoryCheck) {
		super();
		this.salesItemsRepository = salesItemsRepository;
		this.salesRepository = salesRepository;
		this.modelMapper = modelMapper;
		this.productClientController = productClientController;
		this.productCheck = productCheck;
		this.inventoryCheck = inventoryCheck;
	}

	public SalesItemDTO createSalesItem(CreateSalesItemDTO createSalesItem) {
		
		SalesItems newItem = new SalesItems();
		SalesItems savedItem = null;
		
		ProductDTO product = productClientController.getProductById(createSalesItem.getProductId());
		Sales sale = salesRepository.findById(createSalesItem.getSaleId()).orElseThrow();

		// Check if product is enabled
		if (!productCheck.isProductEnabled(product) || productCheck.isProductDeleted(product)) {
			throw new ProductNotActiveException("This product cannot sell.");
		}
		
		// Check stock
		if (!inventoryCheck.checkStockAvailability(createSalesItem.getProductId(), createSalesItem.getQuantity())) {
			throw new InsufficientStockException("Stock is not enoug to sell.");
		}

		// Check unit price
		if (!productCheck.isUnitPriceMatch(product, createSalesItem)) {
			throw new UnitPriceMismatchException("There is a problem with selling price.");
		}

		// Check discount
		if (!productCheck.isDiscountMatch(product, createSalesItem)) {
			throw new DiscountMismatchException("There is a problem with discount.");
		}

		// Check total price
		if (!productCheck.isTotalMatch(product, createSalesItem)) {
			throw new TotalMismatchException("There is a problem with total price.");
		}

		// Check if item is already added to this sale
		Optional<SalesItems> oldItems = salesItemsRepository
				.findByProductIdAndSaleId(createSalesItem.getProductId(), createSalesItem.getSaleId());
		
		if (oldItems.isPresent()) {
			SalesItems oldItem = oldItems.get();
			newItem.setSalesItemId(oldItem.getSalesItemId());
			newItem.setQuantity(getSum(createSalesItem.getQuantity(), oldItem.getQuantity()));
			newItem.setTotalPrice(getSum(createSalesItem.getTotalPrice(), oldItem.getTotalPrice()));
		} else {
			newItem.setQuantity(createSalesItem.getQuantity());
			newItem.setTotalPrice(createSalesItem.getTotalPrice());
		}
		newItem.setSaleId(sale.getSaleId());
		newItem.setProductId(product.getId());
		newItem.setPricePerUnit(createSalesItem.getPricePerUnit());
		newItem.setItemDiscountVal(createSalesItem.getItemDiscountVal());
		newItem.setItemDiscountPer(createSalesItem.getItemDiscountPer());

		savedItem = salesItemsRepository.save(newItem);

		return convertToDTO(savedItem);
	}

	public SalesItemDTO updateSalesItem(Integer salesItemId, CreateSalesItemDTO updateItem) {

		LOGGER.debug("Attempting to update sale item with ID: {}", salesItemId);

		return salesItemsRepository.findById(salesItemId).map(item -> {
			item.setQuantity(updateItem.getQuantity());
			item.setPricePerUnit(updateItem.getPricePerUnit());
			item.setItemDiscountVal(updateItem.getItemDiscountVal());
			item.setItemDiscountPer(updateItem.getItemDiscountPer());
			item.setTotalPrice(updateItem.getTotalPrice());

			SalesItems savedItem = salesItemsRepository.save(item);
			LOGGER.info("Sales item with ID: {} successfully updated. Sale ID: '{}'", salesItemId,
					savedItem.getSaleId());
			return convertToDTO(savedItem);

		}).orElseThrow(() -> {
			String errorMessage = ErrorMessages.SALESITEM_NOT_FOUND + salesItemId;
			LOGGER.error("Sale item update failed: {}", errorMessage);
			return new NotFoundException(errorMessage);
		});
	}

	public List<SalesItemDTO> getAllSaleItemsBySaleId(Integer saleId) {

		LOGGER.debug("Fetching all sale items of sale ID: {}.", saleId);

		List<SalesItems> saleItems = salesItemsRepository.findBySaleId(saleId);
		if (saleItems.isEmpty()) {
			LOGGER.warn("No sales items found with sale ID: {}.", saleId);
		} else {
			LOGGER.info("Fetched {} sales items from the database.", saleItems.size());
		}
		return saleItems.stream().map(this::convertToDTO).collect(Collectors.toList());
	}

	/** Delete a sale item */
	public void deleteSaleItem(Integer salesItemId) {

		LOGGER.info("Deleting sale item with ID {}", salesItemId);
		SalesItems salesItem = salesItemsRepository.findById(salesItemId).orElseThrow(() -> {
			String errorMessage = ErrorMessages.SALESITEM_NOT_FOUND + salesItemId;
			LOGGER.error("Sale item delete failed: {}", errorMessage);
			return new NotFoundException(errorMessage);
		});
		salesItemsRepository.delete(salesItem);
		LOGGER.info("Sale Item with ID {} deleted successfully", salesItemId);
	}

	private SalesItemDTO convertToDTO(SalesItems salesItem) {
		
		SalesItemDTO dto = modelMapper.map(salesItem, SalesItemDTO.class);
		
		ProductDTO productDTO = productClientController.getProductById(salesItem.getProductId());
		dto.setProduct(productDTO);
		
		return dto;
	}
}
