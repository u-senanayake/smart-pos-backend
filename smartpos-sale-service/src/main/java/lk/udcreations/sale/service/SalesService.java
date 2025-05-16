package lk.udcreations.sale.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import lk.udcreations.common.dto.customer.CustomerDTO;
import lk.udcreations.common.dto.payment.PaymentDTO;
import lk.udcreations.common.dto.sale.CreateSaleDTO;
import lk.udcreations.common.dto.sale.FinalizeSaleDTO;
import lk.udcreations.common.dto.sale.SaleDTO;
import lk.udcreations.common.dto.sale.UpdateSaleDTO;
import lk.udcreations.common.dto.salesitems.SalesItemDTO;
import lk.udcreations.common.dto.user.CreatedUpdatedUserDTO;
import lk.udcreations.common.dto.user.UsersDTO;
import lk.udcreations.sale.constants.ErrorMessages;
import lk.udcreations.sale.controller.CustomerClientController;
import lk.udcreations.sale.entity.Payment;
import lk.udcreations.sale.entity.Sales;
import lk.udcreations.sale.entity.SalesItems;
import lk.udcreations.sale.exception.NotFoundException;
import lk.udcreations.sale.exception.PaymentAmountException;
import lk.udcreations.sale.exception.TotalAmountException;
import lk.udcreations.sale.exception.TotalQuantityException;
import lk.udcreations.sale.repository.PaymentRepository;
import lk.udcreations.sale.repository.SalesItemsRepository;
import lk.udcreations.sale.repository.SalesRepository;
import lk.udcreations.sale.security.AuthUtils;
import lk.udcreations.sale.util.relationcheck.SalesCheck;

@Service
public class SalesService {

	private static final Logger LOGGER = LoggerFactory.getLogger(SalesService.class);

	private final SalesRepository salesRepository;
	private final PaymentRepository paymentRepository;
	private final SalesItemsRepository salesItemsRepository;
	private final AuthUtils authUtils;
	private final CustomerClientController customerClientController;
	private final SalesCheck salesCheck;
	private final ModelMapper modelMapper;

	public SalesService(SalesRepository salesRepository, PaymentRepository paymentRepository,
			SalesItemsRepository salesItemsRepository, AuthUtils authUtils,
			CustomerClientController customerClientController, SalesCheck salesCheck, ModelMapper modelMapper) {
		super();
		this.salesRepository = salesRepository;
		this.paymentRepository = paymentRepository;
		this.salesItemsRepository = salesItemsRepository;
		this.authUtils = authUtils;
		this.customerClientController = customerClientController;
		this.salesCheck = salesCheck;
		this.modelMapper = modelMapper;
	}

	/**
	 * Create a new sale
	 * 
	 * 1. When open POS page
	 * 
	 * 2. When press New Sale Button
	 */
	@Transactional
	public SaleDTO createSale(CreateSaleDTO createSale) {

		UsersDTO loggedInUser = authUtils.getLoggedInUser();

		LOGGER.info("Creating sale for user ID {} and customer ID {}", loggedInUser.getUserId(),
				createSale.getCustomerId());

		Sales newSale = new Sales();
		newSale.setUserId(loggedInUser.getUserId());
		newSale.setCustomerId(createSale.getCustomerId());
		newSale.setTotalAmount(BigDecimal.ZERO);
		newSale.setTotalItemCount(0);
		newSale.setSaleDateTime(LocalDateTime.now());
		newSale.setPaymentStatus("DRAFT");

		Sales savedSale = salesRepository.save(newSale);

		LOGGER.info("Sale created successfully with ID {}", savedSale.getSaleId());

		return convertToDTO(savedSale);
	}

	/**
	 * Update an existing sale
	 * 
	 * 1. When change customer
	 * 
	 * 2. When item added
	 * 
	 * 3. When item deleted
	 */
	@Transactional
	public SaleDTO updateSale(Integer saleId, UpdateSaleDTO updatedSale) {

		LOGGER.info("Updating sale with ID {}", saleId);

		return salesRepository.findById(saleId).map(sale -> {
			sale.setTotalAmount(updatedSale.getTotalAmount());
			sale.setTotalItemCount(updatedSale.getTotalItemCount());
			sale.setPaymentStatus(updatedSale.getPaymentStatus());
			sale.setCustomerId(updatedSale.getCustomerId());

			Sales savedSale = salesRepository.save(sale);
			LOGGER.info(
					"Sale with ID: {} successfully updated. Total amount: {}, Total item: {}, Payment status: {}, Customer ID: {}",
					saleId, savedSale.getTotalAmount(), savedSale.getTotalItemCount(), savedSale.getPaymentStatus(),
					savedSale.getCustomerId());

			return convertToDTO(savedSale);
		}).orElseThrow(() -> {
			String errMsg = ErrorMessages.SALE_NOT_FOUND + saleId;
			LOGGER.error("Sale fetch failed: {} ", errMsg);
			return new NotFoundException(errMsg);
		});
	}

	/** Get all sales */
	public List<SaleDTO> getAllSales() {

		LOGGER.info("Fetching all sales");

		List<Sales> sales = salesRepository.findAll();
		if (sales.isEmpty()) {
			LOGGER.warn("No sales found in the system.");
		} else {
			LOGGER.info("Fetched {} sales from the database.", sales.size());
		}

		return sales.stream().map(this::convertToDTO).collect(Collectors.toList());
	}

	/** Get all sales by status */
	public List<SaleDTO> getSalesByPaymentStatus(String status) {
		LOGGER.info("Fetching all finalized sales");

		List<Sales> sales = salesRepository.findByPaymentStatus(status);
		if (sales.isEmpty()) {
			LOGGER.warn("No finalized sales found in the system.");
		} else {
			LOGGER.info("Fetched {} finalized sales from the database.", sales.size());
		}

		return sales.stream().map(this::convertToDTO).collect(Collectors.toList());
	}
	
	/** Get all sales by status */
	public List<SaleDTO> getDraftSales() {
		LOGGER.info("Fetching all draft sales");

		List<Sales> sales = salesRepository.findByPaymentStatus("DRAFT");
		if (sales.isEmpty()) {
			LOGGER.warn("No draft sales found in the system.");
		} else {
			LOGGER.info("Fetched {} draft sales from the database.", sales.size());
		}

		return sales.stream().map(this::convertToDTO).collect(Collectors.toList());
	}
	
	public List<SaleDTO> getSalesHistory() {
		LOGGER.info("Fetching all sale history");

		List<Sales> sales = salesRepository.findByPaymentStatusNot("DRAFT");
		if (sales.isEmpty()) {
			LOGGER.warn("No sales history found in the system.");
		} else {
			LOGGER.info("Fetched {} sales history from the database.", sales.size());
		}

		return sales.stream().map(this::convertToDTO).collect(Collectors.toList());
	}

	/** Get sales by customer ID */
	public List<SaleDTO> getSalesByCustomerId(Integer customerId) {

		LOGGER.info("Fetching all sales of customer ID {}", customerId);

		List<Sales> sales = salesRepository.findByCustomerId(customerId);
		if (sales.isEmpty()) {
			LOGGER.warn("No sales found for customer: {}.", customerId);
		} else {
			LOGGER.info("Fetched {} sales for the customer ID: {}.", sales.size(), customerId);
		}

		return sales.stream().map(this::convertToDTO).collect(Collectors.toList());
	}

	/** Get a sale by ID */
	public SaleDTO getSaleById(Integer saleId) {

		return convertToDTO(findSaleById(saleId));
	}

	/** finalize sale */
	@Transactional
	public SaleDTO finalizeSale(Integer saleId, FinalizeSaleDTO finalizeSaleDTO) {

		LOGGER.info("Finalizing sale with ID {}", saleId);

		// Retrieve the sale
		Sales sale = findSaleById(saleId);

		List<SalesItems> salesItems = salesItemsRepository.findBySaleId(saleId);

		// Check if sales items exist
		if (salesItems == null || salesItems.isEmpty()) {
			String errMsg = "Cannot finalize sale with ID: " + saleId + ". No sales items found";
			LOGGER.error(errMsg);
			throw new NotFoundException(errMsg);
		}
		
		// Verify total amount
		if (!salesCheck.verifiTotalAmount(finalizeSaleDTO.getTotalAmount(), salesItems)) {
			String errMsg = ErrorMessages.TOTAL_AMOUNT_NOT_MATCH;
			LOGGER.error(errMsg);
			throw new TotalAmountException(errMsg);
		}
		// Verify item count
		if (!salesCheck.verifiTotalQuantity(finalizeSaleDTO.getTotalItemCount(), salesItems)) {
			String errMsg = ErrorMessages.ITEM_COUNT_NOT_MATCH;
			LOGGER.error(errMsg);
			throw new TotalQuantityException(errMsg);
		}

		// verify payment amount sum
		if (!salesCheck.verifyPayment(finalizeSaleDTO.getTotalAmount(), finalizeSaleDTO.getPayment())) {
			String errMsg = ErrorMessages.PAYMENT_AMOUNT_NOT_MATCH;
			LOGGER.error(errMsg);
			throw new PaymentAmountException(errMsg);
		}

		// verify discount

		// Save payments
		PaymentDTO payment = finalizeSaleDTO.getPayment();
		Payment payment2 = convertToEntity(payment);
		payment2.setSaleId(sale.getSaleId());
		createPayment(payment2);

		sale.setPaymentStatus("FINALIZED");
		sale.setSaleDateTime(LocalDateTime.now());
		sale.setUpdatedAt(LocalDateTime.now());

		Sales savedSale = salesRepository.save(sale);
		return convertToDTO(savedSale);
	}

	@Transactional
	private void createPayment(Payment payment) {

		LOGGER.debug("Attempting to create a new payment for sale ID: {}", payment.getSaleId());

		// Check payment already saved, update if
		Optional<Payment> existingPayment = paymentRepository.findBySaleId(payment.getSaleId());

		if (existingPayment.isPresent()) {
			Payment updatePayment = existingPayment.get();
			updatePayment.setCashAmount(payment.getCashAmount());
			updatePayment.setCreditCardAmount(payment.getCreditCardAmount());
			updatePayment.setCreditCardReference(payment.getCreditCardReference());
			updatePayment.setQrAmount(payment.getQrAmount());
			updatePayment.setQrReference(payment.getQrReference());
			updatePayment.setChequeAmount(payment.getChequeAmount());
			updatePayment.setChequeReference(payment.getChequeReference());
			updatePayment.setDueAmount(payment.getDueAmount());

			paymentRepository.save(updatePayment);
		} else {
			paymentRepository.save(payment);
		}
	}

	/** Delete a sale */
	@Transactional
	public void deleteSale(Integer saleId) {
		LOGGER.info("Deleting sale with ID {}", saleId);
		Sales sale = salesRepository.findBySaleId(saleId)
				.orElseThrow(() -> {
					String errMsg = "Sale not found with ID: " + saleId;
					LOGGER.error("Sale fetch failed: {}.", errMsg);
					return new RuntimeException(errMsg);
				});
		salesRepository.delete(sale);
		LOGGER.info("Sale with ID {} deleted successfully", saleId);
	}

	private SaleDTO convertToDTO(Sales sale) {

		SaleDTO dto = modelMapper.map(sale, SaleDTO.class);

		// Set CreatedUserDTO
		UsersDTO createdUser = authUtils.getUserById(sale.getUserId());
		CreatedUpdatedUserDTO createdUserDto = modelMapper.map(createdUser, CreatedUpdatedUserDTO.class);
		dto.setCreatedUser(createdUserDto);

		// Set CustomerDTO
		CustomerDTO customerDTO = customerClientController.getCustomerById(sale.getCustomerId());
		dto.setCustomer(customerDTO);

		// Set PaymentDTO
		Payment payment = paymentRepository.findBySaleId(sale.getSaleId()).orElse(new Payment());
		dto.setPayment(convertToDTO(payment));

		// Set List<SalesItemsDTO>
		List<SalesItems> salesItems = salesItemsRepository.findBySaleId(sale.getSaleId());
		dto.setSalesItems(salesItems.stream().map(this::convertToDTO).collect(Collectors.toList()));

		return dto;
	}

	private SalesItemDTO convertToDTO(SalesItems item) {
		return modelMapper.map(item, SalesItemDTO.class);
	}

	private PaymentDTO convertToDTO(Payment payment) {
		return modelMapper.map(payment, PaymentDTO.class);
	}

	private Payment convertToEntity(PaymentDTO paymentDto) {

		Payment payment = new Payment();
		payment.setCashAmount(paymentDto.getCashAmount());
		payment.setChequeAmount(paymentDto.getChequeAmount());
		payment.setChequeReference(paymentDto.getChequeRef());
		payment.setCreditCardAmount(paymentDto.getcCardAmount());
		payment.setCreditCardReference(paymentDto.getcCardRef());
		payment.setDueAmount(paymentDto.getDueAmount());
		payment.setQrAmount(paymentDto.getQrAmount());
		payment.setQrReference(paymentDto.getQrRef());

		return payment;
	}

	private Sales findSaleById(Integer saleId) {

		LOGGER.debug("Fetching sale with ID: {}", saleId);

		Sales sale = salesRepository.findById(saleId).orElseThrow(() -> {
			String errorMessage = ErrorMessages.SALE_NOT_FOUND + saleId;
			LOGGER.error("Sale fetch failed. {}", errorMessage);
			return new NotFoundException(errorMessage);
		});

		LOGGER.info("Successfully fetched sale with ID: {} and customer ID: '{}'", sale.getSaleId(),
				sale.getCustomerId());
		return sale;
	}
}
