package lk.udcreations.sale.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import lk.udcreations.common.dto.customer.CustomerDTO;
import lk.udcreations.sale.config.CustomerServiceClient;

class CustomerClientControllerTest {

    @Mock
    private CustomerServiceClient customerServiceClient;

    @InjectMocks
    private CustomerClientController customerClientController;

    private CustomerDTO mockCustomer;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Initialize test data
        mockCustomer = new CustomerDTO();
        mockCustomer.setCustomerId(1);
        mockCustomer.setFirstName("John");
        mockCustomer.setLastName("Doe");
        mockCustomer.setEmail("john.doe@example.com");
        mockCustomer.setPhoneNo1("1234567890");
    }

    @Test
    void testGetCustomerById() {
        // Arrange
        when(customerServiceClient.getCustomerById(1)).thenReturn(mockCustomer);

        // Act
        CustomerDTO result = customerClientController.getCustomerById(1);

        // Assert
        assertEquals(1, result.getCustomerId());
        assertEquals("John", result.getFirstName());
        assertEquals("Doe", result.getLastName());
        assertEquals("john.doe@example.com", result.getEmail());
        assertEquals("1234567890", result.getPhoneNo1());
        verify(customerServiceClient, times(1)).getCustomerById(1);
    }
}
