package lk.udcreations.sale.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;

import lk.udcreations.common.dto.inventory.StockDTO;
import lk.udcreations.common.dto.returns.CreateReturnDTO;
import lk.udcreations.common.dto.returns.ReturnDTO;
import lk.udcreations.common.dto.salesitems.SalesItemDTO;
import lk.udcreations.sale.controller.ProductClientController;
import lk.udcreations.sale.entity.Returns;
import lk.udcreations.sale.entity.Sales;
import lk.udcreations.sale.entity.SalesItems;
import lk.udcreations.sale.exception.NotFoundException;
import lk.udcreations.sale.repository.ReturnsRepository;
import lk.udcreations.sale.repository.SalesItemsRepository;
import lk.udcreations.sale.repository.SalesRepository;

class ReturnsServiceTest {

    @Mock
    private ReturnsRepository returnsRepository;

    @Mock
    private SalesRepository salesRepository;

    @Mock
    private SalesItemsRepository salesItemsRepository;

    @Mock
    private ProductClientController productClientController;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private ReturnsService returnsService;

    private Sales mockSale;
    private SalesItems mockSalesItem;
    private Returns mockReturn;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Mock Sale
        mockSale = new Sales();
        mockSale.setSaleId(1);
        mockSale.setTotalAmount(new BigDecimal("100.00"));
        mockSale.setTotalItemCount(2);
        mockSale.setPaymentStatus("FINALIZED");

        // Mock SalesItem
        mockSalesItem = new SalesItems();
        mockSalesItem.setSalesItemId(1);
        mockSalesItem.setSaleId(1);
        mockSalesItem.setProductId(1);
        mockSalesItem.setQuantity(2);
        mockSalesItem.setPricePerUnit(new BigDecimal("50.00"));
        mockSalesItem.setTotalPrice(new BigDecimal("100.00"));
        mockSalesItem.setReturnedQuantity(0);

        // Mock Return
        mockReturn = new Returns();
        mockReturn.setReturnId(1);
        mockReturn.setSaleId(1);
        mockReturn.setSalesItemId(1);
        mockReturn.setQuantity(1);
        mockReturn.setReason("Defective product");
        mockReturn.setRefundAmount(new BigDecimal("50.00"));
        mockReturn.setReturnDate(LocalDateTime.now());

        // Mock repository methods
        when(salesRepository.findById(1)).thenReturn(Optional.of(mockSale));
        when(salesItemsRepository.findById(1)).thenReturn(Optional.of(mockSalesItem));
        when(salesItemsRepository.findBySaleId(1)).thenReturn(Arrays.asList(mockSalesItem));
        when(returnsRepository.findBySaleId(1)).thenReturn(Arrays.asList(mockReturn));

        // Mock modelMapper
        when(modelMapper.map(any(), any())).thenAnswer(invocation -> {
            Object source = invocation.getArgument(0);
            Class<?> targetClass = invocation.getArgument(1);

            if (source instanceof Returns && targetClass == ReturnDTO.class) {
                Returns returnEntity = (Returns) source;
                ReturnDTO dto = new ReturnDTO();
                dto.setReturnId(returnEntity.getReturnId());
                dto.setSaleId(returnEntity.getSaleId());
                // ReturnDTO doesn't have setSalesItemId method
                // Instead, it has a list of SalesItemDTO
                dto.setQuantity(returnEntity.getQuantity());
                dto.setReason(returnEntity.getReason());
                dto.setRefundAmount(returnEntity.getRefundAmount());
                dto.setReturnDate(returnEntity.getReturnDate());
                return dto;
            } else if (source instanceof SalesItems && targetClass == SalesItemDTO.class) {
                SalesItems item = (SalesItems) source;
                SalesItemDTO dto = new SalesItemDTO();
                dto.setSalesItemId(item.getSalesItemId());
                dto.setSaleId(item.getSaleId());
                dto.setQuantity(item.getQuantity());
                dto.setTotalPrice(item.getTotalPrice());
                return dto;
            }

            return null;
        });
    }

    @Test
    void testProcessReturn_Success() {
        // Arrange
        CreateReturnDTO createReturnDTO = new CreateReturnDTO();
        createReturnDTO.setSaleId(1);
        createReturnDTO.setSalesItemId(1);
        createReturnDTO.setQuantity(1);
        createReturnDTO.setReason("Defective product");

        List<CreateReturnDTO> createReturnDTOList = new ArrayList<>();
        createReturnDTOList.add(createReturnDTO);

        when(returnsRepository.save(any(Returns.class))).thenReturn(mockReturn);
        when(salesRepository.save(any(Sales.class))).thenReturn(mockSale);
        when(salesItemsRepository.save(any(SalesItems.class))).thenReturn(mockSalesItem);

        // Act
        List<ReturnDTO> result = returnsService.processReturn(createReturnDTOList);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1, result.get(0).getReturnId());
        assertEquals(1, result.get(0).getSaleId());
        // ReturnDTO doesn't have getSalesItemId method
        // Instead, we can check other properties
        assertEquals(1, result.get(0).getQuantity());
        assertEquals("Defective product", result.get(0).getReason());
        assertEquals(new BigDecimal("50.00"), result.get(0).getRefundAmount());
        verify(productClientController, times(1)).addStock(any(Integer.class), any(StockDTO.class));
        verify(salesRepository, times(1)).save(any(Sales.class));
        verify(salesItemsRepository, times(1)).save(any(SalesItems.class));
        verify(returnsRepository, times(1)).save(any(Returns.class));
    }

    @Test
    void testProcessReturn_SaleNotFound() {
        // Arrange
        CreateReturnDTO createReturnDTO = new CreateReturnDTO();
        createReturnDTO.setSaleId(999);
        createReturnDTO.setSalesItemId(1);
        createReturnDTO.setQuantity(1);
        createReturnDTO.setReason("Defective product");

        List<CreateReturnDTO> createReturnDTOList = new ArrayList<>();
        createReturnDTOList.add(createReturnDTO);

        when(salesRepository.findById(999)).thenReturn(Optional.empty());

        // Act & Assert
        Exception exception = assertThrows(NotFoundException.class, 
                () -> returnsService.processReturn(createReturnDTOList));
        assertTrue(exception.getMessage().contains("Sale not found"));
    }

    @Test
    void testProcessReturn_SalesItemNotFound() {
        // Arrange
        CreateReturnDTO createReturnDTO = new CreateReturnDTO();
        createReturnDTO.setSaleId(1);
        createReturnDTO.setSalesItemId(999);
        createReturnDTO.setQuantity(1);
        createReturnDTO.setReason("Defective product");

        List<CreateReturnDTO> createReturnDTOList = new ArrayList<>();
        createReturnDTOList.add(createReturnDTO);

        when(salesItemsRepository.findById(999)).thenReturn(Optional.empty());

        // Act & Assert
        Exception exception = assertThrows(NotFoundException.class, 
                () -> returnsService.processReturn(createReturnDTOList));
        assertTrue(exception.getMessage().contains("Sales item not found"));
    }

    @Test
    void testProcessReturn_InsufficientQuantity() {
        // Arrange
        CreateReturnDTO createReturnDTO = new CreateReturnDTO();
        createReturnDTO.setSaleId(1);
        createReturnDTO.setSalesItemId(1);
        createReturnDTO.setQuantity(3); // More than available
        createReturnDTO.setReason("Defective product");

        List<CreateReturnDTO> createReturnDTOList = new ArrayList<>();
        createReturnDTOList.add(createReturnDTO);

        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, 
                () -> returnsService.processReturn(createReturnDTOList));
        assertTrue(exception.getMessage().contains("Insufficient quantity for return"));
    }

    @Test
    void testGetReturnsBySaleId() {
        // Arrange
        when(returnsRepository.findBySaleId(1)).thenReturn(Arrays.asList(mockReturn));

        // Act
        List<ReturnDTO> result = returnsService.getReturnsBySaleId(1);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1, result.get(0).getReturnId());
        assertEquals(1, result.get(0).getSaleId());
        verify(returnsRepository, times(1)).findBySaleId(1);
    }
}
