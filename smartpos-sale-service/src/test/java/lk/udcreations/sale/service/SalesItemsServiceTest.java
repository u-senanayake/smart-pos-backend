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
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;

import lk.udcreations.common.dto.product.ProductDTO;
import lk.udcreations.common.dto.salesitems.CreateSalesItemDTO;
import lk.udcreations.common.dto.salesitems.SalesItemDTO;
import lk.udcreations.sale.constants.ErrorMessages;
import lk.udcreations.sale.controller.ProductClientController;
import lk.udcreations.sale.entity.Sales;
import lk.udcreations.sale.entity.SalesItems;
import lk.udcreations.sale.exception.DiscountMismatchException;
import lk.udcreations.sale.exception.InsufficientStockException;
import lk.udcreations.sale.exception.NotFoundException;
import lk.udcreations.sale.exception.ProductNotActiveException;
import lk.udcreations.sale.exception.TotalMismatchException;
import lk.udcreations.sale.exception.UnitPriceMismatchException;
import lk.udcreations.sale.repository.SalesItemsRepository;
import lk.udcreations.sale.repository.SalesRepository;
import lk.udcreations.sale.util.relationcheck.InventoryCheck;
import lk.udcreations.sale.util.relationcheck.ProductCheck;

class SalesItemsServiceTest {

    @Mock
    private SalesItemsRepository salesItemsRepository;

    @Mock
    private SalesRepository salesRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private ProductClientController productClientController;

    @Mock
    private ProductCheck productCheck;

    @Mock
    private InventoryCheck inventoryCheck;

    @InjectMocks
    private SalesItemsService salesItemsService;

    private Sales mockSale;
    private SalesItems salesItem1;
    private SalesItems salesItem2;
    private ProductDTO mockProduct;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Mock Sale
        mockSale = new Sales();
        mockSale.setSaleId(1);
        mockSale.setTotalAmount(new BigDecimal("100.00"));
        mockSale.setTotalItemCount(2);
        mockSale.setPaymentStatus("DRAFT");

        // Mock Product
        mockProduct = new ProductDTO();
        mockProduct.setId(1);
        mockProduct.setProductName("Test Product");
        mockProduct.setPrice(new BigDecimal("50.00"));
        mockProduct.setEnabled(true);
        mockProduct.setDeleted(false);

        // Create test sales items
        salesItem1 = new SalesItems();
        salesItem1.setSalesItemId(1);
        salesItem1.setSaleId(1);
        salesItem1.setProductId(1);
        salesItem1.setQuantity(2);
        salesItem1.setPricePerUnit(new BigDecimal("50.00"));
        salesItem1.setItemDiscountVal(BigDecimal.ZERO);
        salesItem1.setItemDiscountPer(0);
        salesItem1.setTotalPrice(new BigDecimal("100.00"));

        salesItem2 = new SalesItems();
        salesItem2.setSalesItemId(2);
        salesItem2.setSaleId(1);
        salesItem2.setProductId(2);
        salesItem2.setQuantity(1);
        salesItem2.setPricePerUnit(new BigDecimal("75.00"));
        salesItem2.setItemDiscountVal(BigDecimal.ZERO);
        salesItem2.setItemDiscountPer(0);
        salesItem2.setTotalPrice(new BigDecimal("75.00"));

        // Mock repository methods
        when(salesRepository.findById(1)).thenReturn(Optional.of(mockSale));
        when(productClientController.getProductById(1)).thenReturn(mockProduct);

        // Mock modelMapper
        when(modelMapper.map(any(), any())).thenAnswer(invocation -> {
            Object source = invocation.getArgument(0);
            Class<?> targetClass = invocation.getArgument(1);

            if (source instanceof SalesItems && targetClass == SalesItemDTO.class) {
                SalesItems item = (SalesItems) source;
                SalesItemDTO dto = new SalesItemDTO();
                dto.setSalesItemId(item.getSalesItemId());
                dto.setSaleId(item.getSaleId());
                dto.setQuantity(item.getQuantity());
                dto.setItemDiscountVal(item.getItemDiscountVal());
                dto.setItemDiscountPer(item.getItemDiscountPer());
                dto.setTotalPrice(item.getTotalPrice());

                ProductDTO product = new ProductDTO();
                product.setId(item.getProductId());
                product.setPrice(item.getPricePerUnit());
                dto.setProduct(product);
                return dto;
            }

            return null;
        });
    }

    @Test
    void testCreateSalesItem_NewItem() {
        // Arrange
        CreateSalesItemDTO createSalesItemDTO = new CreateSalesItemDTO();
        createSalesItemDTO.setSaleId(1);
        createSalesItemDTO.setProductId(1);
        createSalesItemDTO.setQuantity(2);
        createSalesItemDTO.setPricePerUnit(new BigDecimal("50.00"));
        createSalesItemDTO.setItemDiscountVal(BigDecimal.ZERO);
        createSalesItemDTO.setItemDiscountPer(0);
        createSalesItemDTO.setTotalPrice(new BigDecimal("100.00"));

        when(productCheck.isProductEnabled(any())).thenReturn(true);
        when(productCheck.isProductDeleted(any())).thenReturn(false);
        when(inventoryCheck.checkStockAvailability(any(), any())).thenReturn(true);
        when(productCheck.isUnitPriceMatch(any(), any())).thenReturn(true);
        when(productCheck.isDiscountMatch(any(), any())).thenReturn(true);
        when(productCheck.isTotalMatch(any(), any())).thenReturn(true);
        when(salesItemsRepository.findByProductIdAndSaleId(1, 1)).thenReturn(Optional.empty());
        when(salesItemsRepository.save(any(SalesItems.class))).thenReturn(salesItem1);

        // Act
        SalesItemDTO result = salesItemsService.createSalesItem(createSalesItemDTO);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getSalesItemId());
        assertEquals(1, result.getSaleId());
        assertEquals(1, result.getProduct().getId());
        assertEquals(2, result.getQuantity());
        assertEquals(new BigDecimal("50.00"), result.getProduct().getPrice());
        assertEquals(new BigDecimal("100.00"), result.getTotalPrice());
        verify(salesItemsRepository, times(1)).save(any(SalesItems.class));
    }

    @Test
    void testCreateSalesItem_ExistingItem() {
        // Arrange
        CreateSalesItemDTO createSalesItemDTO = new CreateSalesItemDTO();
        createSalesItemDTO.setSaleId(1);
        createSalesItemDTO.setProductId(1);
        createSalesItemDTO.setQuantity(1);
        createSalesItemDTO.setPricePerUnit(new BigDecimal("50.00"));
        createSalesItemDTO.setItemDiscountVal(BigDecimal.ZERO);
        createSalesItemDTO.setItemDiscountPer(0);
        createSalesItemDTO.setTotalPrice(new BigDecimal("50.00"));

        SalesItems existingItem = new SalesItems();
        existingItem.setSalesItemId(1);
        existingItem.setSaleId(1);
        existingItem.setProductId(1);
        existingItem.setQuantity(1);
        existingItem.setPricePerUnit(new BigDecimal("50.00"));
        existingItem.setItemDiscountVal(BigDecimal.ZERO);
        existingItem.setItemDiscountPer(0);
        existingItem.setTotalPrice(new BigDecimal("50.00"));

        when(productCheck.isProductEnabled(any())).thenReturn(true);
        when(productCheck.isProductDeleted(any())).thenReturn(false);
        when(inventoryCheck.checkStockAvailability(any(), any())).thenReturn(true);
        when(productCheck.isUnitPriceMatch(any(), any())).thenReturn(true);
        when(productCheck.isDiscountMatch(any(), any())).thenReturn(true);
        when(productCheck.isTotalMatch(any(), any())).thenReturn(true);
        when(salesItemsRepository.findByProductIdAndSaleId(1, 1)).thenReturn(Optional.of(existingItem));
        when(salesItemsRepository.save(any(SalesItems.class))).thenReturn(salesItem1);

        // Act
        SalesItemDTO result = salesItemsService.createSalesItem(createSalesItemDTO);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getSalesItemId());
        assertEquals(1, result.getSaleId());
        assertEquals(1, result.getProduct().getId());
        verify(salesItemsRepository, times(1)).save(any(SalesItems.class));
    }

    @Test
    void testCreateSalesItem_ProductNotEnabled() {
        // Arrange
        CreateSalesItemDTO createSalesItemDTO = new CreateSalesItemDTO();
        createSalesItemDTO.setSaleId(1);
        createSalesItemDTO.setProductId(1);
        createSalesItemDTO.setQuantity(2);

        when(productCheck.isProductEnabled(any())).thenReturn(false);

        // Act & Assert
        Exception exception = assertThrows(ProductNotActiveException.class, 
                () -> salesItemsService.createSalesItem(createSalesItemDTO));
        assertTrue(exception.getMessage().contains("This product cannot sell"));
        verify(salesItemsRepository, never()).save(any(SalesItems.class));
    }

    @Test
    void testCreateSalesItem_ProductDeleted() {
        // Arrange
        CreateSalesItemDTO createSalesItemDTO = new CreateSalesItemDTO();
        createSalesItemDTO.setSaleId(1);
        createSalesItemDTO.setProductId(1);
        createSalesItemDTO.setQuantity(2);

        when(productCheck.isProductEnabled(any())).thenReturn(true);
        when(productCheck.isProductDeleted(any())).thenReturn(true);

        // Act & Assert
        Exception exception = assertThrows(ProductNotActiveException.class, 
                () -> salesItemsService.createSalesItem(createSalesItemDTO));
        assertTrue(exception.getMessage().contains("This product cannot sell"));
        verify(salesItemsRepository, never()).save(any(SalesItems.class));
    }

    @Test
    void testCreateSalesItem_InsufficientStock() {
        // Arrange
        CreateSalesItemDTO createSalesItemDTO = new CreateSalesItemDTO();
        createSalesItemDTO.setSaleId(1);
        createSalesItemDTO.setProductId(1);
        createSalesItemDTO.setQuantity(10);

        when(productCheck.isProductEnabled(any())).thenReturn(true);
        when(productCheck.isProductDeleted(any())).thenReturn(false);
        when(inventoryCheck.checkStockAvailability(any(), any())).thenReturn(false);

        // Act & Assert
        Exception exception = assertThrows(InsufficientStockException.class, 
                () -> salesItemsService.createSalesItem(createSalesItemDTO));
        assertTrue(exception.getMessage().contains("Stock is not enoug to sell"));
        verify(salesItemsRepository, never()).save(any(SalesItems.class));
    }

    @Test
    void testCreateSalesItem_UnitPriceMismatch() {
        // Arrange
        CreateSalesItemDTO createSalesItemDTO = new CreateSalesItemDTO();
        createSalesItemDTO.setSaleId(1);
        createSalesItemDTO.setProductId(1);
        createSalesItemDTO.setQuantity(2);

        when(productCheck.isProductEnabled(any())).thenReturn(true);
        when(productCheck.isProductDeleted(any())).thenReturn(false);
        when(inventoryCheck.checkStockAvailability(any(), any())).thenReturn(true);
        when(productCheck.isUnitPriceMatch(any(), any())).thenReturn(false);

        // Act & Assert
        Exception exception = assertThrows(UnitPriceMismatchException.class, 
                () -> salesItemsService.createSalesItem(createSalesItemDTO));
        assertTrue(exception.getMessage().contains("There is a problem with selling price"));
        verify(salesItemsRepository, never()).save(any(SalesItems.class));
    }

    @Test
    void testCreateSalesItem_DiscountMismatch() {
        // Arrange
        CreateSalesItemDTO createSalesItemDTO = new CreateSalesItemDTO();
        createSalesItemDTO.setSaleId(1);
        createSalesItemDTO.setProductId(1);
        createSalesItemDTO.setQuantity(2);

        when(productCheck.isProductEnabled(any())).thenReturn(true);
        when(productCheck.isProductDeleted(any())).thenReturn(false);
        when(inventoryCheck.checkStockAvailability(any(), any())).thenReturn(true);
        when(productCheck.isUnitPriceMatch(any(), any())).thenReturn(true);
        when(productCheck.isDiscountMatch(any(), any())).thenReturn(false);

        // Act & Assert
        Exception exception = assertThrows(DiscountMismatchException.class, 
                () -> salesItemsService.createSalesItem(createSalesItemDTO));
        assertTrue(exception.getMessage().contains("There is a problem with discount"));
        verify(salesItemsRepository, never()).save(any(SalesItems.class));
    }

    @Test
    void testCreateSalesItem_TotalMismatch() {
        // Arrange
        CreateSalesItemDTO createSalesItemDTO = new CreateSalesItemDTO();
        createSalesItemDTO.setSaleId(1);
        createSalesItemDTO.setProductId(1);
        createSalesItemDTO.setQuantity(2);

        when(productCheck.isProductEnabled(any())).thenReturn(true);
        when(productCheck.isProductDeleted(any())).thenReturn(false);
        when(inventoryCheck.checkStockAvailability(any(), any())).thenReturn(true);
        when(productCheck.isUnitPriceMatch(any(), any())).thenReturn(true);
        when(productCheck.isDiscountMatch(any(), any())).thenReturn(true);
        when(productCheck.isTotalMatch(any(), any())).thenReturn(false);

        // Act & Assert
        Exception exception = assertThrows(TotalMismatchException.class, 
                () -> salesItemsService.createSalesItem(createSalesItemDTO));
        assertTrue(exception.getMessage().contains("There is a problem with total price"));
        verify(salesItemsRepository, never()).save(any(SalesItems.class));
    }

    @Test
    void testUpdateSalesItem() {
        // Arrange
        CreateSalesItemDTO updateSalesItemDTO = new CreateSalesItemDTO();
        updateSalesItemDTO.setQuantity(3);
        updateSalesItemDTO.setPricePerUnit(new BigDecimal("50.00"));
        updateSalesItemDTO.setItemDiscountVal(BigDecimal.ZERO);
        updateSalesItemDTO.setItemDiscountPer(0);
        updateSalesItemDTO.setTotalPrice(new BigDecimal("150.00"));

        when(salesItemsRepository.findById(1)).thenReturn(Optional.of(salesItem1));
        when(salesItemsRepository.save(any(SalesItems.class))).thenReturn(salesItem1);

        // Act
        SalesItemDTO result = salesItemsService.updateSalesItem(1, updateSalesItemDTO);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getSalesItemId());
        assertEquals(3, salesItem1.getQuantity());
        assertEquals(new BigDecimal("150.00"), salesItem1.getTotalPrice());
        verify(salesItemsRepository, times(1)).findById(1);
        verify(salesItemsRepository, times(1)).save(salesItem1);
    }

    @Test
    void testUpdateSalesItem_ItemNotFound() {
        // Arrange
        CreateSalesItemDTO updateSalesItemDTO = new CreateSalesItemDTO();
        updateSalesItemDTO.setQuantity(3);

        when(salesItemsRepository.findById(999)).thenReturn(Optional.empty());

        // Act & Assert
        Exception exception = assertThrows(NotFoundException.class, 
                () -> salesItemsService.updateSalesItem(999, updateSalesItemDTO));
        assertTrue(exception.getMessage().contains(ErrorMessages.SALESITEM_NOT_FOUND));
        verify(salesItemsRepository, times(1)).findById(999);
        verify(salesItemsRepository, never()).save(any(SalesItems.class));
    }

    @Test
    void testGetAllSaleItemsBySaleId() {
        // Arrange
        when(salesItemsRepository.findBySaleId(1)).thenReturn(Arrays.asList(salesItem1, salesItem2));

        // Act
        List<SalesItemDTO> result = salesItemsService.getAllSaleItemsBySaleId(1);

        // Assert
        assertEquals(2, result.size());
        verify(salesItemsRepository, times(1)).findBySaleId(1);
    }

    @Test
    void testDeleteSaleItem() {
        // Arrange
        when(salesItemsRepository.findById(1)).thenReturn(Optional.of(salesItem1));

        // Act
        salesItemsService.deleteSaleItem(1);

        // Assert
        verify(salesItemsRepository, times(1)).findById(1);
        verify(salesItemsRepository, times(1)).delete(salesItem1);
    }

    @Test
    void testDeleteSaleItem_ItemNotFound() {
        // Arrange
        when(salesItemsRepository.findById(999)).thenReturn(Optional.empty());

        // Act & Assert
        Exception exception = assertThrows(NotFoundException.class, () -> salesItemsService.deleteSaleItem(999));
        assertTrue(exception.getMessage().contains(ErrorMessages.SALESITEM_NOT_FOUND));
        verify(salesItemsRepository, times(1)).findById(999);
        verify(salesItemsRepository, never()).delete(any(SalesItems.class));
    }
}
