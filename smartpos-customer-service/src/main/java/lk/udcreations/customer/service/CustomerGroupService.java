package lk.udcreations.customer.service;

import lk.udcreations.common.dto.customergroup.CustomerGroupDTO;
import lk.udcreations.common.dto.user.CreatedUpdatedUserDTO;
import lk.udcreations.common.dto.user.UsersDTO;
import lk.udcreations.customer.config.UserServiceClient;
import lk.udcreations.customer.constants.ErrorMessages;
import lk.udcreations.customer.entity.CustomerGroup;
import lk.udcreations.customer.exception.NotFoundException;
import lk.udcreations.customer.repository.CustomerGroupRepository;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CustomerGroupService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CustomerGroupService.class);

    private final CustomerGroupRepository customerGroupRepository;
    private final ModelMapper modelMapper;
    private final UserServiceClient userServiceClient;

    public CustomerGroupService(CustomerGroupRepository customerGroupRepository, ModelMapper modelMapper, UserServiceClient userServiceClient) {
        this.customerGroupRepository = customerGroupRepository;
        this.modelMapper = modelMapper;
        this.userServiceClient = userServiceClient;
    }

    /**
     * Get all customer groups
     */
    public List<CustomerGroupDTO> getAllCustomerGroups() {

        LOGGER.debug("Fetching all customer groups from the database.");

        List<CustomerGroup> customerGroups = customerGroupRepository.findAll();
        if (customerGroups.isEmpty()) {
            LOGGER.warn("No customer groups found in the database.");
        } else {
            LOGGER.info("Successfully fetched {} customer groups from the database.", customerGroups.size());
        }
        return customerGroups.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    /**
     * Get all non-deleted customer groups
     */
    public List<CustomerGroupDTO> getAllExistCustomerGroups() {

        LOGGER.debug("Fetching all non-deleted roles.");

        List<CustomerGroup> customerGroup = customerGroupRepository.findByDeletedFalse();
        if (customerGroup.isEmpty()) {
            LOGGER.warn("No active customer groups found in the system.");
        } else {
            LOGGER.info("Fetched {} active customer groups from the database.", customerGroup.size());
        }
        return customerGroup.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    /**
     * Get a role by ID
     */
    public CustomerGroupDTO getCustomerGroupById(Integer customerGroupId) {

        LOGGER.debug("Fetching customer group with ID: {}", customerGroupId);

        CustomerGroup customerGroup = customerGroupRepository.findById(customerGroupId).orElseThrow(() -> {
            String errorMessage = ErrorMessages.CUSTOMERGROUP_NOT_FOUND + customerGroupId;
            LOGGER.error("Customer group fetch failed. {}", errorMessage);
            return new NotFoundException(errorMessage);
        });

        LOGGER.info("Successfully fetched Customer group with ID: {} and name: '{}'",
                customerGroup.getCustomerGroupId(), customerGroup.getName());
        return convertToDTO(customerGroup);
    }

    /**
     * Create a new customer group
     */
    public CustomerGroupDTO createCustomerGroup(CustomerGroup customerGroup, String loggedInUsername) {

        LOGGER.debug("Attempting to create a new customer group with name: {}", customerGroup.getName());

        UsersDTO loggedInUser = userServiceClient.getUserDetails(loggedInUsername);
        CustomerGroup savedCustomerGroup;

        // Check if the category exists
        Optional<CustomerGroup> existingCustomerGroup = customerGroupRepository
                .findByNameAndDeletedFalse(customerGroup.getName());

        if (existingCustomerGroup.isPresent()) {
            String errorMessage = ErrorMessages.CUSTOMERGROUP_NAME_EXISTS;
            LOGGER.error("Customer group creation failed: {}", errorMessage);
            throw new IllegalArgumentException(errorMessage);

        }

        // Check for a soft-deleted category and reactivate it
        Optional<CustomerGroup> softDeletedCustomerGroup = customerGroupRepository
                .findByNameAndDeletedTrue(customerGroup.getName());
        if (softDeletedCustomerGroup.isPresent()) {
            CustomerGroup reactivatedCustomerGroup = softDeletedCustomerGroup.get();

            reactivatedCustomerGroup.setDescription(customerGroup.getDescription());
            reactivatedCustomerGroup.setEnabled(customerGroup.isEnabled());
            reactivatedCustomerGroup.setUpdatedUser(loggedInUser.getUserId());
            reactivatedCustomerGroup.setDeleted(false);
            reactivatedCustomerGroup.setDeletedAt(null);
            reactivatedCustomerGroup.setDeletedUser(null);

            savedCustomerGroup = customerGroupRepository.save(reactivatedCustomerGroup);
            LOGGER.info("Soft-deleted customer group '{}' reactivated successfully.", customerGroup.getName());
        } else {
            // Otherwise, create a new category
            customerGroup.setUpdatedUser(loggedInUser.getUserId());
            customerGroup.setCreatedUser(loggedInUser.getUserId());
            customerGroup.setDeleted(false);

            savedCustomerGroup = customerGroupRepository.save(customerGroup);
            LOGGER.info("New customer group '{}' created successfully with ID: {}", customerGroup.getName(),
                    customerGroup.getCustomerGroupId());
        }
        return convertToDTO(savedCustomerGroup);
    }

    /**
     * Update a customer group
     */
    public CustomerGroupDTO updateCustomerGroup(Integer customerGroupId, CustomerGroup updatedCustomerGroup, String loggedInUsername) {

        LOGGER.debug("Attempting to update customer group with ID: {}", customerGroupId);

        UsersDTO loggedInUser = userServiceClient.getUserDetails(loggedInUsername);
        return customerGroupRepository.findById(customerGroupId).map(customerGroup -> {
            customerGroup.setName(updatedCustomerGroup.getName());
            customerGroup.setDescription(updatedCustomerGroup.getDescription());
            customerGroup.setEnabled(updatedCustomerGroup.isEnabled());
            customerGroup.setUpdatedUser(loggedInUser.getUserId());

            CustomerGroup savedCustomerGroup = customerGroupRepository.save(customerGroup);
            LOGGER.info("Customer group with ID: {} successfully updated. New name: '{}'", customerGroupId,
                    savedCustomerGroup.getName());
            return convertToDTO(savedCustomerGroup);
        }).orElseThrow(() -> {
            String errorMessage = ErrorMessages.CUSTOMERGROUP_NOT_FOUND + customerGroupId;
            LOGGER.error("Customer group update failed: {}", errorMessage);
            return new RuntimeException(errorMessage);
        });

    }

    /**
     * Delete a customer group by ID (soft delete)
     */
    public void softDeleteCustomerGroup(Integer customerGroupId, String loggedInUsername) {

        LOGGER.debug("Attempting to soft delete customer group with ID: {}", customerGroupId);

        UsersDTO loggedInUser = userServiceClient.getUserDetails(loggedInUsername);

        customerGroupRepository.findById(customerGroupId).ifPresentOrElse(customerGroup -> {
            customerGroup.setUpdatedUser(loggedInUser.getUserId());
            customerGroup.setDeleted(true);
            customerGroup.setDeletedAt(LocalDateTime.now());
            customerGroup.setDeletedUser(loggedInUser.getUserId());
            customerGroupRepository.save(customerGroup);
            LOGGER.info("Customer group with ID: {} has been soft deleted.", customerGroupId);
        }, () -> {
            String errorMessage = ErrorMessages.CUSTOMERGROUP_NOT_FOUND + customerGroupId;
            LOGGER.error("Soft delete failed: {}", errorMessage);
            throw new NotFoundException(errorMessage);
        });
    }

    /**
     * Delete a customer group by ID
     */
    public void deleteCustomerGroup(Integer customerGroupId) {

        LOGGER.debug("Attempting to delete customer group with ID: {}", customerGroupId);

        CustomerGroup customerGroup = customerGroupRepository.findById(customerGroupId).orElseThrow(() -> {
            String errorMessage = ErrorMessages.CUSTOMERGROUP_NOT_FOUND + customerGroupId;
            LOGGER.error("Customer group deletion failed: {}", errorMessage);
            return new NotFoundException(errorMessage);
        });

        customerGroupRepository.delete(customerGroup);
        LOGGER.info("Customer group with ID: {} has been permanently deleted.", customerGroupId);
    }

    private CustomerGroupDTO convertToDTO(CustomerGroup customerGroup) {

        CustomerGroupDTO customerGroupDTO = modelMapper.map(customerGroup, CustomerGroupDTO.class);

        // Set CreatedUserDTO
        UsersDTO createdUser = userServiceClient.getUserById(customerGroup.getCreatedUser());
        CreatedUpdatedUserDTO createdUserDto = modelMapper.map(createdUser, CreatedUpdatedUserDTO.class);
        customerGroupDTO.setCreatedUser(createdUserDto);

        // Set UpdatedUserDTO
        UsersDTO updatedUser = userServiceClient.getUserById(customerGroup.getUpdatedUser());
        CreatedUpdatedUserDTO updatedUserDto = modelMapper.map(updatedUser, CreatedUpdatedUserDTO.class);
        customerGroupDTO.setUpdatedUser(updatedUserDto);

        // Set DeletedUserDTO
        if (customerGroup.isDeleted()) {
            UsersDTO deletedUser = userServiceClient.getUserById(customerGroup.getDeletedUser());
            CreatedUpdatedUserDTO deletedUserDto = modelMapper.map(deletedUser, CreatedUpdatedUserDTO.class);
            customerGroupDTO.setDeletedUser(deletedUserDto);
        }
        return customerGroupDTO;
    }
}
