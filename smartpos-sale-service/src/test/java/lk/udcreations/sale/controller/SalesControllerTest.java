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
import java.time.LocalDateTime;
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

import lk.udcreations.common.dto.payment.PaymentDTO;
import lk.udcreations.common.dto.sale.CreateSaleDTO;
import lk.udcreations.common.dto.sale.FinalizeSaleDTO;
import lk.udcreations.common.dto.sale.SaleDTO;
import lk.udcreations.common.dto.sale.UpdateSaleDTO;
import lk.udcreations.sale.service.SalesService;

class SalesControllerTest {

    @Mock
    private SalesService salesService;

    @InjectMocks
    private SalesController salesController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    private SaleDTO saleDTO1;
    private SaleDTO saleDTO2;
    private CreateSaleDTO createSaleDTO;
    private UpdateSaleDTO updateSaleDTO;
    private FinalizeSaleDTO finalizeSaleDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(salesController).build();
        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules(); // Register Java 8 time support

        // Initialize test data
        saleDTO1 = new SaleDTO();
        saleDTO1.setSaleId(1);
        saleDTO1.setTotalAmount(new BigDecimal("100.00"));
        saleDTO1.setTotalItemCount(2);
        saleDTO1.setPaymentStatus("DRAFT");
        saleDTO1.setSaleDateTime(LocalDateTime.now());

        saleDTO2 = new SaleDTO();
        saleDTO2.setSaleId(2);
        saleDTO2.setTotalAmount(new BigDecimal("150.00"));
        saleDTO2.setTotalItemCount(3);
        saleDTO2.setPaymentStatus("COMPLETED");
        saleDTO2.setSaleDateTime(LocalDateTime.now());

        createSaleDTO = new CreateSaleDTO();
        createSaleDTO.setCustomerId(101);

        updateSaleDTO = new UpdateSaleDTO();
        updateSaleDTO.setCustomerId(101);
        updateSaleDTO.setTotalAmount(new BigDecimal("120.00"));
        updateSaleDTO.setTotalItemCount(3);
        updateSaleDTO.setPaymentStatus("DRAFT");

        finalizeSaleDTO = new FinalizeSaleDTO();
        finalizeSaleDTO.setTotalAmount(new BigDecimal("100.00"));
        finalizeSaleDTO.setTotalItemCount(2);

        PaymentDTO paymentDTO = new PaymentDTO();
        paymentDTO.setCashAmount(new BigDecimal("100.00"));
        finalizeSaleDTO.setPayment(paymentDTO);
    }

    @Test
    void testCreateSale() throws Exception {
        when(salesService.createSale(any(CreateSaleDTO.class))).thenReturn(saleDTO1);

        mockMvc.perform(post("/api/v1/sale")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createSaleDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.saleId").value(1))
                .andExpect(jsonPath("$.customer.customerId").value(101))
                .andExpect(jsonPath("$.totalAmount").value(100.00))
                .andExpect(jsonPath("$.totalItemCount").value(2))
                .andExpect(jsonPath("$.paymentStatus").value("DRAFT"));

        verify(salesService, times(1)).createSale(any(CreateSaleDTO.class));
    }

    @Test
    void testUpdateSale() throws Exception {
        when(salesService.updateSale(eq(1), any(UpdateSaleDTO.class))).thenReturn(saleDTO1);

        mockMvc.perform(put("/api/v1/sale/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateSaleDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.saleId").value(1))
                .andExpect(jsonPath("$.customer.customerId").value(101));

        verify(salesService, times(1)).updateSale(eq(1), any(UpdateSaleDTO.class));
    }

    @Test
    void testGetAllSales() throws Exception {
        List<SaleDTO> sales = Arrays.asList(saleDTO1, saleDTO2);
        when(salesService.getAllSales()).thenReturn(sales);

        mockMvc.perform(get("/api/v1/sale"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].saleId").value(1))
                .andExpect(jsonPath("$[1].saleId").value(2));

        verify(salesService, times(1)).getAllSales();
    }

    @Test
    void testGetSalesByPaymentStatus() throws Exception {
        List<SaleDTO> sales = Arrays.asList(saleDTO2);
        when(salesService.getSalesByPaymentStatus("COMPLETED")).thenReturn(sales);

        mockMvc.perform(get("/api/v1/sale/payment/status/COMPLETED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].saleId").value(2))
                .andExpect(jsonPath("$[0].paymentStatus").value("COMPLETED"));

        verify(salesService, times(1)).getSalesByPaymentStatus("COMPLETED");
    }

    @Test
    void testGetDraftSales() throws Exception {
        List<SaleDTO> sales = Arrays.asList(saleDTO1);
        when(salesService.getDraftSales()).thenReturn(sales);

        mockMvc.perform(get("/api/v1/sale/payment/draft"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].saleId").value(1))
                .andExpect(jsonPath("$[0].paymentStatus").value("DRAFT"));

        verify(salesService, times(1)).getDraftSales();
    }

    @Test
    void testGetSalesHistory() throws Exception {
        List<SaleDTO> sales = Arrays.asList(saleDTO2);
        when(salesService.getSalesHistory()).thenReturn(sales);

        mockMvc.perform(get("/api/v1/sale/payment/notdraft"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].saleId").value(2))
                .andExpect(jsonPath("$[0].paymentStatus").value("COMPLETED"));

        verify(salesService, times(1)).getSalesHistory();
    }

    @Test
    void testGetSalesByCustomerId() throws Exception {
        List<SaleDTO> sales = Arrays.asList(saleDTO1);
        when(salesService.getSalesByCustomerId(101)).thenReturn(sales);

        mockMvc.perform(get("/api/v1/sale/customer/101"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].saleId").value(1))
                .andExpect(jsonPath("$[0].customer.customerId").value(101));

        verify(salesService, times(1)).getSalesByCustomerId(101);
    }

    @Test
    void testGetSaleById() throws Exception {
        when(salesService.getSaleById(1)).thenReturn(saleDTO1);

        mockMvc.perform(get("/api/v1/sale/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.saleId").value(1))
                .andExpect(jsonPath("$.customer.customerId").value(101));

        verify(salesService, times(1)).getSaleById(1);
    }

    @Test
    void testFinalizeSale() throws Exception {
        SaleDTO finalizedSale = new SaleDTO();
        finalizedSale.setSaleId(1);
        finalizedSale.setTotalAmount(new BigDecimal("100.00"));
        finalizedSale.setTotalItemCount(2);
        finalizedSale.setPaymentStatus("COMPLETED");
        finalizedSale.setSaleDateTime(LocalDateTime.now());

        when(salesService.finalizeSale(eq(1), any(FinalizeSaleDTO.class))).thenReturn(finalizedSale);

        mockMvc.perform(put("/api/v1/sale/finalize/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(finalizeSaleDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.saleId").value(1))
                .andExpect(jsonPath("$.paymentStatus").value("COMPLETED"));

        verify(salesService, times(1)).finalizeSale(eq(1), any(FinalizeSaleDTO.class));
    }

    @Test
    void testDeleteSale() throws Exception {
        doNothing().when(salesService).deleteSale(1);

        mockMvc.perform(delete("/api/v1/sale/1"))
                .andExpect(status().isNoContent());

        verify(salesService, times(1)).deleteSale(1);
    }
}
