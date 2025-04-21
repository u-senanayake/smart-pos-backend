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
import lk.udcreations.common.dto.customergroup.CustomerGroupDTO;
import lk.udcreations.common.dto.user.CreatedUpdatedUserDTO;
import lk.udcreations.common.dto.user.UsersDTO;
import lk.udcreations.customer.entity.CustomerGroup;
import lk.udcreations.customer.exception.NotFoundException;
import lk.udcreations.customer.repository.CustomerGroupRepository;
import lk.udcreations.customer.security.AuthUtils;

class CustomerGroupServiceTest {

    @Mock
    private CustomerGroupRepository customerGroupRepository;

    @Mock
    private AuthUtils authUtils;
    
    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private CustomerGroupService customerGroupService;

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
        customerGroupDTO.setEnabled(true);
        customerGroupDTO.setDeleted(false);
        customerGroupDTO.setCreatedUser(createdUpdatedUserDTO);
        customerGroupDTO.setUpdatedUser(createdUpdatedUserDTO);
        
        when(authUtils.getLoggedInUser()).thenReturn(adminUser);
        when(authUtils.getUserById(anyInt())).thenReturn(adminUser);
        when(modelMapper.map(any(CustomerGroup.class), eq(CustomerGroupDTO.class))).thenReturn(customerGroupDTO);
        when(modelMapper.map(any(UsersDTO.class), eq(CreatedUpdatedUserDTO.class))).thenReturn(createdUpdatedUserDTO);
    }

    @Test
    void testGetAllCustomerGroups() {
        CustomerGroup group1 = new CustomerGroup(1, "VIP Customers", "High-value customers", true, false,
                now, now, null, adminUserId, adminUserId, null);
        CustomerGroup group2 = new CustomerGroup(2, "Regular Customers", "Normal customers", true, false,
                now, now, null, adminUserId, adminUserId, null);

        when(customerGroupRepository.findAll()).thenReturn(Arrays.asList(group1, group2));

        List<CustomerGroupDTO> result = customerGroupService.getAllCustomerGroups();

        assertEquals(2, result.size());
        verify(customerGroupRepository, times(1)).findAll();
    }
    
    @Test
    void testGetAllExistCustomerGroups() {
        CustomerGroup group1 = new CustomerGroup(1, "VIP Customers", "High-value customers", true, false,
                now, now, null, adminUserId, adminUserId, null);
        CustomerGroup group2 = new CustomerGroup(2, "Regular Customers", "Normal customers", true, false,
                now, now, null, adminUserId, adminUserId, null);

        when(customerGroupRepository.findByDeletedFalse()).thenReturn(Arrays.asList(group1, group2));

        List<CustomerGroupDTO> result = customerGroupService.getAllExistCustomerGroups();

        assertEquals(2, result.size());
        verify(customerGroupRepository, times(1)).findByDeletedFalse();
    }

    @Test
    void testGetCustomerGroupById_CustomerGroupExists() {
        CustomerGroup group = new CustomerGroup(1, "VIP Customers", "High-value customers", true, false,
                now, now, null, adminUserId, adminUserId, null);

        when(customerGroupRepository.findById(1)).thenReturn(Optional.of(group));

        CustomerGroupDTO result = customerGroupService.getCustomerGroupById(1);

        assertNotNull(result);
        verify(customerGroupRepository, times(1)).findById(1);
    }

    @Test
    void testGetCustomerGroupById_CustomerGroupNotFound() {
        when(customerGroupRepository.findById(1)).thenReturn(Optional.empty());

        Exception exception = assertThrows(NotFoundException.class, () -> customerGroupService.getCustomerGroupById(1));

        assertTrue(exception.getMessage().contains(ErrorMessages.CUSTOMERGROUP_NOT_FOUND));
        verify(customerGroupRepository, times(1)).findById(1);
    }

    @Test
    void testCreateCustomerGroup_NewCustomerGroup() {
        CustomerGroup newGroup = new CustomerGroup(null, "Gold Members", "Premium customers", true, false,
                null, null, null, null, null, null);

        CustomerGroup savedGroup = new CustomerGroup(1, "Gold Members", "Premium customers", true, false,
                now, now, null, adminUserId, adminUserId, null);

        when(customerGroupRepository.findByNameAndDeletedFalse("Gold Members")).thenReturn(Optional.empty());
        when(customerGroupRepository.findByNameAndDeletedTrue("Gold Members")).thenReturn(Optional.empty());
        when(customerGroupRepository.save(any(CustomerGroup.class))).thenReturn(savedGroup);

        CustomerGroupDTO result = customerGroupService.createCustomerGroup(newGroup);

        assertNotNull(result);
        verify(customerGroupRepository, times(1)).save(any(CustomerGroup.class));
    }

    @Test
    void testCreateCustomerGroup_ReactivateSoftDeletedCustomerGroup() {
        CustomerGroup newGroup = new CustomerGroup(null, "Gold Members", "Premium customers", true, false,
                null, null, null, null, null, null);

        CustomerGroup softDeletedGroup = new CustomerGroup(1, "Gold Members", "Old description", false, true,
                now, now, now, adminUserId, adminUserId, adminUserId);

        CustomerGroup reactivatedGroup = new CustomerGroup(1, "Gold Members", "Premium customers", true, false,
                now, now, null, adminUserId, adminUserId, null);

        when(customerGroupRepository.findByNameAndDeletedFalse("Gold Members")).thenReturn(Optional.empty());
        when(customerGroupRepository.findByNameAndDeletedTrue("Gold Members")).thenReturn(Optional.of(softDeletedGroup));
        when(customerGroupRepository.save(any(CustomerGroup.class))).thenReturn(reactivatedGroup);

        CustomerGroupDTO result = customerGroupService.createCustomerGroup(newGroup);

        assertNotNull(result);
        verify(customerGroupRepository, times(1)).save(any(CustomerGroup.class));
    }

    @Test
    void testCreateCustomerGroup_CustomerGroupAlreadyExists() {
        CustomerGroup existingGroup = new CustomerGroup(1, "VIP Customers", "High-value customers", true, false,
                now, now, null, adminUserId, adminUserId, null);

        when(customerGroupRepository.findByNameAndDeletedFalse("VIP Customers")).thenReturn(Optional.of(existingGroup));

        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> customerGroupService.createCustomerGroup(existingGroup));

        assertTrue(exception.getMessage().contains(ErrorMessages.CUSTOMERGROUP_NAME_EXISTS));
        verify(customerGroupRepository, times(1)).findByNameAndDeletedFalse("VIP Customers");
        verify(customerGroupRepository, never()).save(any(CustomerGroup.class));
    }

    @Test
    void testUpdateCustomerGroup_CustomerGroupExists() {
        CustomerGroup existingGroup = new CustomerGroup(1, "VIP Customers", "High-value customers", true, false,
                now, now, null, adminUserId, adminUserId, null);

        CustomerGroup updatedGroup = new CustomerGroup(null, "Updated VIP", "Updated description", true, false,
                null, null, null, null, null, null);

        when(customerGroupRepository.findById(1)).thenReturn(Optional.of(existingGroup));
        when(customerGroupRepository.save(any(CustomerGroup.class))).thenReturn(existingGroup);

        CustomerGroupDTO result = customerGroupService.updateCustomerGroup(1, updatedGroup);

        assertNotNull(result);
        verify(customerGroupRepository, times(1)).findById(1);
        verify(customerGroupRepository, times(1)).save(existingGroup);
    }
    
    @Test
    void testUpdateCustomerGroup_CustomerGroupNotFound() {
        CustomerGroup updatedGroup = new CustomerGroup(null, "Updated VIP", "Updated description", true, false,
                null, null, null, null, null, null);

        when(customerGroupRepository.findById(1)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class,
                () -> customerGroupService.updateCustomerGroup(1, updatedGroup));

        assertTrue(exception.getMessage().contains(ErrorMessages.CUSTOMERGROUP_NOT_FOUND));
        verify(customerGroupRepository, times(1)).findById(1);
        verify(customerGroupRepository, never()).save(any(CustomerGroup.class));
    }

    @Test
    void testSoftDeleteCustomerGroup_CustomerGroupExists() {
        CustomerGroup existingGroup = new CustomerGroup(1, "VIP Customers", "High-value customers", true, false,
                now, now, null, adminUserId, adminUserId, null);

        when(customerGroupRepository.findById(1)).thenReturn(Optional.of(existingGroup));

        customerGroupService.softDeleteCustomerGroup(1);

        assertTrue(existingGroup.isDeleted());
        verify(customerGroupRepository, times(1)).findById(1);
        verify(customerGroupRepository, times(1)).save(existingGroup);
    }

    @Test
    void testSoftDeleteCustomerGroup_CustomerGroupNotFound() {
        when(customerGroupRepository.findById(1)).thenReturn(Optional.empty());

        Exception exception = assertThrows(NotFoundException.class,
                () -> customerGroupService.softDeleteCustomerGroup(1));

        assertTrue(exception.getMessage().contains(ErrorMessages.CUSTOMERGROUP_NOT_FOUND));
        verify(customerGroupRepository, times(1)).findById(1);
    }
    
    @Test
    void testDeleteCustomerGroup_CustomerGroupExists() {
        CustomerGroup existingGroup = new CustomerGroup(1, "VIP Customers", "High-value customers", true, false,
                now, now, null, adminUserId, adminUserId, null);

        when(customerGroupRepository.findById(1)).thenReturn(Optional.of(existingGroup));

        customerGroupService.deleteCustomerGroup(1);

        verify(customerGroupRepository, times(1)).findById(1);
        verify(customerGroupRepository, times(1)).delete(existingGroup);
    }
    
    @Test
    void testDeleteCustomerGroup_CustomerGroupNotFound() {
        when(customerGroupRepository.findById(1)).thenReturn(Optional.empty());

        Exception exception = assertThrows(NotFoundException.class,
                () -> customerGroupService.deleteCustomerGroup(1));

        assertTrue(exception.getMessage().contains(ErrorMessages.CUSTOMERGROUP_NOT_FOUND));
        verify(customerGroupRepository, times(1)).findById(1);
        verify(customerGroupRepository, never()).delete(any(CustomerGroup.class));
    }
}