package lk.udcreations.customer.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lk.udcreations.common.dto.customergroup.CustomerGroupDTO;
import lk.udcreations.customer.entity.CustomerGroup;
import lk.udcreations.customer.service.CustomerGroupService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CustomergroupControllerTest {

    @Mock
    private CustomerGroupService customerGroupService;

    @InjectMocks
    private CustomerGroupController customergroupController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    CustomerGroupDTO group1;
    CustomerGroupDTO group2;
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(customergroupController).build();
        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules(); // Register Java 8 date/time support

        group1 = new CustomerGroupDTO();
        group1.setCustomerGroupId(1);
        group1.setName("VIP Customers");
        group1.setDescription("High-value customers");
        group1.setEnabled(true);
        group1.setDeleted(false);
        group1.setCreatedAt(LocalDateTime.now());


        group2 = new CustomerGroupDTO();
        group2.setCustomerGroupId(2);
        group2.setName("Regular Customers");
        group2.setDescription("Normal customers");
        group2.setEnabled(true);
        group2.setDeleted(false);
        group2.setCreatedAt(LocalDateTime.now());
    }

    @Test
    void testGetAllCustomerGroups() throws Exception {

        List<CustomerGroupDTO> customerGroups = Arrays.asList(group1, group2);

        when(customerGroupService.getAllCustomerGroups()).thenReturn(customerGroups);

        mockMvc.perform(get("/api/v1/customergroup/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].name").value("VIP Customers"))
                .andExpect(jsonPath("$[1].name").value("Regular Customers"));

        verify(customerGroupService, times(1)).getAllCustomerGroups();
    }

    @Test
    void testGetAllExistCustomerGroups() throws Exception {

        List<CustomerGroupDTO> customerGroups = Arrays.asList(group1, group2);

        when(customerGroupService.getAllExistCustomerGroups()).thenReturn(customerGroups);

        mockMvc.perform(get("/api/v1/customergroup"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].name").value("VIP Customers"))
                .andExpect(jsonPath("$[1].name").value("Regular Customers"));

        verify(customerGroupService, times(1)).getAllExistCustomerGroups();
    }

    @Test
    void testGetCustomerGroupById() throws Exception {

        when(customerGroupService.getCustomerGroupById(1)).thenReturn(group1);

        mockMvc.perform(get("/api/v1/customergroup/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("VIP Customers"));

        verify(customerGroupService, times(1)).getCustomerGroupById(1);
    }

    @Test
    void testCreateCustomerGroup() throws Exception {

        CustomerGroup newGroup = new CustomerGroup();
        newGroup.setName("Gold Members");
        newGroup.setDescription("Premium customers");
        newGroup.setEnabled(true);
        newGroup.setDeleted(false);

        CustomerGroupDTO createdGroup = new CustomerGroupDTO();
        createdGroup.setCustomerGroupId(1);
        createdGroup.setName("Gold Members");
        createdGroup.setDescription("Premium customers");
        createdGroup.setEnabled(true);
        createdGroup.setDeleted(false);
        createdGroup.setCreatedAt(LocalDateTime.now());

        when(customerGroupService.createCustomerGroup(any(CustomerGroup.class), any())).thenReturn(createdGroup);

        mockMvc.perform(post("/api/v1/customergroup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newGroup))).andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Gold Members"));

        verify(customerGroupService, times(1)).createCustomerGroup(any(CustomerGroup.class), any());
    }

    @Test
    void testUpdateCustomerGroup() throws Exception {
        CustomerGroup updatedGroup = new CustomerGroup();
        updatedGroup.setName("Updated VIP");
        updatedGroup.setDescription("Updated description");
        updatedGroup.setEnabled(true);
        updatedGroup.setDeleted(false);

        CustomerGroupDTO updatedGroupDTO = new CustomerGroupDTO();
        updatedGroupDTO.setCustomerGroupId(1);
        updatedGroupDTO.setName("Updated VIP");
        updatedGroupDTO.setDescription("Updated description");
        updatedGroupDTO.setEnabled(true);
        updatedGroupDTO.setDeleted(false);
        updatedGroupDTO.setCreatedAt(LocalDateTime.now());

        when(customerGroupService.updateCustomerGroup(eq(1), any(CustomerGroup.class), any())).thenReturn(updatedGroupDTO);

        mockMvc.perform(put("/api/v1/customergroup/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedGroup))).andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated VIP"));

        verify(customerGroupService, times(1)).updateCustomerGroup(eq(1), any(CustomerGroup.class), any());
    }

   /* @Test
    void testDeleteCustomerGroup() throws Exception {
        doNothing().when(customerGroupService).softDeleteCustomerGroup(1, any());

        mockMvc.perform(delete("/api/v1/customergroup/1")).andExpect(status().isNoContent());

        verify(customerGroupService, times(1)).softDeleteCustomerGroup(1, any());
    }*/
}
