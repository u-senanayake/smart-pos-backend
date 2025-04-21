package lk.udcreations.customer.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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

import lk.udcreations.customer.constants.ErrorMessages;
import lk.udcreations.common.dto.customer.CustomerDTO;
import lk.udcreations.common.dto.customergroup.CustomerGroupDTO;
import lk.udcreations.common.dto.user.CreatedUpdatedUserDTO;
import lk.udcreations.common.dto.user.UsersDTO;
import lk.udcreations.customer.entity.Customer;
import lk.udcreations.customer.entity.CustomerGroup;
import lk.udcreations.customer.exception.NotFoundException;
import lk.udcreations.customer.repository.CustomerGroupRepository;
import lk.udcreations.customer.repository.CustomerRepository;
import lk.udcreations.customer.security.AuthUtils;

class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private CustomerGroupRepository customerGroupRepository;

    @Mock
    private AuthUtils authUtils;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private CustomerService customerService;

    private Integer adminUserId;
    private LocalDateTime now;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Set up time
        now = LocalDateTime.now();

        // Mock a logged-in admin user
        adminUserId = 999;
        UsersDTO adminUser = new UsersDTO();
        adminUser.setUserId(adminUserId);
        adminUser.setUsername("admin");

        CreatedUpdatedUserDTO createdUpdatedUserDTO = new CreatedUpdatedUserDTO();
        createdUpdatedUserDTO.setUserId(adminUserId);
        createdUpdatedUserDTO.setUsername("admin");

        CustomerGroupDTO customerGroupDTO = new CustomerGroupDTO();
        customerGroupDTO.setCustomerGroupId(1);
        customerGroupDTO.setName("VIP Customers");
        customerGroupDTO.setDescription("High-value customers");

        CustomerDTO customerDTO = new CustomerDTO();
        customerDTO.setCustomerId(1);
        customerDTO.setUsername("john_doe");
        customerDTO.setFirstName("John");
        customerDTO.setLastName("Doe");
        customerDTO.setEmail("john@example.com");
        customerDTO.setPhoneNo1("1234567890");
        customerDTO.setAddress("123 Main St");
        customerDTO.setEnabled(true);
        customerDTO.setLocked(false);
        customerDTO.setDeleted(false);
        customerDTO.setCustomerGroup(customerGroupDTO);
        customerDTO.setCreatedUser(createdUpdatedUserDTO);
        customerDTO.setUpdatedUser(createdUpdatedUserDTO);

        when(authUtils.getLoggedInUser()).thenReturn(adminUser);
        when(authUtils.getUserById(anyInt())).thenReturn(adminUser);
        when(modelMapper.map(any(Customer.class), eq(CustomerDTO.class))).thenReturn(customerDTO);
        when(modelMapper.map(any(UsersDTO.class), eq(CreatedUpdatedUserDTO.class))).thenReturn(createdUpdatedUserDTO);
        when(modelMapper.map(any(CustomerGroup.class), eq(CustomerGroupDTO.class))).thenReturn(customerGroupDTO);
    }

    @Test
    void testGetAllCustomer() {
        Customer customer1 = new Customer(1, 1, "john_doe", "John", "Doe", "john@example.com", "1234567890",
                "123 Main St", true, false, false, now, now, null, adminUserId, adminUserId, null);
        Customer customer2 = new Customer(2, 1, "jane_doe", "Jane", "Doe", "jane@example.com", "0987654321",
                "456 Oak St", true, false, false, now, now, null, adminUserId, adminUserId, null);

        when(customerRepository.findAll()).thenReturn(Arrays.asList(customer1, customer2));
        when(customerGroupRepository.findById(1)).thenReturn(Optional.of(new CustomerGroup()));

        List<CustomerDTO> result = customerService.getAllCustomer();

        assertEquals(2, result.size());
        verify(customerRepository, times(1)).findAll();
    }

    @Test
    void testGetAllExistCustomers() {
        Customer customer1 = new Customer(1, 1, "john_doe", "John", "Doe", "john@example.com", "1234567890",
                "123 Main St", true, false, false, now, now, null, adminUserId, adminUserId, null);
        Customer customer2 = new Customer(2, 1, "jane_doe", "Jane", "Doe", "jane@example.com", "0987654321",
                "456 Oak St", true, false, false, now, now, null, adminUserId, adminUserId, null);

        when(customerRepository.findByDeletedFalse()).thenReturn(Arrays.asList(customer1, customer2));
        when(customerGroupRepository.findById(1)).thenReturn(Optional.of(new CustomerGroup()));

        List<CustomerDTO> result = customerService.getAllExistCustomers();

        assertEquals(2, result.size());
        verify(customerRepository, times(1)).findByDeletedFalse();
    }

    @Test
    void testGetCustomerById_CustomerExists() {
        Customer customer = new Customer(1, 1, "john_doe", "John", "Doe", "john@example.com", "1234567890",
                "123 Main St", true, false, false, now, now, null, adminUserId, adminUserId, null);

        when(customerRepository.findById(1)).thenReturn(Optional.of(customer));
        when(customerGroupRepository.findById(1)).thenReturn(Optional.of(new CustomerGroup()));

        CustomerDTO result = customerService.getCustomerById(1);

        assertNotNull(result);
        verify(customerRepository, times(1)).findById(1);
    }

    @Test
    void testGetCustomerById_CustomerNotFound() {
        when(customerRepository.findById(1)).thenReturn(Optional.empty());

        Exception exception = assertThrows(NotFoundException.class, () -> customerService.getCustomerById(1));

        assertTrue(exception.getMessage().contains(ErrorMessages.CUSTOMER_NOT_FOUND));
        verify(customerRepository, times(1)).findById(1);
    }

    @Test
    void testGetCustomerByUserName_CustomerExists() {
        Customer customer = new Customer(1, 1, "john_doe", "John", "Doe", "john@example.com", "1234567890",
                "123 Main St", true, false, false, now, now, null, adminUserId, adminUserId, null);

        when(customerRepository.findByUsername("john_doe")).thenReturn(Optional.of(customer));
        when(customerGroupRepository.findById(1)).thenReturn(Optional.of(new CustomerGroup()));

        CustomerDTO result = customerService.getCustomerByUserName("john_doe");

        assertNotNull(result);
        verify(customerRepository, times(1)).findByUsername("john_doe");
    }

    @Test
    void testGetCustomerByUserName_CustomerNotFound() {
        when(customerRepository.findByUsername("john_doe")).thenReturn(Optional.empty());

        Exception exception = assertThrows(NotFoundException.class, () -> customerService.getCustomerByUserName("john_doe"));

        assertTrue(exception.getMessage().contains(ErrorMessages.CUSTOMER_NOT_FOUND));
        verify(customerRepository, times(1)).findByUsername("john_doe");
    }

    @Test
    void testGetCustomerByFirstName_CustomerExists() {
        Customer customer = new Customer(1, 1, "john_doe", "John", "Doe", "john@example.com", "1234567890",
                "123 Main St", true, false, false, now, now, null, adminUserId, adminUserId, null);

        when(customerRepository.findByFirstName("John")).thenReturn(Optional.of(customer));
        when(customerGroupRepository.findById(1)).thenReturn(Optional.of(new CustomerGroup()));

        CustomerDTO result = customerService.getCustomerByFirstName("John");

        assertNotNull(result);
        verify(customerRepository, times(1)).findByFirstName("John");
    }

    @Test
    void testGetCustomerByFirstName_CustomerNotFound() {
        when(customerRepository.findByFirstName("John")).thenReturn(Optional.empty());

        Exception exception = assertThrows(NotFoundException.class, () -> customerService.getCustomerByFirstName("John"));

        assertTrue(exception.getMessage().contains(ErrorMessages.CUSTOMER_NOT_FOUND));
        verify(customerRepository, times(1)).findByFirstName("John");
    }

    @Test
    void testGetCustomerByLastName_CustomerExists() {
        Customer customer = new Customer(1, 1, "john_doe", "John", "Doe", "john@example.com", "1234567890",
                "123 Main St", true, false, false, now, now, null, adminUserId, adminUserId, null);

        when(customerRepository.findByLastName("Doe")).thenReturn(Optional.of(customer));
        when(customerGroupRepository.findById(1)).thenReturn(Optional.of(new CustomerGroup()));

        CustomerDTO result = customerService.getCustomerByLastName("Doe");

        assertNotNull(result);
        verify(customerRepository, times(1)).findByLastName("Doe");
    }

    @Test
    void testGetCustomerByLastName_CustomerNotFound() {
        when(customerRepository.findByLastName("Doe")).thenReturn(Optional.empty());

        Exception exception = assertThrows(NotFoundException.class, () -> customerService.getCustomerByLastName("Doe"));

        assertTrue(exception.getMessage().contains(ErrorMessages.CUSTOMER_NOT_FOUND));
        verify(customerRepository, times(1)).findByLastName("Doe");
    }

    @Test
    void testGetCustomerByFirstNameAndLastName_CustomerExists() {
        Customer customer = new Customer(1, 1, "john_doe", "John", "Doe", "john@example.com", "1234567890",
                "123 Main St", true, false, false, now, now, null, adminUserId, adminUserId, null);

        when(customerRepository.findByFirstNameAndLastName("John", "Doe")).thenReturn(Optional.of(customer));
        when(customerGroupRepository.findById(1)).thenReturn(Optional.of(new CustomerGroup()));

        CustomerDTO result = customerService.getCustomerByFirstNameAndLastName("John", "Doe");

        assertNotNull(result);
        verify(customerRepository, times(1)).findByFirstNameAndLastName("John", "Doe");
    }

    @Test
    void testGetCustomerByFirstNameAndLastName_CustomerNotFound() {
        when(customerRepository.findByFirstNameAndLastName("John", "Doe")).thenReturn(Optional.empty());

        Exception exception = assertThrows(NotFoundException.class, () -> customerService.getCustomerByFirstNameAndLastName("John", "Doe"));

        assertTrue(exception.getMessage().contains(ErrorMessages.CUSTOMER_NOT_FOUND));
        verify(customerRepository, times(1)).findByFirstNameAndLastName("John", "Doe");
    }

    @Test
    void testGetUserByEmail_CustomerExists() {
        Customer customer = new Customer(1, 1, "john_doe", "John", "Doe", "john@example.com", "1234567890",
                "123 Main St", true, false, false, now, now, null, adminUserId, adminUserId, null);

        when(customerRepository.findByEmail("john@example.com")).thenReturn(Optional.of(customer));
        when(customerGroupRepository.findById(1)).thenReturn(Optional.of(new CustomerGroup()));

        CustomerDTO result = customerService.getUserByEmail("john@example.com");

        assertNotNull(result);
        verify(customerRepository, times(1)).findByEmail("john@example.com");
    }

    @Test
    void testGetUserByEmail_CustomerNotFound() {
        when(customerRepository.findByEmail("john@example.com")).thenReturn(Optional.empty());

        Exception exception = assertThrows(NotFoundException.class, () -> customerService.getUserByEmail("john@example.com"));

        assertTrue(exception.getMessage().contains(ErrorMessages.CUSTOMER_NOT_FOUND));
        verify(customerRepository, times(1)).findByEmail("john@example.com");
    }

    @Test
    void testCreateCustomer_NewCustomer() {
        Customer newCustomer = new Customer(null, 1, "new_user", "New", "User", "new@example.com", "1234567890",
                "123 Main St", true, false, false, null, null, null, null, null, null);

        Customer savedCustomer = new Customer(1, 1, "new_user", "New", "User", "new@example.com", "1234567890",
                "123 Main St", true, false, false, now, now, null, adminUserId, adminUserId, null);

        when(customerRepository.findByUsernameAndDeletedFalse("new_user")).thenReturn(Optional.empty());
        when(customerRepository.findByUsernameAndDeletedTrue("new_user")).thenReturn(Optional.empty());
        when(customerRepository.existsByEmail("new@example.com")).thenReturn(false);
        when(customerRepository.save(any(Customer.class))).thenReturn(savedCustomer);
        when(customerGroupRepository.findById(1)).thenReturn(Optional.of(new CustomerGroup()));

        CustomerDTO result = customerService.createCustomer(newCustomer);

        assertNotNull(result);
        verify(customerRepository, times(1)).save(any(Customer.class));
    }

    @Test
    void testCreateCustomer_ReactivateSoftDeletedCustomer() {
        Customer newCustomer = new Customer(null, 1, "reactivated_user", "Reactivated", "User", "reactivated@example.com", "1234567890",
                "123 Main St", true, false, false, null, null, null, null, null, null);

        Customer softDeletedCustomer = new Customer(1, 1, "reactivated_user", "Old", "User", "old@example.com", "0987654321",
                "456 Oak St", false, true, true, now, now, now, adminUserId, adminUserId, adminUserId);

        Customer reactivatedCustomer = new Customer(1, 1, "reactivated_user", "Reactivated", "User", "reactivated@example.com", "1234567890",
                "123 Main St", true, false, false, now, now, null, adminUserId, adminUserId, null);

        when(customerRepository.findByUsernameAndDeletedFalse("reactivated_user")).thenReturn(Optional.empty());
        when(customerRepository.findByUsernameAndDeletedTrue("reactivated_user")).thenReturn(Optional.of(softDeletedCustomer));
        when(customerRepository.existsByEmail("reactivated@example.com")).thenReturn(false);
        when(customerRepository.save(any(Customer.class))).thenReturn(reactivatedCustomer);
        when(customerGroupRepository.findById(1)).thenReturn(Optional.of(new CustomerGroup()));

        CustomerDTO result = customerService.createCustomer(newCustomer);

        assertNotNull(result);
        verify(customerRepository, times(1)).save(any(Customer.class));
    }

    @Test
    void testCreateCustomer_CustomerAlreadyExists() {
        Customer existingCustomer = new Customer(1, 1, "existing_user", "Existing", "User", "existing@example.com", "1234567890",
                "123 Main St", true, false, false, now, now, null, adminUserId, adminUserId, null);

        when(customerRepository.findByUsernameAndDeletedFalse("existing_user")).thenReturn(Optional.of(existingCustomer));

        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> customerService.createCustomer(existingCustomer));

        assertTrue(exception.getMessage().contains(ErrorMessages.CUSTOMER_NAME_EXISTS));
        verify(customerRepository, times(1)).findByUsernameAndDeletedFalse("existing_user");
        verify(customerRepository, never()).save(any(Customer.class));
    }

    @Test
    void testCreateCustomer_EmailAlreadyExists() {
        Customer newCustomer = new Customer(null, 1, "new_user", "New", "User", "existing@example.com", "1234567890",
                "123 Main St", true, false, false, null, null, null, null, null, null);

        when(customerRepository.findByUsernameAndDeletedFalse("new_user")).thenReturn(Optional.empty());
        when(customerRepository.existsByEmail("existing@example.com")).thenReturn(true);

        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> customerService.createCustomer(newCustomer));

        assertTrue(exception.getMessage().contains(ErrorMessages.EMAIL_EXISTS));
        verify(customerRepository, times(1)).findByUsernameAndDeletedFalse("new_user");
        verify(customerRepository, times(1)).existsByEmail("existing@example.com");
        verify(customerRepository, never()).save(any(Customer.class));
    }

    @Test
    void testUpdateCustomer_CustomerExists() {
        Customer existingCustomer = new Customer(1, 1, "john_doe", "John", "Doe", "john@example.com", "1234567890",
                "123 Main St", true, false, false, now, now, null, adminUserId, adminUserId, null);

        Customer updatedCustomer = new Customer(null, 1, "john_doe", "Updated", "User", "updated@example.com", "0987654321",
                "456 Oak St", true, false, false, null, null, null, null, null, null);

        when(customerRepository.findById(1)).thenReturn(Optional.of(existingCustomer));
        when(customerRepository.save(any(Customer.class))).thenReturn(existingCustomer);
        when(customerGroupRepository.findById(1)).thenReturn(Optional.of(new CustomerGroup()));

        CustomerDTO result = customerService.updateCustomer(1, updatedCustomer);

        assertNotNull(result);
        verify(customerRepository, times(1)).findById(1);
        verify(customerRepository, times(1)).save(existingCustomer);
    }

    @Test
    void testUpdateCustomer_CustomerNotFound() {
        Customer updatedCustomer = new Customer(null, 1, "john_doe", "Updated", "User", "updated@example.com", "0987654321",
                "456 Oak St", true, false, false, null, null, null, null, null, null);

        when(customerRepository.findById(1)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class,
                () -> customerService.updateCustomer(1, updatedCustomer));

        assertTrue(exception.getMessage().contains(ErrorMessages.CUSTOMER_NOT_FOUND));
        verify(customerRepository, times(1)).findById(1);
        verify(customerRepository, never()).save(any(Customer.class));
    }

    @Test
    void testSoftDeleteCustomer_CustomerExists() {
        Customer existingCustomer = new Customer(1, 1, "john_doe", "John", "Doe", "john@example.com", "1234567890",
                "123 Main St", true, false, false, now, now, null, adminUserId, adminUserId, null);

        when(customerRepository.findById(1)).thenReturn(Optional.of(existingCustomer));

        customerService.softDeleteCustomer(1);

        assertTrue(existingCustomer.isDeleted());
        verify(customerRepository, times(1)).findById(1);
        verify(customerRepository, times(1)).save(existingCustomer);
    }

    @Test
    void testSoftDeleteCustomer_CustomerNotFound() {
        when(customerRepository.findById(1)).thenReturn(Optional.empty());

        Exception exception = assertThrows(NotFoundException.class,
                () -> customerService.softDeleteCustomer(1));

        assertTrue(exception.getMessage().contains(ErrorMessages.CUSTOMER_NOT_FOUND));
        verify(customerRepository, times(1)).findById(1);
    }

    @Test
    void testDeleteCustomer_CustomerExists() {
        Customer existingCustomer = new Customer(1, 1, "john_doe", "John", "Doe", "john@example.com", "1234567890",
                "123 Main St", true, false, false, now, now, null, adminUserId, adminUserId, null);

        when(customerRepository.findById(1)).thenReturn(Optional.of(existingCustomer));

        customerService.deleteCustomer(1);

        verify(customerRepository, times(1)).findById(1);
        verify(customerRepository, times(1)).delete(existingCustomer);
    }

    @Test
    void testDeleteCustomer_CustomerNotFound() {
        when(customerRepository.findById(1)).thenReturn(Optional.empty());

        Exception exception = assertThrows(NotFoundException.class,
                () -> customerService.deleteCustomer(1));

        assertTrue(exception.getMessage().contains(ErrorMessages.CUSTOMER_NOT_FOUND));
        verify(customerRepository, times(1)).findById(1);
        verify(customerRepository, never()).delete(any(Customer.class));
    }
}
