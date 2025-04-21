package lk.udcreations.sale.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lk.udcreations.common.dto.inventory.StockDTO;
import lk.udcreations.common.dto.returns.CreateReturnDTO;
import lk.udcreations.common.dto.returns.ReturnDTO;
import lk.udcreations.common.dto.salesitems.SalesItemDTO;
import lk.udcreations.sale.exception.NotFoundException;
import lk.udcreations.sale.controller.ProductClientController;
import lk.udcreations.sale.entity.Returns;
import lk.udcreations.sale.entity.Sales;
import lk.udcreations.sale.entity.SalesItems;
import lk.udcreations.sale.repository.ReturnsRepository;
import lk.udcreations.sale.repository.SalesItemsRepository;
import lk.udcreations.sale.repository.SalesRepository;

@Service
public class ReturnsService {

	private static final Logger LOGGER = LoggerFactory.getLogger(ReturnsService.class);

	private final ReturnsRepository returnsRepository;
	private final SalesRepository salesRepository;
	private final SalesItemsRepository salesItemsRepository;

	private final ProductClientController productClientController;
	private final ModelMapper modelMapper;

	public ReturnsService(ReturnsRepository returnsRepository, SalesRepository salesRepository,
			SalesItemsRepository salesItemsRepository, ProductClientController productClientController,
			ModelMapper modelMapper) {
		super();
		this.returnsRepository = returnsRepository;
		this.salesRepository = salesRepository;
		this.salesItemsRepository = salesItemsRepository;
		this.productClientController = productClientController;
		this.modelMapper = modelMapper;
	}

	@Transactional
	public List<ReturnDTO> processReturn(List<CreateReturnDTO> createReturnDTOList) {

		LOGGER.info("Processing return for {} products", createReturnDTOList.size());

		List<ReturnDTO> returnDTOList = new ArrayList<>();

		for (CreateReturnDTO createReturnDTO : createReturnDTOList) {
			LOGGER.info("Processing return for saleItemId: {} in saleId: {}", createReturnDTO.getSalesItemId(),
					createReturnDTO.getSaleId());

			// Validate sale and sale item
			Sales sale = validateSale(createReturnDTO.getSaleId());
			SalesItems salesItem = validateSaleItem(createReturnDTO.getSalesItemId(), createReturnDTO.getSaleId());

			// Verify sufficient quantity for return
			if (salesItem.getQuantity() < createReturnDTO.getQuantity()) {
				LOGGER.error("Insufficient quantity to return. Requested: {}, Available: {}",
						createReturnDTO.getQuantity(), salesItem.getQuantity());
				throw new IllegalArgumentException("Insufficient quantity for return.");
			}

			// Update inventory
			StockDTO stockDTO = new StockDTO(createReturnDTO.getQuantity());
			productClientController.addStock(salesItem.getProductId(), stockDTO);

			// Calculate refund amount
			BigDecimal refundAmount = calculateRefundAmount(createReturnDTO.getQuantity(), salesItem.getPricePerUnit());

			// Update sale
			sale.setTotalAmount(sale.getTotalAmount().subtract(refundAmount));
			sale.setTotalItemCount(sale.getTotalItemCount() - createReturnDTO.getQuantity());
			salesRepository.save(sale);

			// Update sales item
			salesItem.setQuantity(salesItem.getQuantity() - createReturnDTO.getQuantity());
			salesItem.setReturnedQuantity(createReturnDTO.getQuantity());
			salesItemsRepository.save(salesItem);

			// Save return record
			Returns returnRecord = new Returns();
			returnRecord.setSaleId(sale.getSaleId());
			returnRecord.setSalesItemId(salesItem.getSalesItemId());
			returnRecord.setQuantity(createReturnDTO.getQuantity());
			returnRecord.setReason(createReturnDTO.getReason());
			returnRecord.setRefundAmount(refundAmount);
			returnRecord.setReturnDate(LocalDateTime.now());
			returnsRepository.save(returnRecord);

			// Add to DTO list
			returnDTOList.add(convertToDTO(returnRecord));


		}
		LOGGER.info("Successfully processed return for {} products", createReturnDTOList.size());

		return returnDTOList;
	}

	public List<ReturnDTO> getReturnsBySaleId(Integer saleId) {
		return returnsRepository.findBySaleId(saleId).stream().map(this::convertToDTO).collect(Collectors.toList());
	}

	private Sales validateSale(Integer saleId) {
		return salesRepository.findById(saleId)
				.orElseThrow(() -> new NotFoundException("Sale not found with ID: " + saleId));
	}

	private SalesItems validateSaleItem(Integer saleItemId, Integer saleId) {
		return salesItemsRepository.findById(saleItemId).orElseThrow(
				() -> new NotFoundException("Sales item not found with ID: " + saleItemId + " for sale ID: " + saleId));
	}

	private BigDecimal calculateRefundAmount(int quantity, BigDecimal unitPrice) {
		// TODO check discount
		return unitPrice.multiply(BigDecimal.valueOf(quantity));
	}

	private ReturnDTO convertToDTO(Returns returnRecord) {

		ReturnDTO returnDTO = modelMapper.map(returnRecord, ReturnDTO.class);

		// Set List<SalesItemsDTO>
		List<SalesItems> salesItems = salesItemsRepository.findBySaleId(returnRecord.getSaleId());
		returnDTO.setSalesItems(salesItems.stream().map(this::convertToDTO).collect(Collectors.toList()));
		return returnDTO;
	}

	private SalesItemDTO convertToDTO(SalesItems item) {
		return modelMapper.map(item, SalesItemDTO.class);
	}
}
