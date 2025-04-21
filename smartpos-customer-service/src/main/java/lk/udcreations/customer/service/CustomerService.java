package lk.udcreations.customer.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lk.udcreations.customer.constants.ErrorMessages;
import lk.udcreations.common.dto.customer.CustomerDTO;
import lk.udcreations.common.dto.customergroup.CustomerGroupDTO;
import lk.udcreations.common.dto.user.CreatedUpdatedUserDTO;
import lk.udcreations.common.dto.user.UsersDTO;
import lk.udcreations.customer.exception.NotFoundException;
import lk.udcreations.customer.entity.Customer;
import lk.udcreations.customer.entity.CustomerGroup;
import lk.udcreations.customer.repository.CustomerGroupRepository;
import lk.udcreations.customer.repository.CustomerRepository;
import lk.udcreations.customer.security.AuthUtils;

@Service
public class CustomerService {

	private static final Logger LOGGER = LoggerFactory.getLogger(CustomerService.class);

	private final CustomerRepository customerRepository;
	private final CustomerGroupRepository customerGroupRepository;
	private final AuthUtils authUtils;
	private final ModelMapper modelMapper;

	public CustomerService(CustomerRepository customerRepository, CustomerGroupRepository customerGroupRepository,
			AuthUtils authUtils, ModelMapper modelMapper) {
		super();
		this.customerRepository = customerRepository;
		this.customerGroupRepository = customerGroupRepository;
		this.authUtils = authUtils;
		this.modelMapper = modelMapper;
	}

	/**
	 * Get all customer.
	 */
	public List<CustomerDTO> getAllCustomer() {
		LOGGER.debug("Fetching all customers from the database.");

		List<Customer> cusomers = customerRepository.findAll();
		if (cusomers.isEmpty()) {
			LOGGER.warn("No cusomers found in the database.");
		} else {
			LOGGER.info("Successfully fetched {} cusomers from the database.", cusomers.size());
		}

		return cusomers.stream().map(this::convertToDTO).collect(Collectors.toList());
	}

	/**
	 * Get all exist customers.
	 */
	public List<CustomerDTO> getAllExistCustomers() {

		LOGGER.debug("Fetching all non-deleted roles.");

		List<Customer> customers = customerRepository.findByDeletedFalse();
		if (customers.isEmpty()) {
			LOGGER.warn("No active customers found in the system.");
		} else {
			LOGGER.info("Fetched {} active customers from the database.", customers.size());
		}
		return customers.stream().map(this::convertToDTO).collect(Collectors.toList());
	}

	/**
	 * Get a customer by ID.
	 */
	public CustomerDTO getCustomerById(Integer customerId) {

		LOGGER.debug("Fetching customer with ID: {}", customerId);

		Customer customer = customerRepository.findById(customerId).orElseThrow(() -> {
			String errorMessage = ErrorMessages.CUSTOMER_NOT_FOUND + customerId;
			LOGGER.error("Customer fetch failed. {}", errorMessage);
			return new NotFoundException(errorMessage);
		});
		LOGGER.info("Successfully fetched customer with ID: {} and name: '{}'", customer.getCustomerId(),
				customer.getUsername());
		return convertToDTO(customer);
	}

	/**
	 * Get a customer by User name.
	 */
	public CustomerDTO getCustomerByUserName(String username) {

		LOGGER.debug("Fetching customer with username: {}", username);

		Customer customer = customerRepository.findByUsername(username).orElseThrow(() -> {
			String errorMessage = ErrorMessages.CUSTOMER_NOT_FOUND + username;
			LOGGER.error("Customer fetch failed. {}", errorMessage);
			return new NotFoundException(errorMessage);
		});
		LOGGER.info("Successfully fetched customer with ID: {} and name: '{}'", customer.getCustomerId(),
				customer.getUsername());

		return convertToDTO(customer);
	}

	/**
	 * Get a customer by first name.
	 */
	public CustomerDTO getCustomerByFirstName(String firstName) {

		LOGGER.debug("Fetching customer with first name: {}", firstName);

		Customer customer = customerRepository.findByFirstName(firstName).orElseThrow(() -> {
			String errorMessage = ErrorMessages.CUSTOMER_NOT_FOUND + firstName;
			LOGGER.error("Customer fetch failed. {}", errorMessage);
			return new NotFoundException(errorMessage);
		});
		LOGGER.info("Successfully fetched customer with ID: {} and name: '{}'", customer.getCustomerId(),
				customer.getUsername());
		return convertToDTO(customer);
	}

	/**
	 * Get a customer by last name.
	 */
	public CustomerDTO getCustomerByLastName(String lastName) {

		LOGGER.debug("Fetching customer with last name: {}", lastName);

		Customer customer = customerRepository.findByLastName(lastName).orElseThrow(() -> {
			String errorMessage = ErrorMessages.CUSTOMER_NOT_FOUND + lastName;
			LOGGER.error("Customer fetch failed. {}", errorMessage);
			return new NotFoundException(errorMessage);
		});
		LOGGER.info("Successfully fetched customer with ID: {} and name: '{}'", customer.getCustomerId(),
				customer.getUsername());
		return convertToDTO(customer);
	}

	/**
	 * Get a customer by first name and last name.
	 */
	public CustomerDTO getCustomerByFirstNameAndLastName(String firstName, String lastName) {

		LOGGER.debug("Fetching customer with last name: {} and last name: {}", lastName, lastName);

		Customer customer = customerRepository.findByFirstNameAndLastName(firstName, lastName).orElseThrow(() -> {
			String errorMessage = ErrorMessages.CUSTOMER_NOT_FOUND + lastName + lastName;
			LOGGER.error("Customer fetch failed. {}", errorMessage);
			return new NotFoundException(errorMessage);
		});
		LOGGER.info("Successfully fetched customer with ID: {} and name: '{}'", customer.getCustomerId(),
				customer.getUsername());

		return convertToDTO(customer);
	}

	/**
	 * Get a customer by email.
	 */
	public CustomerDTO getUserByEmail(String email) {

		LOGGER.debug("Fetching customer with email: {}", email);

		Customer customer = customerRepository.findByEmail(email).orElseThrow(() -> {
			String errorMessage = ErrorMessages.CUSTOMER_NOT_FOUND + email;
			LOGGER.error("Customer fetch failed. {}", errorMessage);
			return new NotFoundException(errorMessage);
		});
		LOGGER.info("Successfully fetched customer with ID: {} and name: '{}'", customer.getCustomerId(),
				customer.getUsername());
		return convertToDTO(customer);
	}

	/**
	 * Create a new customer.
	 */
	@Transactional
	public CustomerDTO createCustomer(Customer newCustomer) {

		LOGGER.debug("Attempting to create a new customer with name: {}", newCustomer.getUsername());

		Customer savedCustomer = null;

		// Check if the user exists
		Optional<Customer> existingCustomer = customerRepository
				.findByUsernameAndDeletedFalse(newCustomer.getUsername());
		if (existingCustomer.isPresent()) {
			String errorMessage = ErrorMessages.CUSTOMER_NAME_EXISTS;
			LOGGER.error("Customer creation failed: {}", errorMessage);
			throw new IllegalArgumentException(errorMessage);
		}

		if (customerRepository.existsByEmail(newCustomer.getEmail())) {
			String errorMessage = ErrorMessages.EMAIL_EXISTS;
			LOGGER.error("Customer creation failed: {}", errorMessage);
			throw new IllegalArgumentException(errorMessage);
		}

		UsersDTO loggedInUser = authUtils.getLoggedInUser();

		// Check for soft-deleted user and reactivate it
		Optional<Customer> softDeletedCustomer = customerRepository
				.findByUsernameAndDeletedTrue(newCustomer.getUsername());
		if (softDeletedCustomer.isPresent()) {
			Customer reactivatedCustomer = softDeletedCustomer.get();

			reactivatedCustomer.setFirstName(newCustomer.getFirstName());
			reactivatedCustomer.setLastName(newCustomer.getLastName());
			reactivatedCustomer.setEmail(newCustomer.getEmail());
			reactivatedCustomer.setAddress(newCustomer.getAddress());
			reactivatedCustomer.setPhoneNo1(newCustomer.getPhoneNo1());
			reactivatedCustomer.setEnabled(newCustomer.isEnabled());
			reactivatedCustomer.setLocked(newCustomer.isLocked());
			reactivatedCustomer.setLocked(newCustomer.isLocked());
			reactivatedCustomer.setUpdatedUserId(loggedInUser.getUserId());
			reactivatedCustomer.setDeleted(false);
			reactivatedCustomer.setDeletedAt(null);
			reactivatedCustomer.setDeletedUserId(null);

			savedCustomer = customerRepository.save(reactivatedCustomer);
			LOGGER.info("Soft-deleted customer '{}' reactivated successfully.", savedCustomer.getUsername());
		} else {
			// Otherwise, create a new user
			newCustomer.setCreatedUserId(loggedInUser.getUserId());
			newCustomer.setUpdatedUserId(loggedInUser.getUserId());

			savedCustomer = customerRepository.save(newCustomer);
			LOGGER.info("New customer '{}' created successfully with ID: {}", savedCustomer.getUsername(),
					savedCustomer.getCustomerId());

		}
		return convertToDTO(savedCustomer);
	}

	/**
	 * Update an existing customer.
	 */
	@Transactional
	public CustomerDTO updateCustomer(Integer customerId, Customer updatedCustomer) {

		LOGGER.debug("Attempting to update customer with ID: {}", customerId);

		UsersDTO loggedInUser = authUtils.getLoggedInUser();

		return customerRepository.findById(customerId).map(customer -> {

			customer.setCustomerGroupId(updatedCustomer.getCustomerGroupId());
			customer.setFirstName(updatedCustomer.getFirstName());
			customer.setLastName(updatedCustomer.getLastName());
			customer.setEmail(updatedCustomer.getEmail());
			customer.setPhoneNo1(updatedCustomer.getPhoneNo1());
			customer.setAddress(updatedCustomer.getAddress());
			customer.setEnabled(updatedCustomer.isEnabled());
			customer.setLocked(updatedCustomer.isLocked());
			customer.setUpdatedUserId(loggedInUser.getUserId());

			Customer savedCustomer = customerRepository.save(customer);
			LOGGER.info("Customer with ID: {} successfully updated. New name: '{}'", customerId,
					savedCustomer.getUsername());
			return convertToDTO(savedCustomer);
		}).orElseThrow(() -> {
			String errorMessage = ErrorMessages.CUSTOMER_NOT_FOUND + customerId;
			LOGGER.error("Customer update failed: {}", errorMessage);
			return new RuntimeException(errorMessage);
		});
	}

	/** Delete a customer by ID (soft delete) */
	public void softDeleteCustomer(Integer customerId) {

		LOGGER.debug("Attempting to soft delete customer with ID: {}", customerId);

		UsersDTO loggedInUser = authUtils.getLoggedInUser();

		customerRepository.findById(customerId).ifPresentOrElse(customer -> {
			customer.setUpdatedUserId(loggedInUser.getUserId());
			customer.setDeleted(true);
			customer.setDeletedAt(LocalDateTime.now());
			customer.setDeletedUserId(loggedInUser.getUserId());
			customerRepository.save(customer);
			LOGGER.info("Customer with ID: {} has been soft deleted.", customerId);
		}, () -> {
			String errorMessage = ErrorMessages.CUSTOMER_NOT_FOUND + customerId;
			LOGGER.error("Soft delete failed: {}", errorMessage);
			throw new NotFoundException(errorMessage);
		});
	}

	/** Delete a customer by ID */
	public void deleteCustomer(Integer customerId) {

		LOGGER.debug("Attempting to delete customer with ID: {}", customerId);

		Customer customer = customerRepository.findById(customerId).orElseThrow(() -> {
			String errorMessage = ErrorMessages.CUSTOMER_NOT_FOUND + customerId;
			LOGGER.error("Customer deletion failed: {}", errorMessage);
			throw new NotFoundException(errorMessage);
		});

		customerRepository.delete(customer);
		LOGGER.info("Customer with ID: {} has been permanently deleted.", customerId);
	}

	/**
	 * Convert Users entity to UsersDTO.
	 */
	private CustomerDTO convertToDTO(Customer customer) {

		CustomerDTO customerDTO = modelMapper.map(customer, CustomerDTO.class);

		CustomerGroup customerGroup = customerGroupRepository.findById(customer.getCustomerGroupId()).orElseThrow();
		CustomerGroupDTO customerGroupDTO = modelMapper.map(customerGroup, CustomerGroupDTO.class);
		customerDTO.setCustomerGroup(customerGroupDTO);

		// Set CreatedUserDTO
		UsersDTO createdUser = authUtils.getUserById(customer.getCreatedUserId());
		CreatedUpdatedUserDTO createdUserDto = modelMapper.map(createdUser, CreatedUpdatedUserDTO.class);
		customerDTO.setCreatedUser(createdUserDto);

		// Set UpdatedUserDTO
		UsersDTO updatedUser = authUtils.getUserById(customer.getUpdatedUserId());
		CreatedUpdatedUserDTO updatedUserDto = modelMapper.map(updatedUser, CreatedUpdatedUserDTO.class);
		customerDTO.setUpdatedUser(updatedUserDto);

		// Set DeletedUserDTO
		if (customer.isDeleted()) {
			UsersDTO deletedUser = authUtils.getUserById(customer.getDeletedUserId());
			CreatedUpdatedUserDTO deletedUserDto = modelMapper.map(deletedUser, CreatedUpdatedUserDTO.class);
			customerDTO.setDeletedUser(deletedUserDto);
		}

		return customerDTO;
	}
}
