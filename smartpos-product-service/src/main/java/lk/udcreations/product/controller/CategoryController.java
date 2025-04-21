package lk.udcreations.product.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lk.udcreations.common.dto.category.CategoryDTO;
import lk.udcreations.product.entity.Category;
import lk.udcreations.product.service.CategoryService;

@RestController
@RequestMapping("/api/v1/category")
@Tag(name = "Category API", description = "Endpoints for managing product categories")
public class CategoryController {

	private final CategoryService categoryService;


	public CategoryController(CategoryService categoryService) {
		super();
		this.categoryService = categoryService;
	}

	/** Get all categories */
	@Operation(summary = "Get all categories", description = "Retrieve all categories, including soft-deleted ones.")
	@ApiResponse(responseCode = "200", description = "Successfully retrieved categories")
	@GetMapping("/all")
	public ResponseEntity<List<CategoryDTO>> getAllcategories() {
		return ResponseEntity.ok(categoryService.getAllcategories());
	}


	/** Get all non-deleted categories */
	@Operation(summary = "Get all existing categories", description = "Retrieve all non-deleted categories.")
	@ApiResponse(responseCode = "200", description = "Successfully retrieved active categories")
	@GetMapping
	public ResponseEntity<List<CategoryDTO>> getAllExistCategories() {
		return ResponseEntity.ok(categoryService.getAllExistCategories());
	}

	/** Get a category by ID */
	@Operation(summary = "Get a category by ID", description = "Retrieve a category's details by its ID.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Category found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CategoryDTO.class))),
			@ApiResponse(responseCode = "404", description = "Category not found", content = @Content) })
	@GetMapping("/{categoryId}")
	public ResponseEntity<CategoryDTO> getcategoryById(
			@Parameter(description = "ID of the category to retrieve") @PathVariable Integer categoryId) {
		return ResponseEntity.ok(categoryService.getCategoryById(categoryId));
	}

	/** Create a new category */
	@Operation(summary = "Create a new category", description = "Add a new category to the system.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "201", description = "Category created successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CategoryDTO.class))),
			@ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content) })
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public CategoryDTO createCategory(
			@Parameter(description = "Category object to be created") @Valid @RequestBody Category category) {
		return categoryService.createcategory(category);
	}

	/** Update a category */
	@Operation(summary = "Update a category", description = "Update category details by its ID.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Category updated successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CategoryDTO.class))),
			@ApiResponse(responseCode = "404", description = "Category not found", content = @Content) })
	@PutMapping("/{categoryId}")
	public ResponseEntity<CategoryDTO> updateCategory(
			@Parameter(description = "ID of the category to update") @PathVariable Integer categoryId,
			@Parameter(description = "Updated category details") @Valid @RequestBody Category updatedCategory) {
		return ResponseEntity.ok(categoryService.updateCategory(categoryId, updatedCategory));
	}

	/** Delete a category */
	@Operation(summary = "Soft delete a category", description = "Soft delete a category by its ID.")
	@ApiResponses(value = { @ApiResponse(responseCode = "204", description = "Category soft-deleted successfully"),
			@ApiResponse(responseCode = "404", description = "Category not found", content = @Content) })
	@DeleteMapping("/{categoryId}")
	public ResponseEntity<Void> deleteCategory(
			@Parameter(description = "ID of the category to delete") @PathVariable Integer categoryId) {
		categoryService.softDeleteCategory(categoryId);
		return ResponseEntity.noContent().build();
	}
}
