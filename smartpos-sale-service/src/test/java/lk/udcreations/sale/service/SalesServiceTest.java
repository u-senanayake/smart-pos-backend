package lk.udcreations.sale.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;

import lk.udcreations.common.dto.customer.CustomerDTO;
import lk.udcreations.common.dto.payment.PaymentDTO;
import lk.udcreations.common.dto.sale.CreateSaleDTO;
import lk.udcreations.common.dto.sale.FinalizeSaleDTO;
import lk.udcreations.common.dto.sale.SaleDTO;
import lk.udcreations.common.dto.sale.UpdateSaleDTO;
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

class SalesServiceTest {

    @Mock
    private SalesRepository salesRepository;

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private SalesItemsRepository salesItemsRepository;

    @Mock
    private AuthUtils authUtils;

    @Mock
    private CustomerClientController customerClientController;

    @Mock
    private SalesCheck salesCheck;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private SalesService salesService;

    private Integer adminUserId;
    private UsersDTO mockUser;
    private Sales sale1;
    private Sales sale2;
    private Payment mockPayment;
    private SalesItems mockSalesItem;
    private CustomerDTO mockCustomer;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Mock a logged-in admin user
        adminUserId = 999;
        mockUser = new UsersDTO();
        mockUser.setUserId(adminUserId);

        // Mock Customer
        mockCustomer = new CustomerDTO();
        mockCustomer.setCustomerId(1);

        // Mock Payment
        mockPayment = new Payment();
        mockPayment.setPaymentId(1);
        mockPayment.setSaleId(1);
        mockPayment.setCashAmount(new BigDecimal("100.00"));

        // Mock SalesItem
        mockSalesItem = new SalesItems();
        mockSalesItem.setSalesItemId(1);
        mockSalesItem.setSaleId(1);
        mockSalesItem.setProductId(1);
        mockSalesItem.setQuantity(2);
        mockSalesItem.setPricePerUnit(new BigDecimal("50.00"));
        mockSalesItem.setTotalPrice(new BigDecimal("100.00"));

        // Create test sales
        sale1 = new Sales();
        sale1.setSaleId(1);
        sale1.setUserId(adminUserId);
        sale1.setCustomerId(1);
        sale1.setTotalAmount(new BigDecimal("100.00"));
        sale1.setTotalItemCount(2);
        sale1.setSaleDateTime(LocalDateTime.now());
        sale1.setPaymentStatus("DRAFT");

        sale2 = new Sales();
        sale2.setSaleId(2);
        sale2.setUserId(adminUserId);
        sale2.setCustomerId(1);
        sale2.setTotalAmount(new BigDecimal("200.00"));
        sale2.setTotalItemCount(4);
        sale2.setSaleDateTime(LocalDateTime.now());
        sale2.setPaymentStatus("FINALIZED");

        // Mock authUtils methods
        when(authUtils.getLoggedInUser()).thenReturn(mockUser);
        when(authUtils.getUserById(any(Integer.class))).thenReturn(mockUser);

        // Mock repository methods
        when(customerClientController.getCustomerById(1)).thenReturn(mockCustomer);
        when(paymentRepository.findBySaleId(1)).thenReturn(Optional.of(mockPayment));
        when(salesItemsRepository.findBySaleId(1)).thenReturn(Arrays.asList(mockSalesItem));

        // Mock modelMapper
        when(modelMapper.map(any(), any())).thenAnswer(invocation -> {
            Object source = invocation.getArgument(0);
            Class<?> targetClass = invocation.getArgument(1);

            if (source instanceof Sales && targetClass == SaleDTO.class) {
                Sales sale = (Sales) source;
                SaleDTO dto = new SaleDTO();
                dto.setSaleId(sale.getSaleId());
                dto.setTotalAmount(sale.getTotalAmount());
                dto.setTotalItemCount(sale.getTotalItemCount());
                dto.setPaymentStatus(sale.getPaymentStatus());
                dto.setSaleDateTime(sale.getSaleDateTime());
                return dto;
            } else if (source instanceof Payment && targetClass == PaymentDTO.class) {
                Payment payment = (Payment) source;
                PaymentDTO dto = new PaymentDTO();
                dto.setPaymentId(payment.getPaymentId());
                dto.setCashAmount(payment.getCashAmount());
                return dto;
            } else if (source instanceof UsersDTO && targetClass == CreatedUpdatedUserDTO.class) {
                UsersDTO user = (UsersDTO) source;
                CreatedUpdatedUserDTO dto = new CreatedUpdatedUserDTO();
                dto.setUserId(user.getUserId());
                return dto;
            }

            return null;
        });
    }

    @Test
    void testCreateSale() {
        // Arrange
        CreateSaleDTO createSaleDTO = new CreateSaleDTO();
        createSaleDTO.setCustomerId(1);

        when(salesRepository.save(any(Sales.class))).thenReturn(sale1);

        // Act
        SaleDTO result = salesService.createSale(createSaleDTO);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getSaleId());
        assertEquals("DRAFT", result.getPaymentStatus());
        verify(salesRepository, times(1)).save(any(Sales.class));
    }

    @Test
    void testUpdateSale() {
        // Arrange
        UpdateSaleDTO updateSaleDTO = new UpdateSaleDTO();
        updateSaleDTO.setCustomerId(1);
        updateSaleDTO.setTotalAmount(new BigDecimal("150.00"));
        updateSaleDTO.setTotalItemCount(3);
        updateSaleDTO.setPaymentStatus("DRAFT");

        when(salesRepository.findById(1)).thenReturn(Optional.of(sale1));
        when(salesRepository.save(any(Sales.class))).thenReturn(sale1);

        // Act
        SaleDTO result = salesService.updateSale(1, updateSaleDTO);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getSaleId());
        assertEquals(new BigDecimal("150.00"), sale1.getTotalAmount());
        assertEquals(3, sale1.getTotalItemCount());
        verify(salesRepository, times(1)).findById(1);
        verify(salesRepository, times(1)).save(sale1);
    }

    @Test
    void testUpdateSale_SaleNotFound() {
        // Arrange
        UpdateSaleDTO updateSaleDTO = new UpdateSaleDTO();
        updateSaleDTO.setCustomerId(1);

        when(salesRepository.findById(999)).thenReturn(Optional.empty());

        // Act & Assert
        Exception exception = assertThrows(NotFoundException.class, 
                () -> salesService.updateSale(999, updateSaleDTO));
        assertTrue(exception.getMessage().contains(ErrorMessages.SALE_NOT_FOUND));
        verify(salesRepository, times(1)).findById(999);
        verify(salesRepository, never()).save(any(Sales.class));
    }

    @Test
    void testGetAllSales() {
        // Arrange
        when(salesRepository.findAll()).thenReturn(Arrays.asList(sale1, sale2));

        // Act
        List<SaleDTO> result = salesService.getAllSales();

        // Assert
        assertEquals(2, result.size());
        verify(salesRepository, times(1)).findAll();
    }

    @Test
    void testGetSalesByPaymentStatus() {
        // Arrange
        when(salesRepository.findByPaymentStatus("FINALIZED")).thenReturn(Arrays.asList(sale2));

        // Act
        List<SaleDTO> result = salesService.getSalesByPaymentStatus("FINALIZED");

        // Assert
        assertEquals(1, result.size());
        assertEquals("FINALIZED", result.get(0).getPaymentStatus());
        verify(salesRepository, times(1)).findByPaymentStatus("FINALIZED");
    }

    @Test
    void testGetDraftSales() {
        // Arrange
        when(salesRepository.findByPaymentStatus("DRAFT")).thenReturn(Arrays.asList(sale1));

        // Act
        List<SaleDTO> result = salesService.getDraftSales();

        // Assert
        assertEquals(1, result.size());
        assertEquals("DRAFT", result.get(0).getPaymentStatus());
        verify(salesRepository, times(1)).findByPaymentStatus("DRAFT");
    }

    @Test
    void testGetSalesHistory() {
        // Arrange
        when(salesRepository.findByPaymentStatusNot("DRAFT")).thenReturn(Arrays.asList(sale2));

        // Act
        List<SaleDTO> result = salesService.getSalesHistory();

        // Assert
        assertEquals(1, result.size());
        assertEquals("FINALIZED", result.get(0).getPaymentStatus());
        verify(salesRepository, times(1)).findByPaymentStatusNot("DRAFT");
    }

    @Test
    void testGetSalesByCustomerId() {
        // Arrange
        when(salesRepository.findByCustomerId(1)).thenReturn(Arrays.asList(sale1, sale2));

        // Act
        List<SaleDTO> result = salesService.getSalesByCustomerId(1);

        // Assert
        assertEquals(2, result.size());
        verify(salesRepository, times(1)).findByCustomerId(1);
    }

    @Test
    void testGetSaleById() {
        // Arrange
        when(salesRepository.findById(1)).thenReturn(Optional.of(sale1));

        // Act
        SaleDTO result = salesService.getSaleById(1);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getSaleId());
        verify(salesRepository, times(1)).findById(1);
    }

    @Test
    void testGetSaleById_SaleNotFound() {
        // Arrange
        when(salesRepository.findById(999)).thenReturn(Optional.empty());

        // Act & Assert
        Exception exception = assertThrows(NotFoundException.class, () -> salesService.getSaleById(999));
        assertTrue(exception.getMessage().contains(ErrorMessages.SALE_NOT_FOUND));
        verify(salesRepository, times(1)).findById(999);
    }

    @Test
    void testFinalizeSale() {
        // Arrange
        FinalizeSaleDTO finalizeSaleDTO = new FinalizeSaleDTO();
        finalizeSaleDTO.setTotalAmount(new BigDecimal("100.00"));
        finalizeSaleDTO.setTotalItemCount(2);

        PaymentDTO paymentDTO = new PaymentDTO();
        paymentDTO.setCashAmount(new BigDecimal("100.00"));
        finalizeSaleDTO.setPayment(paymentDTO);

        when(salesRepository.findById(1)).thenReturn(Optional.of(sale1));
        when(salesCheck.verifiTotalAmount(any(BigDecimal.class), any(List.class))).thenReturn(true);
        when(salesCheck.verifiTotalQuantity(any(Integer.class), any(List.class))).thenReturn(true);
        when(salesCheck.verifyPayment(any(BigDecimal.class), any(PaymentDTO.class))).thenReturn(true);
        when(salesRepository.save(any(Sales.class))).thenReturn(sale1);

        // Act
        SaleDTO result = salesService.finalizeSale(1, finalizeSaleDTO);

        // Assert
        assertNotNull(result);
        assertEquals("FINALIZED", sale1.getPaymentStatus());
        verify(salesRepository, times(1)).findById(1);
        verify(paymentRepository, times(1)).save(any(Payment.class));
        verify(salesRepository, times(1)).save(sale1);
    }

    @Test
    void testFinalizeSale_NoSalesItems() {
        // Arrange
        FinalizeSaleDTO finalizeSaleDTO = new FinalizeSaleDTO();
        finalizeSaleDTO.setTotalAmount(new BigDecimal("100.00"));
        finalizeSaleDTO.setTotalItemCount(2);

        when(salesRepository.findById(1)).thenReturn(Optional.of(sale1));
        when(salesItemsRepository.findBySaleId(1)).thenReturn(Arrays.asList());

        // Act & Assert
        Exception exception = assertThrows(NotFoundException.class, 
                () -> salesService.finalizeSale(1, finalizeSaleDTO));
        assertTrue(exception.getMessage().contains("No sales items found"));
        verify(salesRepository, times(1)).findById(1);
        verify(salesRepository, never()).save(any(Sales.class));
    }

    @Test
    void testFinalizeSale_TotalAmountMismatch() {
        // Arrange
        FinalizeSaleDTO finalizeSaleDTO = new FinalizeSaleDTO();
        finalizeSaleDTO.setTotalAmount(new BigDecimal("150.00"));
        finalizeSaleDTO.setTotalItemCount(2);

        when(salesRepository.findById(1)).thenReturn(Optional.of(sale1));
        when(salesCheck.verifiTotalAmount(any(BigDecimal.class), any(List.class))).thenReturn(false);

        // Act & Assert
        Exception exception = assertThrows(TotalAmountException.class, 
                () -> salesService.finalizeSale(1, finalizeSaleDTO));
        assertTrue(exception.getMessage().contains(ErrorMessages.TOTAL_AMOUNT_NOT_MATCH));
        verify(salesRepository, times(1)).findById(1);
        verify(salesRepository, never()).save(any(Sales.class));
    }

    @Test
    void testFinalizeSale_TotalQuantityMismatch() {
        // Arrange
        FinalizeSaleDTO finalizeSaleDTO = new FinalizeSaleDTO();
        finalizeSaleDTO.setTotalAmount(new BigDecimal("100.00"));
        finalizeSaleDTO.setTotalItemCount(3);

        when(salesRepository.findById(1)).thenReturn(Optional.of(sale1));
        when(salesCheck.verifiTotalAmount(any(), any())).thenReturn(true);
        when(salesCheck.verifiTotalQuantity(any(), any())).thenReturn(false);

        // Act & Assert
        Exception exception = assertThrows(TotalQuantityException.class, 
                () -> salesService.finalizeSale(1, finalizeSaleDTO));
        assertTrue(exception.getMessage().contains(ErrorMessages.ITEM_COUNT_NOT_MATCH));
        verify(salesRepository, times(1)).findById(1);
        verify(salesRepository, never()).save(any(Sales.class));
    }

    @Test
    void testFinalizeSale_PaymentAmountMismatch() {
        // Arrange
        FinalizeSaleDTO finalizeSaleDTO = new FinalizeSaleDTO();
        finalizeSaleDTO.setTotalAmount(new BigDecimal("100.00"));
        finalizeSaleDTO.setTotalItemCount(2);

        PaymentDTO paymentDTO = new PaymentDTO();
        paymentDTO.setCashAmount(new BigDecimal("50.00"));
        finalizeSaleDTO.setPayment(paymentDTO);

        when(salesRepository.findById(1)).thenReturn(Optional.of(sale1));
        when(salesCheck.verifiTotalAmount(any(), any())).thenReturn(true);
        when(salesCheck.verifiTotalQuantity(any(), any())).thenReturn(true);
        when(salesCheck.verifyPayment(any(), any())).thenReturn(false);

        // Act & Assert
        Exception exception = assertThrows(PaymentAmountException.class, 
                () -> salesService.finalizeSale(1, finalizeSaleDTO));
        assertTrue(exception.getMessage().contains(ErrorMessages.PAYMENT_AMOUNT_NOT_MATCH));
        verify(salesRepository, times(1)).findById(1);
        verify(salesRepository, never()).save(any(Sales.class));
    }

    @Test
    void testDeleteSale() {
        // Arrange
        when(salesRepository.findBySaleId(1)).thenReturn(Optional.of(sale1));

        // Act
        salesService.deleteSale(1);

        // Assert
        verify(salesRepository, times(1)).findBySaleId(1);
        verify(salesRepository, times(1)).delete(sale1);
    }

    @Test
    void testDeleteSale_SaleNotFound() {
        // Arrange
        when(salesRepository.findBySaleId(999)).thenReturn(Optional.empty());

        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class, () -> salesService.deleteSale(999));
        assertTrue(exception.getMessage().contains("Sale not found"));
        verify(salesRepository, times(1)).findBySaleId(999);
        verify(salesRepository, never()).delete(any(Sales.class));
    }
}
