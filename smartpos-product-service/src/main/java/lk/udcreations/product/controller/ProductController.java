package lk.udcreations.product.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lk.udcreations.common.dto.product.CreateProductDTO;
import lk.udcreations.common.dto.product.ProductDTO;
import lk.udcreations.product.service.ProductService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/product")
@Tag(name = "Product API", description = "Endpoints for managing products, including creation, update, deletion, and status checks.")
public class ProductController {

    private final ProductService productService;

    @Value("${file.upload-dir}")
    private String uploadDir;

    public ProductController(ProductService productService) {
        super();
        this.productService = productService;
    }

    /**
     * Get all products
     */
    @Operation(
            summary = "Get all products",
            description = "Retrieve all products, including soft-deleted ones.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved products",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProductDTO.class)))
    })
    @GetMapping("/all")
    public ResponseEntity<List<ProductDTO>> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    /**
     * Get all non-deleted products
     */
    @Operation(
            summary = "Get active products",
            description = "Retrieve all non-deleted (active) products.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved active products",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProductDTO.class)))
    })
    @GetMapping
    public ResponseEntity<List<ProductDTO>> getAllExistProducts() {
        return ResponseEntity.ok(productService.getAllExistProducts());
    }

    /**
     * Get a product by ID
     */
    @Operation(
            summary = "Get a product by ID",
            description = "Retrieve product details by its ID.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Product found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProductDTO.class))),
            @ApiResponse(
                    responseCode = "404",
                    description = "Product not found",
                    content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<ProductDTO> getProductById(
            @Parameter(description = "ID of the product to retrieve", required = true) @PathVariable Integer id) {
        return ResponseEntity.ok(productService.getProductDTOById(id));
    }

    /**
     * Create a new product
     */
    @Operation(
            summary = "Create a new product",
            description = "Add a new product to the system.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Product created successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProductDTO.class))),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input data",
                    content = @Content)
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProductDTO createProduct(
            @RequestParam("product") String createProduct,
            @RequestParam("file") MultipartFile file,
            @Parameter(hidden = true) @AuthenticationPrincipal String loggedInUsername) {
        return productService.createProduct(createProduct, file, loggedInUsername);
    }

    /**
     * Update a product
     */
    @Operation(
            summary = "Update a product",
            description = "Update product details by its ID.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Product updated successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProductDTO.class))),
            @ApiResponse(
                    responseCode = "404",
                    description = "Product not found",
                    content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<ProductDTO> updateProduct(
            @Parameter(description = "ID of the product to update", required = true) @PathVariable Integer id,
            @Parameter(description = "Updated product details", required = true) @Valid @RequestBody CreateProductDTO product,
            @Parameter(hidden = true) @AuthenticationPrincipal String loggedInUsername) {

        return ResponseEntity.ok(productService.updateProduct(id, product, loggedInUsername));
    }

    /**
     * Delete a product
     */
    @Operation(
            summary = "Soft delete a product",
            description = "Soft delete a product by marking it as deleted.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Product soft-deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Product not found", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(
            @Parameter(description = "ID of the product to delete", required = true) @PathVariable Integer id,
            @Parameter(hidden = true) @AuthenticationPrincipal String loggedInUsername) {
        productService.softDeleteProduct(id, loggedInUsername);
        return ResponseEntity.noContent().build();
    }

    /**
     * Check if a product is deleted by productId
     */
    @Operation(
            summary = "Check if a product is deleted by productId",
            description = "Returns true if the product with the given productId is soft-deleted.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Check completed",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Boolean.class))),
            @ApiResponse(
                    responseCode = "404",
                    description = "Product not found",
                    content = @Content)
    })
    @GetMapping("productId/deleted/{productId}")
    public boolean checkProductDeletedByProductId(
            @Parameter(description = "ProductId of the product to check", required = true) @PathVariable String productId) {
        return productService.checkProductDeletedByProductId(productId);
    }

    /**
     * Check if a product is deleted by id
     */
    @Operation(
            summary = "Check if a product is deleted by id",
            description = "Returns true if the product with the given id is soft-deleted.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Check completed",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Boolean.class))),
            @ApiResponse(
                    responseCode = "404",
                    description = "Product not found",
                    content = @Content)
    })
    @GetMapping("id/deleted/{id}")
    public boolean checkProductDeletedById(
            @Parameter(description = "ID of the product to check", required = true) @PathVariable Integer id) {
        return productService.checkProductDeletedById(id);
    }

    /**
     * Check if a product is enabled by productId
     */
    @Operation(
            summary = "Check if a product is enabled by productId",
            description = "Returns true if the product with the given productId is enabled.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Check completed",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Boolean.class))),
            @ApiResponse(
                    responseCode = "404",
                    description = "Product not found",
                    content = @Content)
    })
    @GetMapping("productId/enabled/{productId}")
    public boolean checkProductEnabledByProductId(
            @Parameter(description = "ProductId of the product to check", required = true) @PathVariable String productId) {
        return productService.checkProductEnabledByProductId(productId);
    }

    /**
     * Check if a product is enabled by id
     */
    @Operation(
            summary = "Check if a product is enabled by id",
            description = "Returns true if the product with the given id is enabled.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Check completed",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Boolean.class))),
            @ApiResponse(
                    responseCode = "404",
                    description = "Product not found",
                    content = @Content)
    })
    @GetMapping("id/enabled/{id}")
    public boolean checkProductEnabledById(
            @Parameter(description = "ID of the product to check", required = true) @PathVariable Integer id) {
        return productService.checkProductEnabledById(id);
    }
}