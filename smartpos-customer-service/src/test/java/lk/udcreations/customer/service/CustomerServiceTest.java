package lk.udcreations.customer.service;

import lk.udcreations.common.dto.customer.CustomerDTO;
import lk.udcreations.common.dto.customergroup.CustomerGroupDTO;
import lk.udcreations.common.dto.user.CreatedUpdatedUserDTO;
import lk.udcreations.common.dto.user.UsersDTO;
import lk.udcreations.customer.config.UserServiceClient;
import lk.udcreations.customer.constants.ErrorMessages;
import lk.udcreations.customer.entity.Customer;
import lk.udcreations.customer.entity.CustomerGroup;
import lk.udcreations.customer.exception.NotFoundException;
import lk.udcreations.customer.repository.CustomerGroupRepository;
import lk.udcreations.customer.repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private CustomerGroupRepository customerGroupRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private CustomerService customerService;

    @Mock
    private UserServiceClient userServiceClient;

    private Integer adminUserId;
    private LocalDateTime now;

    Customer customer1;
    Customer customer2;
    private final String adminUsername = "admin";

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

        when(userServiceClient.getUserDetails(adminUsername)).thenReturn(adminUser);

        CreatedUpdatedUserDTO createdUpdatedUserDTO = new CreatedUpdatedUserDTO();
        createdUpdatedUserDTO.setUserId(adminUserId);
        createdUpdatedUserDTO.setUsername("admin");

        CustomerGroupDTO customerGroupDTO = new CustomerGroupDTO();
        customerGroupDTO.setCustomerGroupId(1);
        customerGroupDTO.setName("VIP Customers");
        customerGroupDTO.setDescription("High-value customers");

        CustomerDTO customerDTO = getCustomerDTO(customerGroupDTO, createdUpdatedUserDTO);

        when(modelMapper.map(any(Customer.class), eq(CustomerDTO.class))).thenReturn(customerDTO);
        when(modelMapper.map(any(UsersDTO.class), eq(CreatedUpdatedUserDTO.class))).thenReturn(createdUpdatedUserDTO);
        when(modelMapper.map(any(CustomerGroup.class), eq(CustomerGroupDTO.class))).thenReturn(customerGroupDTO);

        customer1 = new Customer();
        customer1.setCustomerId(1);
        customer1.setCustomerGroupId(1);
        customer1.setUsername("john_doe");
        customer1.setFirstName("John");
        customer1.setLastName("Doe");
        customer1.setEmail("");
        customer1.setPhoneNo1("1234567890");
        customer1.setAddress("123 Main St");
        customer1.setEnabled(true);
        customer1.setDeleted(false);
        customer1.setLocked(false);
        customer1.setCreatedAt(now);
        customer1.setUpdatedAt(now);
        customer1.setCreatedUserId(adminUserId);
        customer1.setUpdatedUserId(adminUserId);

        customer2 = new Customer();
        customer2.setCustomerId(2);
        customer2.setCustomerGroupId(1);
        customer2.setUsername("jane_doe");
        customer2.setFirstName("Jane");
        customer2.setLastName("Doe");
        customer2.setEmail("");
        customer2.setPhoneNo1("0987654321");
        customer2.setAddress("456 Oak St");
        customer2.setEnabled(true);
        customer2.setDeleted(false);
        customer2.setLocked(false);
        customer2.setCreatedAt(now);
        customer2.setUpdatedAt(now);
        customer2.setCreatedUserId(adminUserId);
        customer2.setUpdatedUserId(adminUserId);
    }

    private static CustomerDTO getCustomerDTO(CustomerGroupDTO customerGroupDTO, CreatedUpdatedUserDTO createdUpdatedUserDTO) {
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
        return customerDTO;
    }

    @Test
    void testGetAllCustomer() {

        when(customerRepository.findAll()).thenReturn(Arrays.asList(customer1, customer2));
        when(customerGroupRepository.findById(1)).thenReturn(Optional.of(new CustomerGroup()));

        List<CustomerDTO> result = customerService.getAllCustomer();

        assertEquals(2, result.size());
        verify(customerRepository, times(1)).findAll();
    }

    @Test
    void testGetAllExistCustomers() {

        when(customerRepository.findByDeletedFalse()).thenReturn(Arrays.asList(customer1, customer2));
        when(customerGroupRepository.findById(1)).thenReturn(Optional.of(new CustomerGroup()));

        List<CustomerDTO> result = customerService.getAllExistCustomers();

        assertEquals(2, result.size());
        verify(customerRepository, times(1)).findByDeletedFalse();
    }

    @Test
    void testGetCustomerById_CustomerExists() {

        when(customerRepository.findById(1)).thenReturn(Optional.of(customer1));
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

        when(customerRepository.findByUsername("john_doe")).thenReturn(Optional.of(customer1));
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

        when(customerRepository.findByFirstName("John")).thenReturn(Optional.of(customer1));
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

        when(customerRepository.findByLastName("Doe")).thenReturn(Optional.of(customer1));
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

        when(customerRepository.findByFirstNameAndLastName("John", "Doe")).thenReturn(Optional.of(customer1));
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

        when(customerRepository.findByEmail("john@example.com")).thenReturn(Optional.of(customer1));
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
        Customer newCustomer = new Customer();
        newCustomer.setCustomerGroupId(1);
        newCustomer.setUsername("new_user");
        newCustomer.setFirstName("New");
        newCustomer.setLastName("User");
        newCustomer.setEmail("");
        newCustomer.setPhoneNo1("1234567890");
        newCustomer.setAddress("123 Main St");
        newCustomer.setEnabled(true);
        newCustomer.setDeleted(false);
        newCustomer.setLocked(false);

        Customer savedCustomer = getCustomer("new_user", "New", "User");

        when(customerRepository.findByUsernameAndDeletedFalse("new_user")).thenReturn(Optional.empty());
        when(customerRepository.findByUsernameAndDeletedTrue("new_user")).thenReturn(Optional.empty());
        when(customerRepository.existsByEmail("new@example.com")).thenReturn(false);
        when(customerRepository.save(any(Customer.class))).thenReturn(savedCustomer);
        when(customerGroupRepository.findById(1)).thenReturn(Optional.of(new CustomerGroup()));

        CustomerDTO result = customerService.createCustomer(newCustomer, adminUsername);

        assertNotNull(result);
        verify(customerRepository, times(1)).save(any(Customer.class));
    }

    private Customer getCustomer(String new_user, String New, String User) {
        Customer savedCustomer = new Customer();
        savedCustomer.setCustomerId(1);
        savedCustomer.setCustomerGroupId(1);
        savedCustomer.setUsername(new_user);
        savedCustomer.setFirstName(New);
        savedCustomer.setLastName(User);
        savedCustomer.setEmail("");
        savedCustomer.setPhoneNo1("1234567890");
        savedCustomer.setAddress("123 Main St");
        savedCustomer.setEnabled(true);
        savedCustomer.setDeleted(false);
        savedCustomer.setLocked(false);
        savedCustomer.setCreatedAt(now);
        savedCustomer.setUpdatedAt(now);
        savedCustomer.setCreatedUserId(adminUserId);
        savedCustomer.setUpdatedUserId(adminUserId);
        return savedCustomer;
    }

    @Test
    void testCreateCustomer_ReactivateSoftDeletedCustomer() {
        Customer newCustomer = new Customer();
        newCustomer.setCustomerGroupId(1);
        newCustomer.setUsername("reactivated_user");
        newCustomer.setFirstName("Reactivated");
        newCustomer.setLastName("User");
        newCustomer.setEmail("");
        newCustomer.setPhoneNo1("1234567890");
        newCustomer.setAddress("123 Main St");
        newCustomer.setEnabled(true);
        newCustomer.setDeleted(false);
        newCustomer.setLocked(false);

        Customer softDeletedCustomer = getCustomer();

        Customer reactivatedCustomer = getCustomer("reactivated_user", "Reactivated", "User");

        when(customerRepository.findByUsernameAndDeletedFalse("reactivated_user")).thenReturn(Optional.empty());
        when(customerRepository.findByUsernameAndDeletedTrue("reactivated_user")).thenReturn(Optional.of(softDeletedCustomer));
        when(customerRepository.existsByEmail("reactivated@example.com")).thenReturn(false);
        when(customerRepository.save(any(Customer.class))).thenReturn(reactivatedCustomer);
        when(customerGroupRepository.findById(1)).thenReturn(Optional.of(new CustomerGroup()));

        CustomerDTO result = customerService.createCustomer(newCustomer, "admin");

        assertNotNull(result);
        verify(customerRepository, times(1)).save(any(Customer.class));
    }

    private Customer getCustomer() {
        Customer softDeletedCustomer = new Customer();
        softDeletedCustomer.setCustomerId(1);
        softDeletedCustomer.setCustomerGroupId(1);
        softDeletedCustomer.setUsername("reactivated_user");
        softDeletedCustomer.setFirstName("Old");
        softDeletedCustomer.setLastName("User");
        softDeletedCustomer.setEmail("");
        softDeletedCustomer.setPhoneNo1("0987654321");
        softDeletedCustomer.setAddress("456 Oak St");
        softDeletedCustomer.setEnabled(false);
        softDeletedCustomer.setDeleted(true);
        softDeletedCustomer.setLocked(true);
        softDeletedCustomer.setCreatedAt(now);
        softDeletedCustomer.setUpdatedAt(now);
        softDeletedCustomer.setDeletedAt(now);
        softDeletedCustomer.setCreatedUserId(adminUserId);
        softDeletedCustomer.setUpdatedUserId(adminUserId);
        softDeletedCustomer.setDeletedUserId(adminUserId);
        return softDeletedCustomer;
    }

    @Test
    void testCreateCustomer_CustomerAlreadyExists() {
        Customer existingCustomer = getCustomer("existing_user", "Existing", "User");

        when(customerRepository.findByUsernameAndDeletedFalse("existing_user")).thenReturn(Optional.of(existingCustomer));

        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> customerService.createCustomer(existingCustomer, adminUsername));

        assertTrue(exception.getMessage().contains(ErrorMessages.CUSTOMER_NAME_EXISTS));
        verify(customerRepository, times(1)).findByUsernameAndDeletedFalse("existing_user");
        verify(customerRepository, never()).save(any(Customer.class));
    }

    @Test
    void testCreateCustomer_EmailAlreadyExists() {
        Customer newCustomer = new Customer();
        newCustomer.setCustomerGroupId(1);
        newCustomer.setUsername("new_user");
        newCustomer.setFirstName("New");
        newCustomer.setLastName("User");
        newCustomer.setEmail("existing@example.com");
        newCustomer.setPhoneNo1("1234567890");
        newCustomer.setAddress("123 Main St");
        newCustomer.setEnabled(true);
        newCustomer.setDeleted(false);
        newCustomer.setLocked(false);

        when(customerRepository.findByUsernameAndDeletedFalse("new_user")).thenReturn(Optional.empty());
        when(customerRepository.existsByEmail("existing@example.com")).thenReturn(true);

        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> customerService.createCustomer(newCustomer, adminUsername));

        assertTrue(exception.getMessage().contains(ErrorMessages.EMAIL_EXISTS));
        verify(customerRepository, times(1)).findByUsernameAndDeletedFalse("new_user");
        verify(customerRepository, times(1)).existsByEmail("existing@example.com");
        verify(customerRepository, never()).save(any(Customer.class));
    }

    @Test
    void testUpdateCustomer_CustomerExists() {
        Customer existingCustomer = getCustomer("john_doe", "John", "Doe");

        Customer updatedCustomer = new Customer();
        updatedCustomer.setCustomerGroupId(1);
        updatedCustomer.setUsername("john_doe");
        updatedCustomer.setFirstName("Updated");
        updatedCustomer.setLastName("User");
        updatedCustomer.setEmail("");
        updatedCustomer.setPhoneNo1("0987654321");
        updatedCustomer.setAddress("456 Oak St");
        updatedCustomer.setEnabled(true);
        updatedCustomer.setDeleted(false);
        updatedCustomer.setLocked(false);

        when(customerRepository.findById(1)).thenReturn(Optional.of(existingCustomer));
        when(customerRepository.save(any(Customer.class))).thenReturn(existingCustomer);
        when(customerGroupRepository.findById(1)).thenReturn(Optional.of(new CustomerGroup()));

        CustomerDTO result = customerService.updateCustomer(1, updatedCustomer, adminUsername);

        assertNotNull(result);
        verify(customerRepository, times(1)).findById(1);
        verify(customerRepository, times(1)).save(existingCustomer);
    }

    @Test
    void testUpdateCustomer_CustomerNotFound() {
        Customer updatedCustomer = new Customer();
        updatedCustomer.setCustomerGroupId(1);
        updatedCustomer.setUsername("john_doe");
        updatedCustomer.setFirstName("Updated");
        updatedCustomer.setLastName("User");
        updatedCustomer.setEmail("");
        updatedCustomer.setPhoneNo1("0987654321");
        updatedCustomer.setAddress("456 Oak St");
        updatedCustomer.setEnabled(true);
        updatedCustomer.setDeleted(false);
        updatedCustomer.setLocked(false);

        when(customerRepository.findById(1)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class,
                () -> customerService.updateCustomer(1, updatedCustomer, adminUsername));

        assertTrue(exception.getMessage().contains(ErrorMessages.CUSTOMER_NOT_FOUND));
        verify(customerRepository, times(1)).findById(1);
        verify(customerRepository, never()).save(any(Customer.class));
    }

    @Test
    void testSoftDeleteCustomer_CustomerExists() {
        Customer existingCustomer = customer1;

        when(customerRepository.findById(1)).thenReturn(Optional.of(existingCustomer));

        customerService.softDeleteCustomer(1, "admin");

        assertTrue(existingCustomer.isDeleted());
        verify(customerRepository, times(1)).findById(1);
        verify(customerRepository, times(1)).save(existingCustomer);
    }

    @Test
    void testSoftDeleteCustomer_CustomerNotFound() {
        when(customerRepository.findById(1)).thenReturn(Optional.empty());

        Exception exception = assertThrows(NotFoundException.class,
                () -> customerService.softDeleteCustomer(1, adminUsername));

        assertTrue(exception.getMessage().contains(ErrorMessages.CUSTOMER_NOT_FOUND));
        verify(customerRepository, times(1)).findById(1);
    }

    @Test
    void testDeleteCustomer_CustomerExists() {
        Customer existingCustomer = customer1;

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
