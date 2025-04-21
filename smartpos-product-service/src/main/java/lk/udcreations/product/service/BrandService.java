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
import lk.udcreations.common.dto.brand.BrandDTO;
import lk.udcreations.common.dto.user.CreatedUpdatedUserDTO;
import lk.udcreations.common.dto.user.UsersDTO;
import lk.udcreations.product.exception.NotFoundException;
import lk.udcreations.product.entity.Brand;
import lk.udcreations.product.repository.BrandRepository;
import lk.udcreations.product.security.AuthUtils;

@Service
public class BrandService {

	private static final Logger LOGGER = LoggerFactory.getLogger(BrandService.class);

	private final BrandRepository brandRepository;
	private final AuthUtils authUtils;
	private final ModelMapper modelMapper;

	public BrandService(BrandRepository brandRepository, AuthUtils authUtils, ModelMapper modelMapper) {
		super();
		this.brandRepository = brandRepository;
		this.authUtils = authUtils;
		this.modelMapper = modelMapper;
	}

	/** Get all brands */
	public List<BrandDTO> getAllBrands() {

		LOGGER.debug("Fetching all brands from the database.");

		List<Brand> brands = brandRepository.findAll();
		if (brands.isEmpty()) {
			LOGGER.warn("No brands found in the database.");
		} else {
			LOGGER.info("Successfully fetched {} brands from the database.", brands.size());
		}

		return brands.stream().map(this::convertToDTO).collect(Collectors.toList());
	}

	/** Get all non-deleted brands */
	public List<BrandDTO> getAllExistBrands() {

		LOGGER.debug("Fetching all non-deleted brands.");

		List<Brand> brands = brandRepository.findByDeletedFalse();
		if (brands.isEmpty()) {
			LOGGER.warn("No active brands found in the system.");
		} else {
			LOGGER.info("Fetched {} active brands from the database.", brands.size());
		}

		return brands.stream().map(this::convertToDTO).collect(Collectors.toList());
	}

	/** Get a brand by ID */
	public BrandDTO getBrandById(Integer brandId) {

		LOGGER.debug("Fetching brand with ID: {}", brandId);

		Brand brand = brandRepository.findById(brandId).orElseThrow(() -> {
			String errorMessage = ErrorMessages.BRAND_NOT_FOUND + brandId;
			LOGGER.error("Brand fetch failed. {}", errorMessage);
			return new NotFoundException(errorMessage);
		});

		LOGGER.info("Successfully fetched brand with ID: {} and name: '{}'", brand.getBrandId(), brand.getName());

		return convertToDTO(brand);
	}

	/** Create a new brand */
	public BrandDTO createBrand(Brand brand) {

		LOGGER.debug("Attempting to create a new brand with name: {}", brand.getName());

		Brand savedBrand = null;
		UsersDTO loggedInUser = authUtils.getLoggedInUser();

		// Check if the brand exists
		Optional<Brand> existingBrand = brandRepository.findByNameAndDeletedFalse(brand.getName());
		if (existingBrand.isPresent()) {
			String errorMessage = ErrorMessages.BRAND_NAME_EXISTS;
			LOGGER.error("Brand creation failed: {}", errorMessage);
			throw new IllegalArgumentException(errorMessage);

		}

		// Check for soft-deleted category and reactivate it
		Optional<Brand> softDeletedBrand = brandRepository.findByNameAndDeletedTrue(brand.getName());
		if (softDeletedBrand.isPresent()) {
			Brand reactivatedBrand = softDeletedBrand.get();

			reactivatedBrand.setDescription(brand.getDescription());
			reactivatedBrand.setEnabled(brand.isEnabled());
			reactivatedBrand.setUpdatedUserId(loggedInUser.getUserId());
			reactivatedBrand.setDeleted(false);
			reactivatedBrand.setDeletedAt(null);
			reactivatedBrand.setDeletedUserId(null);

			savedBrand = brandRepository.save(reactivatedBrand);
			LOGGER.info("Soft-deleted brand '{}' reactivated successfully.", brand.getName());
		} else {
			// Otherwise, create a new category
			brand.setUpdatedUserId(loggedInUser.getUserId());
			brand.setCreatedUserId(loggedInUser.getUserId());
			brand.setDeleted(false);

			savedBrand = brandRepository.save(brand);
			LOGGER.info("New brand '{}' created successfully with ID: {}", brand.getName(), savedBrand.getBrandId());
		}

		return convertToDTO(savedBrand);
	}

	/** Update brand */
	public BrandDTO updateBrand(Integer brandId, Brand updatedBrand) {

		LOGGER.debug("Attempting to update brand with ID: {}", brandId);

		UsersDTO loggedInUser = authUtils.getLoggedInUser();
		return brandRepository.findById(brandId).map(brand -> {
			brand.setName(updatedBrand.getName());
			brand.setDescription(updatedBrand.getDescription());
			brand.setEnabled(updatedBrand.isEnabled());
			brand.setUpdatedUserId(loggedInUser.getUserId());

			Brand savedBrand = brandRepository.save(brand);
			LOGGER.info("Brand with ID: {} successfully updated. New name: '{}'", brandId, savedBrand.getName());

			return convertToDTO(savedBrand);
		}).orElseThrow(() -> {
			String errorMessage = ErrorMessages.BRAND_NOT_FOUND + brandId;
			LOGGER.error("Brand update failed: {}", errorMessage);
			return new RuntimeException(errorMessage);
		});
	}

	/** Delete a brand by ID (soft delete) */
	public void softDeleteBrand(Integer brandId) {

		LOGGER.debug("Attempting to soft delete brand with ID: {}", brandId);

		UsersDTO loggedInUser = authUtils.getLoggedInUser();

		brandRepository.findById(brandId).ifPresentOrElse(brand -> {
			brand.setUpdatedUserId(loggedInUser.getUserId());
			brand.setDeleted(true);
			brand.setDeletedAt(LocalDateTime.now());
			brand.setDeletedUserId(loggedInUser.getUserId());
			brandRepository.save(brand);
			LOGGER.info("Brand with ID: {} has been soft deleted.", brandId);
		}, () -> {
			String errorMessage = ErrorMessages.BRAND_NOT_FOUND + brandId;
			LOGGER.error("Soft delete failed: {}", errorMessage);
			throw new NotFoundException(errorMessage);
		});
	}

	/** Delete a brand by ID */
	public void deleteBrand(Integer brandId) {

		LOGGER.debug("Attempting to delete brand with ID: {}", brandId);

		Brand brand = brandRepository.findById(brandId).orElseThrow(() -> {
			String errorMessage = ErrorMessages.BRAND_NOT_FOUND + brandId;
			LOGGER.error("BRAND deletion failed: {}", errorMessage);
			throw new NotFoundException(errorMessage);
		});

		brandRepository.delete(brand);
		LOGGER.info("Brand with ID: {} has been permanently deleted.", brandId);
	}

	private BrandDTO convertToDTO(Brand brand) {

		BrandDTO brandDTO = modelMapper.map(brand, BrandDTO.class);

		// Set CreatedUserDTO
		UsersDTO createdUser = authUtils.getUserById(brand.getCreatedUserId());
		CreatedUpdatedUserDTO createdUserDto = modelMapper.map(createdUser, CreatedUpdatedUserDTO.class);
		brandDTO.setCreatedUser(createdUserDto);

		// Set UpdatedUserDTO
		UsersDTO updatedUser = authUtils.getUserById(brand.getUpdatedUserId());
		CreatedUpdatedUserDTO updatedUserDto = modelMapper.map(updatedUser, CreatedUpdatedUserDTO.class);
		brandDTO.setUpdatedUser(updatedUserDto);

		// Set DeletedUserDTO
		if (brand.isDeleted()) {
			UsersDTO deletedUser = authUtils.getUserById(brand.getDeletedUserId());
			CreatedUpdatedUserDTO deletedUserDto = modelMapper.map(deletedUser, CreatedUpdatedUserDTO.class);
			brandDTO.setDeletedUser(deletedUserDto);
		}

		return brandDTO;
	}
}
