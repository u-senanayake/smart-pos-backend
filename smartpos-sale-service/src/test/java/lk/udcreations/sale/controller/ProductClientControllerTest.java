package lk.udcreations.sale.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import lk.udcreations.common.dto.inventory.InventoryDTO;
import lk.udcreations.common.dto.inventory.StockDTO;
import lk.udcreations.common.dto.product.ProductDTO;
import lk.udcreations.sale.config.ProductServiceClient;

class ProductClientControllerTest {

    @Mock
    private ProductServiceClient productServiceClient;

    @InjectMocks
    private ProductClientController productClientController;

    private ProductDTO mockProduct;
    private InventoryDTO mockInventory;
    private StockDTO mockStock;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Initialize test data
        mockProduct = new ProductDTO();
        mockProduct.setId(1);
        mockProduct.setProductName("Test Product");
        mockProduct.setPrice(new BigDecimal("50.00"));
        mockProduct.setEnabled(true);
        mockProduct.setDeleted(false);

        mockInventory = new InventoryDTO();
        mockInventory.setInventoryId(1);
        mockInventory.setQuantity(10);

        // Create a product for the inventory
        ProductDTO inventoryProduct = new ProductDTO();
        inventoryProduct.setId(1);
        mockInventory.setProduct(inventoryProduct);

        mockStock = new StockDTO(5);
    }

    @Test
    void testGetProductById() {
        // Arrange
        when(productServiceClient.getProductById(1)).thenReturn(mockProduct);

        // Act
        ProductDTO result = productClientController.getProductById(1);

        // Assert
        assertEquals(1, result.getId());
        assertEquals("Test Product", result.getProductName());
        assertEquals(new BigDecimal("50.00"), result.getPrice());
        assertTrue(result.isEnabled());
        assertFalse(result.isDeleted());
        verify(productServiceClient, times(1)).getProductById(1);
    }

    @Test
    void testCheckProductDeletedByProductId() {
        // Arrange
        when(productServiceClient.checkProductDeletedByProductId("PROD-1")).thenReturn(false);

        // Act
        boolean result = productClientController.checkProductDeletedByProductId("PROD-1");

        // Assert
        assertFalse(result);
        verify(productServiceClient, times(1)).checkProductDeletedByProductId("PROD-1");
    }

    @Test
    void testCheckProductDeletedById() {
        // Arrange
        when(productServiceClient.checkProductDeletedById(1)).thenReturn(false);

        // Act
        boolean result = productClientController.checkProductDeletedById(1);

        // Assert
        assertFalse(result);
        verify(productServiceClient, times(1)).checkProductDeletedById(1);
    }

    @Test
    void testCheckProductEnabledByProductId() {
        // Arrange
        when(productServiceClient.checkProductEnabledByProductId("PROD-1")).thenReturn(true);

        // Act
        boolean result = productClientController.checkProductEnabledByProductId("PROD-1");

        // Assert
        assertTrue(result);
        verify(productServiceClient, times(1)).checkProductEnabledByProductId("PROD-1");
    }

    @Test
    void testCheckProductEnabledById() {
        // Arrange
        when(productServiceClient.checkProductEnabledById(1)).thenReturn(true);

        // Act
        boolean result = productClientController.checkProductEnabledById(1);

        // Assert
        assertTrue(result);
        verify(productServiceClient, times(1)).checkProductEnabledById(1);
    }

    @Test
    void testAddStock() {
        // Arrange
        when(productServiceClient.addStock(1, mockStock)).thenReturn(mockInventory);

        // Act
        InventoryDTO result = productClientController.addStock(1, mockStock);

        // Assert
        assertEquals(1, result.getInventoryId());
        assertEquals(1, result.getProduct().getId());
        assertEquals(10, result.getQuantity());
        verify(productServiceClient, times(1)).addStock(1, mockStock);
    }

    @Test
    void testCheckStockAvailability() {
        // Arrange
        when(productServiceClient.checkStockAvailability(1, 5)).thenReturn(true);

        // Act
        boolean result = productClientController.checkStockAvailability(1, 5);

        // Assert
        assertTrue(result);
        verify(productServiceClient, times(1)).checkStockAvailability(1, 5);
    }
}
