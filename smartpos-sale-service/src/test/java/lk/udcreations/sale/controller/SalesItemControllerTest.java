package lk.udcreations.sale.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;

import lk.udcreations.common.dto.product.ProductDTO;
import lk.udcreations.common.dto.salesitems.CreateSalesItemDTO;
import lk.udcreations.common.dto.salesitems.SalesItemDTO;
import lk.udcreations.sale.service.SalesItemsService;

class SalesItemControllerTest {

    @Mock
    private SalesItemsService salesItemsService;

    @InjectMocks
    private SalesItemController salesItemController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    private SalesItemDTO salesItemDTO1;
    private SalesItemDTO salesItemDTO2;
    private CreateSalesItemDTO createSalesItemDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(salesItemController).build();
        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules(); // Register Java 8 time support

        // Create product DTOs
        ProductDTO productDTO1 = new ProductDTO();
        productDTO1.setId(1);
        productDTO1.setProductName("Test Product 1");
        productDTO1.setPrice(new BigDecimal("50.00"));

        ProductDTO productDTO2 = new ProductDTO();
        productDTO2.setId(2);
        productDTO2.setProductName("Test Product 2");
        productDTO2.setPrice(new BigDecimal("75.00"));

        // Initialize test data
        salesItemDTO1 = new SalesItemDTO();
        salesItemDTO1.setSalesItemId(1);
        salesItemDTO1.setSaleId(1);
        salesItemDTO1.setQuantity(2);
        salesItemDTO1.setItemDiscountVal(BigDecimal.ZERO);
        salesItemDTO1.setItemDiscountPer(0);
        salesItemDTO1.setTotalPrice(new BigDecimal("100.00"));
        salesItemDTO1.setProduct(productDTO1);

        salesItemDTO2 = new SalesItemDTO();
        salesItemDTO2.setSalesItemId(2);
        salesItemDTO2.setSaleId(1);
        salesItemDTO2.setQuantity(1);
        salesItemDTO2.setItemDiscountVal(BigDecimal.ZERO);
        salesItemDTO2.setItemDiscountPer(0);
        salesItemDTO2.setTotalPrice(new BigDecimal("75.00"));
        salesItemDTO2.setProduct(productDTO2);

        createSalesItemDTO = new CreateSalesItemDTO();
        createSalesItemDTO.setSaleId(1);
        createSalesItemDTO.setProductId(1);
        createSalesItemDTO.setQuantity(2);
        createSalesItemDTO.setPricePerUnit(new BigDecimal("50.00"));
        createSalesItemDTO.setItemDiscountVal(BigDecimal.ZERO);
        createSalesItemDTO.setItemDiscountPer(0);
        createSalesItemDTO.setTotalPrice(new BigDecimal("100.00"));
    }

    @Test
    void testCreateSalesItem() throws Exception {
        when(salesItemsService.createSalesItem(any(CreateSalesItemDTO.class))).thenReturn(salesItemDTO1);

        mockMvc.perform(post("/api/v1/salesitem")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createSalesItemDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.salesItemId").value(1))
                .andExpect(jsonPath("$.saleId").value(1))
                .andExpect(jsonPath("$.quantity").value(2))
                .andExpect(jsonPath("$.totalPrice").value(100.00))
                .andExpect(jsonPath("$.product.id").value(1));

        verify(salesItemsService, times(1)).createSalesItem(any(CreateSalesItemDTO.class));
    }

    @Test
    void testUpdateSalesItem() throws Exception {
        when(salesItemsService.updateSalesItem(eq(1), any(CreateSalesItemDTO.class))).thenReturn(salesItemDTO1);

        mockMvc.perform(put("/api/v1/salesitem/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createSalesItemDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.salesItemId").value(1))
                .andExpect(jsonPath("$.saleId").value(1))
                .andExpect(jsonPath("$.quantity").value(2))
                .andExpect(jsonPath("$.totalPrice").value(100.00));

        verify(salesItemsService, times(1)).updateSalesItem(eq(1), any(CreateSalesItemDTO.class));
    }

    @Test
    void testGetSaleItemsBySaleId() throws Exception {
        List<SalesItemDTO> salesItems = Arrays.asList(salesItemDTO1, salesItemDTO2);
        when(salesItemsService.getAllSaleItemsBySaleId(1)).thenReturn(salesItems);

        mockMvc.perform(get("/api/v1/salesitem/sale/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].salesItemId").value(1))
                .andExpect(jsonPath("$[0].product.id").value(1))
                .andExpect(jsonPath("$[1].salesItemId").value(2))
                .andExpect(jsonPath("$[1].product.id").value(2));

        verify(salesItemsService, times(1)).getAllSaleItemsBySaleId(1);
    }

    @Test
    void testDeleteSalesItem() throws Exception {
        doNothing().when(salesItemsService).deleteSaleItem(1);

        mockMvc.perform(delete("/api/v1/salesitem/1"))
                .andExpect(status().isNoContent());

        verify(salesItemsService, times(1)).deleteSaleItem(1);
    }
}