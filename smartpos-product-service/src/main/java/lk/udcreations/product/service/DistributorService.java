package lk.udcreations.product.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import lk.udcreations.product.constants.ErrorMessages;
import lk.udcreations.common.dto.distributor.DistributorDTO;
import lk.udcreations.common.dto.user.CreatedUpdatedUserDTO;
import lk.udcreations.common.dto.user.UsersDTO;
import lk.udcreations.product.exception.NotFoundException;
import lk.udcreations.product.entity.Distributor;
import lk.udcreations.product.repository.DistributorRepository;
import lk.udcreations.product.security.AuthUtils;

@Service
public class DistributorService {

	private static final Logger LOGGER = LoggerFactory.getLogger(DistributorService.class);

	private final DistributorRepository distributorRepository;
	private final AuthUtils authUtils;
	private final ModelMapper modelMapper;

	public DistributorService(DistributorRepository distributorRepository, AuthUtils authUtils,
			ModelMapper modelMapper) {
		super();
		this.distributorRepository = distributorRepository;
		this.authUtils = authUtils;
		this.modelMapper = modelMapper;
	}

	/** Get all distributors */
	public List<DistributorDTO> getAllDistributors() {

		LOGGER.debug("Fetching all distributors from the database.");

		List<Distributor> distributors = distributorRepository.findAll();
		if (distributors.isEmpty()) {
			LOGGER.warn("No distributors found in the database.");
		} else {
			LOGGER.info("Successfully fetched {} distributors from the database.", distributors.size());
		}

		return distributors.stream().map(this::convertToDTO).collect(Collectors.toList());
	}

	/** Get all non-deleted distributor */
	public List<DistributorDTO> getAllExistDistributors() {

		LOGGER.debug("Fetching all non-deleted distributors.");

		List<Distributor> distributors = distributorRepository.findByDeletedFalse();
		if (distributors.isEmpty()) {
			LOGGER.warn("No active distributors found in the system.");
		} else {
			LOGGER.info("Fetched {} active distributors from the database.", distributors.size());
		}

		return distributors.stream().map(this::convertToDTO).collect(Collectors.toList());
	}

	/** Get a distributor by ID */
	public DistributorDTO getDistributorById(Integer distributorId) {

		LOGGER.debug("Fetching distributor with ID: {}", distributorId);

		Distributor distributor = distributorRepository.findById(distributorId).orElseThrow(() -> {
			String errorMessage = ErrorMessages.DISTRIBUTOR_NOT_FOUND + distributorId;
			LOGGER.error("Distributor fetch failed. {}", errorMessage);
			return new NotFoundException(errorMessage);
		});

		LOGGER.info("Successfully fetched distributor with ID: {} and name: '{}'", distributor.getDistributorId(),
				distributor.getCompanyName());
		return convertToDTO(distributor);
	}

	/** Create a new distributor */
	public DistributorDTO createDistributor(Distributor distributor) {

		LOGGER.debug("Attempting to create a new distributor with name: {}", distributor.getCompanyName());

		UsersDTO loggedInUser = authUtils.getLoggedInUser();
		Distributor savedDistributor = null;

		// Check if the distributor exists
		Optional<Distributor> existingDistributor = distributorRepository
				.findByCompanyNameAndDeletedFalse(distributor.getCompanyName());

		if (existingDistributor.isPresent()) {
			String errorMessage = ErrorMessages.DISTRIBUTOR_NAME_EXISTS;
			LOGGER.error("Distributor creation failed: {}", errorMessage);
			throw new IllegalArgumentException(errorMessage);

		}

		// Check if the email exists
		Optional<Distributor> existingEmail = distributorRepository.findByEmail(distributor.getEmail());
		if (existingEmail.isPresent()) {
			String errorMessage = ErrorMessages.EMAIL_EXISTS;
			LOGGER.error("Distributor creation failed: {}", errorMessage);
			throw new IllegalArgumentException(errorMessage);
		}

		// Check for soft-deleted category and reactivate it
		Optional<Distributor> softDeletedDistributor = distributorRepository
				.findByCompanyNameAndDeletedTrue(distributor.getCompanyName());
		if (softDeletedDistributor.isPresent()) {
			Distributor reactivatedDistributor = softDeletedDistributor.get();

			reactivatedDistributor.setEmail(distributor.getEmail());
			reactivatedDistributor.setPhoneNo1(distributor.getPhoneNo1());
			reactivatedDistributor.setPhoneNo2(distributor.getPhoneNo2());
			reactivatedDistributor.setAddress(distributor.getAddress());
			reactivatedDistributor.setEnabled(distributor.isEnabled());
			reactivatedDistributor.setUpdatedUserId(loggedInUser.getUserId());
			reactivatedDistributor.setDeleted(false);
			reactivatedDistributor.setDeletedAt(null);
			reactivatedDistributor.setDeletedUserId(null);

			savedDistributor = distributorRepository.save(reactivatedDistributor);
			LOGGER.info("Soft-deleted distributor '{}' reactivated successfully.", distributor.getCompanyName());
		} else {
			// Otherwise, create a new category
			distributor.setUpdatedUserId(loggedInUser.getUserId());
			distributor.setCreatedUserId(loggedInUser.getUserId());
			distributor.setDeleted(false);

			savedDistributor = distributorRepository.save(distributor);
			LOGGER.info("New distributor '{}' created successfully with ID: {}", distributor.getCompanyName(),
					savedDistributor.getDistributorId());
		}
		return convertToDTO(savedDistributor);
	}

	/** Update distributor */
	public DistributorDTO updateDistributor(Integer distributorId, Distributor updatedDistributor) {

		LOGGER.debug("Attempting to update distributor with ID: {}", distributorId);

		UsersDTO loggedInUser = authUtils.getLoggedInUser();

		return distributorRepository.findById(distributorId).map(distributor -> {
			distributor.setCompanyName(updatedDistributor.getCompanyName());
			distributor.setEmail(updatedDistributor.getEmail());
			distributor.setPhoneNo1(updatedDistributor.getPhoneNo1());
			distributor.setPhoneNo2(updatedDistributor.getPhoneNo2());
			distributor.setAddress(updatedDistributor.getAddress());
			distributor.setEnabled(updatedDistributor.isEnabled());
			distributor.setUpdatedUserId(loggedInUser.getUserId());
			Distributor savedDistributor = distributorRepository.save(distributor);
			LOGGER.info("Distributor with ID: {} successfully updated. New name: '{}'", distributorId,
					savedDistributor.getCompanyName());
			return convertToDTO(savedDistributor);
		}).orElseThrow(() -> {
			String errorMessage = ErrorMessages.DISTRIBUTOR_NOT_FOUND + distributorId;
			LOGGER.error("Distributor update failed: {}", errorMessage);
			return new RuntimeException(errorMessage);
		});

	}

	/** Delete a distributor by ID (soft delete) */
	public void softDeleteDistributor(Integer distributorId) {

		LOGGER.debug("Attempting to soft delete distributor with ID: {}", distributorId);

		UsersDTO loggedInUser = authUtils.getLoggedInUser();

		distributorRepository.findById(distributorId).ifPresentOrElse(distributor -> {
			distributor.setUpdatedUserId(loggedInUser.getUserId());
			distributor.setDeleted(true);
			distributor.setDeletedAt(LocalDateTime.now());
			distributor.setDeletedUserId(loggedInUser.getUserId());
			distributorRepository.save(distributor);
			LOGGER.info("Distributor with ID: {} has been soft deleted.", distributorId);
		}, () -> {
			String errorMessage = ErrorMessages.DISTRIBUTOR_NOT_FOUND + distributorId;
			LOGGER.error("Soft delete failed: {}", errorMessage);
			throw new NotFoundException(errorMessage);
		});
	}

	/** Delete a distributor by ID */
	public void deleteDistributor(Integer distributorId) {

		LOGGER.debug("Attempting to delete distributor with ID: {}", distributorId);

		Distributor distributor = distributorRepository.findById(distributorId).orElseThrow(() -> {
			String errorMessage = ErrorMessages.DISTRIBUTOR_NOT_FOUND + distributorId;
			LOGGER.error("Distributor deletion failed: {}", errorMessage);
			throw new NotFoundException(errorMessage);
		});

		distributorRepository.delete(distributor);
		LOGGER.info("Distributor with ID: {} has been permanently deleted.", distributorId);
	}

	private DistributorDTO convertToDTO(Distributor distributor) {

		DistributorDTO distributorDTO = modelMapper.map(distributor, DistributorDTO.class);

		// Set CreatedUserDTO
		UsersDTO createdUser = authUtils.getUserById(distributor.getCreatedUserId());
		CreatedUpdatedUserDTO createdUserDto = modelMapper.map(createdUser, CreatedUpdatedUserDTO.class);
		distributorDTO.setCreatedUser(createdUserDto);

		// Set UpdatedUserDTO
		UsersDTO updatedUser = authUtils.getUserById(distributor.getUpdatedUserId());
		CreatedUpdatedUserDTO updatedUserDto = modelMapper.map(updatedUser, CreatedUpdatedUserDTO.class);
		distributorDTO.setUpdatedUser(updatedUserDto);

		// Set DeletedUserDTO
		if (distributor.isDeleted()) {
			UsersDTO deletedUser = authUtils.getUserById(distributor.getDeletedUserId());
			CreatedUpdatedUserDTO deletedUserDto = modelMapper.map(deletedUser, CreatedUpdatedUserDTO.class);
			distributorDTO.setDeletedUser(deletedUserDto);
		}

		return distributorDTO;
	}

}
