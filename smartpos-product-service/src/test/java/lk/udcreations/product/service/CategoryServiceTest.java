package lk.udcreations.product.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;

import lk.udcreations.common.dto.category.CategoryDTO;
import lk.udcreations.common.dto.user.UsersDTO;
import lk.udcreations.product.constants.ErrorMessages;
import lk.udcreations.product.entity.Category;
import lk.udcreations.product.exception.NotFoundException;
import lk.udcreations.product.repository.CategoryRepository;
import lk.udcreations.product.security.AuthUtils;

class CategoryServiceTest {

	@Mock
	private CategoryRepository categoryRepository;

	@Mock
	private AuthUtils authUtils;

	@Mock
	private ModelMapper modelMapper;

	@InjectMocks
	private CategoryService categoryService;

	private Integer adminUser;
	private UsersDTO mockUser;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);

		// Mock a logged-in admin user
		adminUser = 999;
		mockUser = new UsersDTO();
		mockUser.setUserId(adminUser);

		// Mock authUtils methods
		when(authUtils.getLoggedInUser()).thenReturn(mockUser);
		when(authUtils.getUserById(any(Integer.class))).thenReturn(mockUser);

		// Mock modelMapper
		when(modelMapper.map(any(), any())).thenAnswer(invocation -> {
			Object source = invocation.getArgument(0);
			Class<?> targetClass = invocation.getArgument(1);

			if (source instanceof Category && targetClass == CategoryDTO.class) {
				Category category = (Category) source;
				CategoryDTO dto = new CategoryDTO();
				dto.setCategoryId(category.getCategoryId());
				dto.setName(category.getName());
				dto.setDescription(category.getDescription());
				dto.setCatPrefix(category.getCatPrefix());
				dto.setEnabled(category.isEnabled());
				dto.setDeleted(category.isDeleted());
				return dto;
			}

			return null;
		});
	}

	@Test
	void testGetAllCategories() {
		Category category1 = new Category();
		category1.setCategoryId(1);
		category1.setName("Electronics");
		category1.setDescription("Electronic items");
		category1.setCatPrefix("E");

		Category category2 = new Category();
		category2.setCategoryId(2);
		category2.setName("Clothing");
		category2.setDescription("Clothing items");
		category2.setCatPrefix("C");

		when(categoryRepository.findAll()).thenReturn(Arrays.asList(category1, category2));

		List<CategoryDTO> result = categoryService.getAllCategories();

		assertEquals(2, result.size());
		assertEquals("Electronics", result.get(0).getName());
		assertEquals("Clothing", result.get(1).getName());
		verify(categoryRepository, times(1)).findAll();
	}

	@Test
	void testGetCategoryById_CategoryExists() {
		Category category = new Category();
		category.setCategoryId(1);
		category.setName("Electronics");
		category.setDescription("Electronic items");
		category.setCatPrefix("E");

		when(categoryRepository.findById(1)).thenReturn(Optional.of(category));

		CategoryDTO result = categoryService.getCategoryById(1);

		assertNotNull(result);
		assertEquals("Electronics", result.getName());
		verify(categoryRepository, times(1)).findById(1);
	}

	@Test
	void testGetCategoryById_CategoryNotFound() {
		when(categoryRepository.findById(1)).thenReturn(Optional.empty());

		Exception exception = assertThrows(NotFoundException.class, () -> categoryService.getCategoryById(1));

		assertTrue(exception.getMessage().contains(ErrorMessages.CATEGORY_NOT_FOUND));
		verify(categoryRepository, times(1)).findById(1);
	}

	@Test
	void testCreateCategory_NewCategory() {
		Category newCategory = new Category();
		newCategory.setCategoryId(1);
		newCategory.setName("Electronics");
		newCategory.setDescription("Electronic items");
		newCategory.setCatPrefix("E");

		Category savedCategory = new Category();
		savedCategory.setCategoryId(1);
		savedCategory.setName("Electronics");
		savedCategory.setDescription("Electronic items");
		savedCategory.setCatPrefix("E");

		when(categoryRepository.findByNameAndDeletedFalse("Electronics")).thenReturn(Optional.empty());
		when(categoryRepository.findByNameAndDeletedTrue("Electronics")).thenReturn(Optional.empty());
		when(categoryRepository.save(any(Category.class))).thenReturn(savedCategory);

		CategoryDTO result = categoryService.createCategory(newCategory);

		assertNotNull(result);
		assertEquals("Electronics", result.getName());
		verify(categoryRepository, times(1)).save(any(Category.class));
	}

	@Test
	void testCreateCategory_CategoryAlreadyExists() {
		Category existingCategory = new Category();
		existingCategory.setCategoryId(1);
		existingCategory.setName("Electronics");
		existingCategory.setDescription("Electronic items");
		existingCategory.setCatPrefix("E");

		when(categoryRepository.findByNameAndDeletedFalse("Electronics")).thenReturn(Optional.empty());
		when(categoryRepository.findByNameAndDeletedTrue("Electronics")).thenReturn(Optional.of(existingCategory));

		when(categoryRepository.findByNameAndDeletedFalse("Electronics")).thenReturn(Optional.of(existingCategory));

		Exception exception = assertThrows(IllegalArgumentException.class,
				() -> categoryService.createCategory(existingCategory));

		assertTrue(exception.getMessage().contains(ErrorMessages.CATEGORY_NAME_EXISTS));
		verify(categoryRepository, times(1)).findByNameAndDeletedFalse("Electronics");
		verify(categoryRepository, never()).save(any(Category.class));
	}

	@Test
	void testUpdateCategory_CategoryExists() {
		Category existingCategory = new Category();
		existingCategory.setCategoryId(1);
		existingCategory.setName("Electronics");
		existingCategory.setDescription("Electronic items");
		existingCategory.setCatPrefix("E");

		Category updatedCategory = new Category();
		updatedCategory.setCategoryId(1);
		updatedCategory.setName("Updated Electronics");
		updatedCategory.setDescription("Updated items");
		updatedCategory.setCatPrefix("U");

		when(categoryRepository.findById(1)).thenReturn(Optional.of(existingCategory));
		when(categoryRepository.findByNameAndDeletedFalse("Updated Electronics")).thenReturn(Optional.empty());
		when(categoryRepository.findByNameAndDeletedTrue("Updated Electronics")).thenReturn(Optional.empty());

		when(categoryRepository.findById(1)).thenReturn(Optional.of(existingCategory));
		when(categoryRepository.save(any(Category.class))).thenReturn(existingCategory);

		CategoryDTO result = categoryService.updateCategory(1, updatedCategory);

		assertNotNull(result);
		assertEquals("Updated Electronics", result.getName());
		verify(categoryRepository, times(1)).findById(1);
		verify(categoryRepository, times(1)).save(existingCategory);
	}

	@Test
	void testUpdateCategory_CategoryNotFound() {
		Category updatedCategory = new Category();
		updatedCategory.setCategoryId(1);
		updatedCategory.setName("Updated Electronics");
		updatedCategory.setDescription("Updated items");
		updatedCategory.setCatPrefix("U");

		when(categoryRepository.findById(1)).thenReturn(Optional.empty());

		Exception exception = assertThrows(RuntimeException.class,
				() -> categoryService.updateCategory(1, updatedCategory));

		assertTrue(exception.getMessage().contains(ErrorMessages.CATEGORY_NOT_FOUND));
		verify(categoryRepository, times(1)).findById(1);
	}

	@Test
	void testSoftDeleteCategory_CategoryExists() {
		Category existingCategory = new Category();
		existingCategory.setCategoryId(1);
		existingCategory.setName("Electronics");
		existingCategory.setDescription("Electronic items");
		existingCategory.setCatPrefix("E");

		when(categoryRepository.findById(1)).thenReturn(Optional.of(existingCategory));

		categoryService.softDeleteCategory(1);

		assertTrue(existingCategory.isDeleted());
		verify(categoryRepository, times(1)).findById(1);
		verify(categoryRepository, times(1)).save(existingCategory);
	}

	@Test
	void testSoftDeleteCategory_CategoryNotFound() {
		when(categoryRepository.findById(1)).thenReturn(Optional.empty());

		Exception exception = assertThrows(NotFoundException.class, () -> categoryService.softDeleteCategory(1));

		assertTrue(exception.getMessage().contains(ErrorMessages.CATEGORY_NOT_FOUND));
		verify(categoryRepository, times(1)).findById(1);
	}

	@Test
	void testGetAllExistCategories() {
		Category category1 = new Category();
		category1.setCategoryId(1);
		category1.setName("Electronics");
		category1.setDescription("Electronic items");
		category1.setCatPrefix("E");
		category1.setDeleted(false);

		Category category2 = new Category();
		category2.setCategoryId(2);
		category2.setName("Clothing");
		category2.setDescription("Clothing items");
		category2.setCatPrefix("C");
		category2.setDeleted(false);

		Category category3 = new Category();
		category3.setCategoryId(3);
		category3.setName("Furniture");
		category3.setDescription("Furniture items");
		category3.setCatPrefix("F");
		category3.setDeleted(true); // This category is deleted and should not be returned

		when(categoryRepository.findByDeletedFalse()).thenReturn(Arrays.asList(category1, category2));

		List<CategoryDTO> result = categoryService.getAllExistCategories();

		assertEquals(2, result.size());
		assertEquals("Electronics", result.get(0).getName());
		assertEquals("Clothing", result.get(1).getName());
		verify(categoryRepository, times(1)).findByDeletedFalse();
	}

	@Test
	void testDeleteCategory_CategoryExists() {
		Category existingCategory = new Category();
		existingCategory.setCategoryId(1);
		existingCategory.setName("Electronics");
		existingCategory.setDescription("Electronic items");
		existingCategory.setCatPrefix("E");

		when(categoryRepository.findById(1)).thenReturn(Optional.of(existingCategory));

		categoryService.deleteCategory(1);

		verify(categoryRepository, times(1)).findById(1);
		verify(categoryRepository, times(1)).delete(existingCategory);
	}

	@Test
	void testDeleteCategory_CategoryNotFound() {
		when(categoryRepository.findById(1)).thenReturn(Optional.empty());

		Exception exception = assertThrows(NotFoundException.class, () -> categoryService.deleteCategory(1));

		assertTrue(exception.getMessage().contains(ErrorMessages.CATEGORY_NOT_FOUND));
		verify(categoryRepository, times(1)).findById(1);
		verify(categoryRepository, never()).delete(any(Category.class));
	}
}
