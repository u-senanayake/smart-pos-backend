package lk.udcreations.product.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import lk.udcreations.product.constants.ErrorMessages;
import lk.udcreations.common.dto.category.CategoryDTO;
import lk.udcreations.common.dto.user.CreatedUpdatedUserDTO;
import lk.udcreations.common.dto.user.UsersDTO;
import lk.udcreations.product.exception.NotFoundException;
import lk.udcreations.product.entity.Category;
import lk.udcreations.product.repository.CategoryRepository;
import lk.udcreations.product.security.AuthUtils;

@Service
public class CategoryService {

	private static final Logger LOGGER = LoggerFactory.getLogger(CategoryService.class);

	private final CategoryRepository categoryRepository;
	private final AuthUtils authUtils;
	private final ModelMapper modelMapper;

	public CategoryService(CategoryRepository categoryRepository, AuthUtils authUtils, ModelMapper modelMapper) {
		super();
		this.categoryRepository = categoryRepository;
		this.authUtils = authUtils;
		this.modelMapper = modelMapper;
	}

	/** Get all categories */
	public List<CategoryDTO> getAllCategories() {

		LOGGER.debug("Fetching all categories from the database.");

		List<Category> categories = categoryRepository.findAll();
		if (categories.isEmpty()) {
			LOGGER.warn("No categories found in the database.");
		} else {
			LOGGER.info("Successfully fetched {} categories from the database.", categories.size());
		}

		return categories.stream().map(this::convertToDTO).collect(Collectors.toList());
	}

	/** Get all non-deleted categories */
	public List<CategoryDTO> getAllExistCategories() {

		LOGGER.debug("Fetching all non-deleted categories.");

		List<Category> categories = categoryRepository.findByDeletedFalse();
		if (categories.isEmpty()) {
			LOGGER.warn("No active categories found in the system.");
		} else {
			LOGGER.info("Fetched {} active categories from the database.", categories.size());
		}
		return categories.stream().map(this::convertToDTO).collect(Collectors.toList());
	}

	/** Get a role by ID */
	public CategoryDTO getCategoryById(Integer categoryId) {

		LOGGER.debug("Fetching role with ID: {}", categoryId);

		Category category = categoryRepository.findById(categoryId).orElseThrow(() -> {
			String errorMessage = ErrorMessages.CATEGORY_NOT_FOUND + categoryId;
			LOGGER.error("Category fetch failed. {}", errorMessage);
			return new NotFoundException(errorMessage);
		});
		LOGGER.info("Successfully fetched category with ID: {} and name: '{}'", category.getCategoryId(),
				category.getName());
		return convertToDTO(category);
	}

	/** Create a new role */
	@Transactional
	public CategoryDTO createCategory(Category category) {

		LOGGER.debug("Attempting to create a new category with name: {}", category.getName());

		Category savedCategory;
		UsersDTO loggedInUser = authUtils.getLoggedInUser();

		// Check if the category exists
		Optional<Category> existingCategory = categoryRepository.findByNameAndDeletedFalse(category.getName());
		if (existingCategory.isPresent()) {
			String errorMessage = ErrorMessages.CATEGORY_NAME_EXISTS;
			LOGGER.error("Category creation failed: {}", errorMessage);
			throw new IllegalArgumentException(errorMessage);
		}

		// Check for a soft-deleted category and reactivate it
		Optional<Category> softDeletedCategory = categoryRepository.findByNameAndDeletedTrue(category.getName());
		if (softDeletedCategory.isPresent()) {
			Category reactivatedCategory = softDeletedCategory.get();

			reactivatedCategory.setDescription(category.getDescription());
			reactivatedCategory.setCatPrefix(category.getCatPrefix());
			reactivatedCategory.setEnabled(category.isEnabled());
			reactivatedCategory.setUpdatedUserId(loggedInUser.getUserId());
			reactivatedCategory.setDeleted(false);
			reactivatedCategory.setDeletedAt(null);
			reactivatedCategory.setDeletedUserId(null);

			savedCategory = categoryRepository.save(reactivatedCategory);
			LOGGER.info("Soft-deleted category '{}' reactivated successfully.", category.getName());
		} else {
			// Otherwise, create a new category
			category.setUpdatedUserId(loggedInUser.getUserId());
			category.setCreatedUserId(loggedInUser.getUserId());
			category.setDeleted(false);

			savedCategory = categoryRepository.save(category);
			LOGGER.info("New category '{}' created successfully with ID: {}", category.getName(),
					savedCategory.getCategoryId());
		}
		return convertToDTO(savedCategory);
	}

	/** Update category */
	@Transactional
	public CategoryDTO updateCategory(Integer categoryId, Category updatedCategory) {

		LOGGER.debug("Attempting to update category with ID: {}", categoryId);

		UsersDTO loggedInUser = authUtils.getLoggedInUser();

		return categoryRepository.findById(categoryId).map(category -> {
			category.setName(updatedCategory.getName());
			category.setDescription(updatedCategory.getDescription());
			category.setCatPrefix(updatedCategory.getCatPrefix());
			category.setEnabled(updatedCategory.isEnabled());
			category.setUpdatedUserId(loggedInUser.getUserId());
			return convertToDTO(categoryRepository.save(category));
		}).orElseThrow(() -> {
			String errorMessage = ErrorMessages.CATEGORY_NOT_FOUND + categoryId;
			LOGGER.error("Category update failed: {}", errorMessage);
			return new RuntimeException(errorMessage);
		});
	}

	/** Delete a category by ID (soft delete) */
	@Transactional
	public void softDeleteCategory(Integer categoryId) {

		LOGGER.debug("Attempting to soft delete role with ID: {}", categoryId);

		UsersDTO loggedInUser = authUtils.getLoggedInUser();

		categoryRepository.findById(categoryId).ifPresentOrElse(category -> {
			category.setUpdatedUserId(loggedInUser.getUserId());
			category.setDeleted(true);
			category.setDeletedAt(LocalDateTime.now());
			category.setDeletedUserId(loggedInUser.getUserId());
			categoryRepository.save(category);
			LOGGER.info("Category with ID: {} has been soft deleted.", categoryId);
		}, () -> {
			String errorMessage = ErrorMessages.CATEGORY_NOT_FOUND + categoryId;
			LOGGER.error("Soft delete failed: {}", errorMessage);
			throw new NotFoundException(errorMessage);
		});
	}

	/** Delete a category by ID */
	@Transactional
	public void deleteCategory(Integer categoryId) {

		LOGGER.debug("Attempting to delete category with ID: {}", categoryId);

		Category category = categoryRepository.findById(categoryId).orElseThrow(() -> {
			String errorMessage = ErrorMessages.CATEGORY_NOT_FOUND + categoryId;
			LOGGER.error("Category deletion failed: {}", errorMessage);
            return new NotFoundException(errorMessage);
		});

		categoryRepository.delete(category);
		LOGGER.info("Category with ID: {} has been permanently deleted.", categoryId);
	}

	private CategoryDTO convertToDTO(Category category) {
		
		CategoryDTO categoryDTO = modelMapper.map(category, CategoryDTO.class);

		// Set CreatedUserDTO
		UsersDTO createdUser = authUtils.getUserById(category.getCreatedUserId());
		CreatedUpdatedUserDTO createdUserDto = modelMapper.map(createdUser, CreatedUpdatedUserDTO.class);
		categoryDTO.setCreatedUser(createdUserDto);

		// Set UpdatedUserDTO
		UsersDTO updatedUser = authUtils.getUserById(category.getUpdatedUserId());
		CreatedUpdatedUserDTO updatedUserDto = modelMapper.map(updatedUser, CreatedUpdatedUserDTO.class);
		categoryDTO.setUpdatedUser(updatedUserDto);

		// Set DeletedUserDTO
		if (category.isDeleted()) {
			UsersDTO deletedUser = authUtils.getUserById(category.getDeletedUserId());
			CreatedUpdatedUserDTO deletedUserDto = modelMapper.map(deletedUser, CreatedUpdatedUserDTO.class);
			categoryDTO.setDeletedUser(deletedUserDto);
		}

		return categoryDTO;
	}
}
