package lk.udcreations.sale.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
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

import lk.udcreations.common.dto.returns.CreateReturnDTO;
import lk.udcreations.common.dto.returns.ReturnDTO;
import lk.udcreations.sale.service.ReturnsService;

class ReturnsControllerTest {

    @Mock
    private ReturnsService returnsService;

    @InjectMocks
    private ReturnsController returnsController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    private ReturnDTO returnDTO1;
    private ReturnDTO returnDTO2;
    private CreateReturnDTO createReturnDTO;
    private List<CreateReturnDTO> createReturnDTOList;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(returnsController).build();
        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules(); // Register Java 8 time support

        // Initialize test data
        returnDTO1 = new ReturnDTO();
        returnDTO1.setReturnId(1);
        returnDTO1.setSaleId(1);
        returnDTO1.setQuantity(1);
        returnDTO1.setReason("Defective product");
        returnDTO1.setRefundAmount(new BigDecimal("50.00"));
        returnDTO1.setReturnDate(LocalDateTime.now());

        returnDTO2 = new ReturnDTO();
        returnDTO2.setReturnId(2);
        returnDTO2.setSaleId(1);
        returnDTO2.setQuantity(2);
        returnDTO2.setReason("Wrong size");
        returnDTO2.setRefundAmount(new BigDecimal("100.00"));
        returnDTO2.setReturnDate(LocalDateTime.now());

        createReturnDTO = new CreateReturnDTO();
        createReturnDTO.setSaleId(1);
        createReturnDTO.setSalesItemId(1);
        createReturnDTO.setQuantity(1);
        createReturnDTO.setReason("Defective product");

        createReturnDTOList = new ArrayList<>();
        createReturnDTOList.add(createReturnDTO);
    }

    @Test
    void testProcessReturn() throws Exception {
        List<ReturnDTO> returnDTOList = Arrays.asList(returnDTO1);
        when(returnsService.processReturn(any(List.class))).thenReturn(returnDTOList);

        mockMvc.perform(post("/api/v1/returns")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createReturnDTOList)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].returnId").value(1))
                .andExpect(jsonPath("$[0].saleId").value(1))
                .andExpect(jsonPath("$[0].quantity").value(1))
                .andExpect(jsonPath("$[0].reason").value("Defective product"))
                .andExpect(jsonPath("$[0].refundAmount").value(50.00));

        verify(returnsService, times(1)).processReturn(any(List.class));
    }

    @Test
    void testGetReturnsBySaleId() throws Exception {
        List<ReturnDTO> returnDTOList = Arrays.asList(returnDTO1, returnDTO2);
        when(returnsService.getReturnsBySaleId(1)).thenReturn(returnDTOList);

        mockMvc.perform(get("/api/v1/returns/sale/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].returnId").value(1))
                .andExpect(jsonPath("$[0].saleId").value(1))
                .andExpect(jsonPath("$[0].reason").value("Defective product"))
                .andExpect(jsonPath("$[1].returnId").value(2))
                .andExpect(jsonPath("$[1].saleId").value(1))
                .andExpect(jsonPath("$[1].reason").value("Wrong size"));

        verify(returnsService, times(1)).getReturnsBySaleId(1);
    }
}