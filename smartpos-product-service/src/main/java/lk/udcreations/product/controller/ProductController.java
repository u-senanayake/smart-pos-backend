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

import java.io.File;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/product")
@Tag(name = "Product API", description = "Endpoints for managing products")
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
    @Operation(summary = "Get all products", description = "Retrieve all products, including soft-deleted ones.")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved products")
    @GetMapping("/all")
    public ResponseEntity<List<ProductDTO>> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    /**
     * Get all non-deleted products
     */
    @Operation(summary = "Get active products", description = "Retrieve all non-deleted products.")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved active products")
    @GetMapping
    public ResponseEntity<List<ProductDTO>> getAllExistProducts() {
        return ResponseEntity.ok(productService.getAllExistProducts());
    }

    /**
     * Get a product by ID
     */
    @Operation(summary = "Get a product by ID", description = "Retrieve product details by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProductDTO.class))),
            @ApiResponse(responseCode = "404", description = "Product not found", content = @Content)})
    @GetMapping("/{id}")
    public ResponseEntity<ProductDTO> getProductById(
            @Parameter(description = "ID of the product to retrieve") @PathVariable Integer id) {
        return ResponseEntity.ok(productService.getProductDTOById(id));
    }

    /**
     * Create a new product
     */
    @Operation(summary = "Create a new product", description = "Add a new product to the system.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Product created successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProductDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content)})
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProductDTO createProduct(
            @Parameter(description = "Product object to be created") @Valid @RequestBody CreateProductDTO createProduct, @AuthenticationPrincipal String loggedInUsername) {
        return productService.createProduct(createProduct, loggedInUsername);
    }

    /**
     * Update a product
     */
    @Operation(summary = "Update a product", description = "Update product details by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product updated successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProductDTO.class))),
            @ApiResponse(responseCode = "404", description = "Product not found", content = @Content)})
//    @PutMapping("/{id}")
//    public ResponseEntity<ProductDTO> updateProduct(
//            @Parameter(description = "ID of the product to update") @PathVariable Integer id,
//            @Parameter(description = "Updated product details") @Valid @RequestBody CreateProductDTO updatedProduct, @AuthenticationPrincipal String loggedInUsername) {
//        return ResponseEntity.ok(productService.updateProduct(id, updatedProduct, loggedInUsername));
//    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductDTO> updateProduct(@PathVariable Integer id, @RequestParam("product") String product, @RequestParam("file") MultipartFile file,
                                                    @AuthenticationPrincipal String loggedInUsername) {

        return ResponseEntity.ok(productService.updateProduct(id, product, file, loggedInUsername));
    }

    @GetMapping("/image")
    public ResponseEntity<byte[]> getProductImage() throws IOException {
        File imgFile = new File(uploadDir + "c.jpg");
        if (!imgFile.exists()) {
            return ResponseEntity.notFound().build();
        }
        byte[] imageBytes = java.nio.file.Files.readAllBytes(imgFile.toPath());
        String contentType = java.nio.file.Files.probeContentType(imgFile.toPath());
        return ResponseEntity.ok()
                .header("Content-Type", contentType != null ? contentType : "application/octet-stream")
                .body(imageBytes);
    }
    /**
     * Delete a product
     */
    @Operation(summary = "Soft delete a product", description = "Soft delete a product by marking it as deleted.")
    @ApiResponses(value = {@ApiResponse(responseCode = "204", description = "Product soft-deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Product not found", content = @Content)})
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(
            @Parameter(description = "ID of the product to delete") @PathVariable Integer id, @AuthenticationPrincipal String loggedInUsername) {
        productService.softDeleteProduct(id, loggedInUsername);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("productId/deleted/{productId}")
    public boolean checkProductDeletedByProductId(
            @Parameter(description = "ID of the product to retrieve") @PathVariable String productId) {
        return productService.checkProductDeletedByProductId(productId);
    }

    @GetMapping("id/deleted/{id}")
    public boolean checkProductDeletedById(
            @Parameter(description = "ID of the product to retrieve") @PathVariable Integer id) {
        return productService.checkProductDeletedById(id);
    }

    @GetMapping("productId/enabled/{productId}")
    public boolean checkProductEnabledByProductId(
            @Parameter(description = "ID of the product to retrieve") @PathVariable String productId) {
        return productService.checkProductEnabledByProductId(productId);
    }

    @GetMapping("id/enabled/{id}")
    public boolean checkProductEnabledById(
            @Parameter(description = "ID of the product to retrieve") @PathVariable Integer id) {
        return productService.checkProductEnabledById(id);
    }
}