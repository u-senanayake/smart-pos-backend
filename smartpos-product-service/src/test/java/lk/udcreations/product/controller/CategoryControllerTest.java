package lk.udcreations.product.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;

import lk.udcreations.product.controller.CategoryController;
import lk.udcreations.common.dto.category.CategoryDTO;
import lk.udcreations.product.service.CategoryService;

class CategoryControllerTest {

	@Mock
	private CategoryService categoryService;

	@InjectMocks
	private CategoryController categoryController;

	private MockMvc mockMvc;
	private ObjectMapper objectMapper;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		mockMvc = MockMvcBuilders.standaloneSetup(categoryController).build();
		objectMapper = new ObjectMapper();
	}


	@Test
	void testGetAllCategories() throws Exception {
		CategoryDTO category1 = new CategoryDTO();
		category1.setCategoryId(1);
		category1.setName("Electronics");
		category1.setDescription("Electronic items");
		category1.setCatPrefix("E");
		category1.setEnabled(true);
		category1.setDeleted(false);

		CategoryDTO category2 = new CategoryDTO();
		category2.setCategoryId(2);
		category2.setName("Clothing");
		category2.setDescription("Clothing items");
		category2.setCatPrefix("C");
		category2.setEnabled(true);
		category2.setDeleted(false);

		List<CategoryDTO> categories = Arrays.asList(category1, category2);

		when(categoryService.getAllcategories()).thenReturn(categories);

		mockMvc.perform(get("/api/v1/category/all")).andExpect(status().isOk()).andExpect(jsonPath("$.size()").value(2))
				.andExpect(jsonPath("$[0].name").value("Electronics"))
				.andExpect(jsonPath("$[1].name").value("Clothing"));

		verify(categoryService, times(1)).getAllcategories();
	}

	@Test
	void testGetAllExistCategories() throws Exception {
		CategoryDTO category1 = new CategoryDTO();
		category1.setCategoryId(1);
		category1.setName("Electronics");
		category1.setDescription("Electronic items");
		category1.setCatPrefix("E");
		category1.setEnabled(true);
		category1.setDeleted(false);

		CategoryDTO category2 = new CategoryDTO();
		category2.setCategoryId(2);
		category2.setName("Clothing");
		category2.setDescription("Clothing items");
		category2.setCatPrefix("C");
		category2.setEnabled(true);
		category2.setDeleted(false);

		List<CategoryDTO> categories = Arrays.asList(category1, category2);

		when(categoryService.getAllExistCategories()).thenReturn(categories);

		mockMvc.perform(get("/api/v1/category")).andExpect(status().isOk()).andExpect(jsonPath("$.size()").value(2))
				.andExpect(jsonPath("$[0].name").value("Electronics"))
				.andExpect(jsonPath("$[1].name").value("Clothing"));

		verify(categoryService, times(1)).getAllExistCategories();
	}

	@Test
	void testGetCategoryById() throws Exception {
		CategoryDTO category = new CategoryDTO();
		category.setCategoryId(1);
		category.setName("Electronics");
		category.setDescription("Electronic items");
		category.setCatPrefix("E");
		category.setEnabled(true);
		category.setDeleted(false);

		when(categoryService.getCategoryById(1)).thenReturn(category);

		mockMvc.perform(get("/api/v1/category/1")).andExpect(status().isOk())
				.andExpect(jsonPath("$.name").value("Electronics"));

		verify(categoryService, times(1)).getCategoryById(1);
	}

	@Test
	void testCreateCategory() throws Exception {
		CategoryDTO inputCategoryDTO = new CategoryDTO();
		inputCategoryDTO.setName("Electronics");
		inputCategoryDTO.setDescription("Electronic items");
		inputCategoryDTO.setCatPrefix("E");
		inputCategoryDTO.setEnabled(true);

		when(categoryService.createcategory(any())).thenReturn(inputCategoryDTO);

		mockMvc.perform(post("/api/v1/category").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(inputCategoryDTO))).andExpect(status().isCreated())
				.andExpect(jsonPath("$.name").value("Electronics"));
	}



	@Test
	void testUpdateCategory() throws Exception {
		CategoryDTO updatedCategoryDTO = new CategoryDTO();
		updatedCategoryDTO.setCategoryId(1);
		updatedCategoryDTO.setName("Updated Electronics"); // Valid name
		updatedCategoryDTO.setDescription("Updated description"); // Optional, so it's fine
		updatedCategoryDTO.setCatPrefix("E"); // Valid prefix
		updatedCategoryDTO.setEnabled(true); // Required and valid
		updatedCategoryDTO.setDeleted(false);

		when(categoryService.updateCategory(eq(1), any())).thenReturn(updatedCategoryDTO);

		mockMvc.perform(put("/api/v1/category/1").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(updatedCategoryDTO))).andExpect(status().isOk())
				.andExpect(jsonPath("$.name").value("Updated Electronics"));

		verify(categoryService, times(1)).updateCategory(eq(1), any());
	}


	@Test
	void testDeleteCategory() throws Exception {
		doNothing().when(categoryService).softDeleteCategory(1);

		mockMvc.perform(delete("/api/v1/category/1")).andExpect(status().isNoContent());

		verify(categoryService, times(1)).softDeleteCategory(1);
	}
}
