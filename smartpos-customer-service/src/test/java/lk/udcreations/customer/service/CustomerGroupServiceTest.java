package lk.udcreations.customer.service;

import lk.udcreations.common.dto.customergroup.CustomerGroupDTO;
import lk.udcreations.common.dto.user.CreatedUpdatedUserDTO;
import lk.udcreations.common.dto.user.UsersDTO;
import lk.udcreations.customer.config.UserServiceClient;
import lk.udcreations.customer.constants.ErrorMessages;
import lk.udcreations.customer.entity.CustomerGroup;
import lk.udcreations.customer.exception.NotFoundException;
import lk.udcreations.customer.repository.CustomerGroupRepository;
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

class CustomerGroupServiceTest {

    @Mock
    private CustomerGroupRepository customerGroupRepository;

    @Mock
    private UserServiceClient userServiceClient;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private CustomerGroupService customerGroupService;

    private Integer adminUserId;
    private LocalDateTime now;
    private final String adminUsername = "admin";

    CustomerGroup group1;
    CustomerGroup group2;
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Set up time
        now = LocalDateTime.now();

        // Mock a logged-in admin user
        adminUserId = 999;
        UsersDTO adminUser = new UsersDTO();
        adminUser.setUserId(adminUserId);
        adminUser.setUsername(adminUsername);
        when(userServiceClient.getUserDetails(adminUsername)).thenReturn(adminUser);

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

        group1 = new CustomerGroup();

        group1.setCustomerGroupId(1);
        group1.setName("VIP Customers");
        group1.setDescription("High-value customers");
        group1.setEnabled(true);
        group1.setDeleted(false);

        group2 = new CustomerGroup();
        group2.setCustomerGroupId(2);
        group2.setName("Regular Customers");
        group2.setDescription("Normal customers");
        group2.setEnabled(true);
        group2.setDeleted(false);

        when(modelMapper.map(any(CustomerGroup.class), eq(CustomerGroupDTO.class))).thenReturn(customerGroupDTO);
        when(modelMapper.map(any(UsersDTO.class), eq(CreatedUpdatedUserDTO.class))).thenReturn(createdUpdatedUserDTO);
    }

    @Test
    void testGetAllCustomerGroups() {
        when(customerGroupRepository.findAll()).thenReturn(Arrays.asList(group1, group2));
        List<CustomerGroupDTO> result = customerGroupService.getAllCustomerGroups();

        assertEquals(2, result.size());
        verify(customerGroupRepository, times(1)).findAll();
    }
    
    @Test
    void testGetAllExistCustomerGroups() {
        when(customerGroupRepository.findByDeletedFalse()).thenReturn(Arrays.asList(group1, group2));

        List<CustomerGroupDTO> result = customerGroupService.getAllExistCustomerGroups();

        assertEquals(2, result.size());
        verify(customerGroupRepository, times(1)).findByDeletedFalse();
    }

    @Test
    void testGetCustomerGroupById_CustomerGroupExists() {

        when(customerGroupRepository.findById(1)).thenReturn(Optional.of(group1));

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
        CustomerGroup newGroup = new CustomerGroup();
        newGroup.setName("Gold Members");
        newGroup.setEnabled(true);
        newGroup.setDeleted(false);

        CustomerGroup savedGroup = new CustomerGroup();
        savedGroup.setName("Gold Members");
        savedGroup.setEnabled(true);
        savedGroup.setDeleted(false);

        when(customerGroupRepository.findByNameAndDeletedFalse("Gold Members")).thenReturn(Optional.empty());
        when(customerGroupRepository.findByNameAndDeletedTrue("Gold Members")).thenReturn(Optional.empty());
        when(customerGroupRepository.save(any(CustomerGroup.class))).thenReturn(savedGroup);

        CustomerGroupDTO result = customerGroupService.createCustomerGroup(newGroup, adminUsername);

        assertNotNull(result);
        verify(customerGroupRepository, times(1)).save(any(CustomerGroup.class));
    }

    @Test
    void testCreateCustomerGroup_ReactivateSoftDeletedCustomerGroup() {
        CustomerGroup newGroup = new CustomerGroup();
        newGroup.setName("Gold Members");
        newGroup.setDescription("Premium customers");
        newGroup.setEnabled(true);
        newGroup.setDeleted(false);

        CustomerGroup softDeletedGroup = getCustomerGroup();


        CustomerGroup reactivatedGroup = getGroup();

        when(customerGroupRepository.findByNameAndDeletedFalse("Gold Members")).thenReturn(Optional.empty());
        when(customerGroupRepository.findByNameAndDeletedTrue("Gold Members")).thenReturn(Optional.of(softDeletedGroup));
        when(customerGroupRepository.save(any(CustomerGroup.class))).thenReturn(reactivatedGroup);

        CustomerGroupDTO result = customerGroupService.createCustomerGroup(newGroup, adminUsername);

        assertNotNull(result);
        verify(customerGroupRepository, times(1)).save(any(CustomerGroup.class));
    }

    private CustomerGroup getGroup() {
        CustomerGroup reactivatedGroup = new CustomerGroup();
        reactivatedGroup.setCustomerGroupId(1);
        reactivatedGroup.setName("Gold Members");
        reactivatedGroup.setDescription("Premium customers");
        reactivatedGroup.setEnabled(true);
        reactivatedGroup.setDeleted(false);
        reactivatedGroup.setCreatedAt(now);
        reactivatedGroup.setUpdatedAt(now);
        reactivatedGroup.setDeletedAt(null);
        reactivatedGroup.setCreatedUser(adminUserId);
        reactivatedGroup.setUpdatedUser(adminUserId);
        reactivatedGroup.setDeletedUser(null);
        return reactivatedGroup;
    }

    private CustomerGroup getCustomerGroup() {
        CustomerGroup softDeletedGroup = new CustomerGroup();
        softDeletedGroup.setCustomerGroupId(1);
        softDeletedGroup.setName("Gold Members");
        softDeletedGroup.setDescription("Old description");
        softDeletedGroup.setEnabled(false);
        softDeletedGroup.setDeleted(true);
        softDeletedGroup.setCreatedAt(now);
        softDeletedGroup.setUpdatedAt(now);
        softDeletedGroup.setDeletedAt(now);
        softDeletedGroup.setCreatedUser(adminUserId);
        softDeletedGroup.setUpdatedUser(adminUserId);
        softDeletedGroup.setDeletedUser(adminUserId);
        return softDeletedGroup;
    }

    @Test
    void testCreateCustomerGroup_CustomerGroupAlreadyExists() {
        CustomerGroup existingGroup = new CustomerGroup();
        existingGroup.setCustomerGroupId(1);
        existingGroup.setName("VIP Customers");
        existingGroup.setDescription("High-value customers");
        existingGroup.setEnabled(true);
        existingGroup.setDeleted(false);
        existingGroup.setCreatedAt(now);
        existingGroup.setUpdatedAt(now);
        existingGroup.setCreatedUser(adminUserId);
        existingGroup.setUpdatedUser(adminUserId);

        when(customerGroupRepository.findByNameAndDeletedFalse("VIP Customers")).thenReturn(Optional.of(existingGroup));

        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> customerGroupService.createCustomerGroup(existingGroup, adminUsername));

        assertTrue(exception.getMessage().contains(ErrorMessages.CUSTOMERGROUP_NAME_EXISTS));
        verify(customerGroupRepository, times(1)).findByNameAndDeletedFalse("VIP Customers");
        verify(customerGroupRepository, never()).save(any(CustomerGroup.class));
    }

    @Test
    void testUpdateCustomerGroup_CustomerGroupExists() {
        CustomerGroup existingGroup = group1;

        CustomerGroup updatedGroup = new CustomerGroup();
        updatedGroup.setName("Updated VIP");
        updatedGroup.setDescription("Updated description");
        updatedGroup.setEnabled(true);
        updatedGroup.setDeleted(false);

        when(customerGroupRepository.findById(1)).thenReturn(Optional.of(existingGroup));
        when(customerGroupRepository.save(any(CustomerGroup.class))).thenReturn(existingGroup);

        CustomerGroupDTO result = customerGroupService.updateCustomerGroup(1, updatedGroup, adminUsername);

        assertNotNull(result);
        verify(customerGroupRepository, times(1)).findById(1);
        verify(customerGroupRepository, times(1)).save(existingGroup);
    }
    
    @Test
    void testUpdateCustomerGroup_CustomerGroupNotFound() {
        CustomerGroup updatedGroup = new CustomerGroup();
        updatedGroup.setName("Updated VIP");
        updatedGroup.setDescription("Updated description");
        updatedGroup.setEnabled(true);
        updatedGroup.setDeleted(false);

        when(customerGroupRepository.findById(1)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class,
                () -> customerGroupService.updateCustomerGroup(1, updatedGroup, adminUsername));

        assertTrue(exception.getMessage().contains(ErrorMessages.CUSTOMERGROUP_NOT_FOUND));
        verify(customerGroupRepository, times(1)).findById(1);
        verify(customerGroupRepository, never()).save(any(CustomerGroup.class));
    }

    @Test
    void testSoftDeleteCustomerGroup_CustomerGroupExists() {
        CustomerGroup existingGroup = group1;

        when(customerGroupRepository.findById(1)).thenReturn(Optional.of(existingGroup));

        customerGroupService.softDeleteCustomerGroup(1, adminUsername);

        assertTrue(existingGroup.isDeleted());
        verify(customerGroupRepository, times(1)).findById(1);
        verify(customerGroupRepository, times(1)).save(existingGroup);
    }

    @Test
    void testSoftDeleteCustomerGroup_CustomerGroupNotFound() {
        when(customerGroupRepository.findById(1)).thenReturn(Optional.empty());

        Exception exception = assertThrows(NotFoundException.class,
                () -> customerGroupService.softDeleteCustomerGroup(1, adminUsername));

        assertTrue(exception.getMessage().contains(ErrorMessages.CUSTOMERGROUP_NOT_FOUND));
        verify(customerGroupRepository, times(1)).findById(1);
    }
    
    @Test
    void testDeleteCustomerGroup_CustomerGroupExists() {
        CustomerGroup existingGroup = group1;

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