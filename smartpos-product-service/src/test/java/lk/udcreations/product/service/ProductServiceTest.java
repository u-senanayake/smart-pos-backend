package lk.udcreations.product.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
import lk.udcreations.common.dto.distributor.DistributorDTO;
import lk.udcreations.common.dto.inventory.InventoryDTO;
import lk.udcreations.common.dto.product.CreateProductDTO;
import lk.udcreations.common.dto.product.ProductDTO;
import lk.udcreations.common.dto.user.CreatedUpdatedUserDTO;
import lk.udcreations.common.dto.user.UsersDTO;
import lk.udcreations.product.constants.ErrorMessages;
import lk.udcreations.product.entity.Category;
import lk.udcreations.product.entity.Distributor;
import lk.udcreations.product.entity.Inventory;
import lk.udcreations.product.entity.Product;
import lk.udcreations.product.exception.NotFoundException;
import lk.udcreations.product.repository.CategoryRepository;
import lk.udcreations.product.repository.DistributorRepository;
import lk.udcreations.product.repository.InventoryRepository;
import lk.udcreations.product.repository.ProductRepository;
import lk.udcreations.product.security.AuthUtils;

class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private InventoryRepository inventoryRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private DistributorRepository distributorRepository;

    @Mock
    private AuthUtils authUtils;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private ProductService productService;

    private Integer adminUserId;
    private UsersDTO mockUser;
    private Product product1;
    private Product product2;
    private Category mockCategory;
    private Distributor mockDistributor;
    private Inventory mockInventory;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Mock a logged-in admin user
        adminUserId = 999;
        mockUser = new UsersDTO();
        mockUser.setUserId(adminUserId);

        // Mock Category
        mockCategory = new Category();
        mockCategory.setCategoryId(1);
        mockCategory.setName("Test Category");

        // Mock Distributor
        mockDistributor = new Distributor();
        mockDistributor.setDistributorId(1);
        mockDistributor.setCompanyName("Test Distributor");

        // Mock Inventory
        mockInventory = new Inventory();
        mockInventory.setInventoryId(1);
        mockInventory.setProductId(1);
        mockInventory.setQuantity(10);
        mockInventory.setStockAlertLevel(2);
        mockInventory.setStockWarningLevel(5);

        // Create test products
        product1 = new Product();
        product1.setId(1);
        product1.setProductId("PROD001");
        product1.setSku("SKU001");
        product1.setProductName("Product 1");
        product1.setDescription("Description 1");
        product1.setCategoryId(1);
        product1.setDistributorId(1);
        product1.setPrice(new BigDecimal("100.00"));
        product1.setCostPrice(new BigDecimal("50.00"));
        product1.setMinPrice(new BigDecimal("70.00"));
        product1.setManufactureDate(LocalDate.now());
        product1.setExpireDate(LocalDate.now().plusYears(1));
        product1.setEnabled(true);
        product1.setDeleted(false);
        product1.setCreatedUserId(adminUserId);
        product1.setUpdatedUserId(adminUserId);

        product2 = new Product();
        product2.setId(2);
        product2.setProductId("PROD002");
        product2.setSku("SKU002");
        product2.setProductName("Product 2");
        product2.setDescription("Description 2");
        product2.setCategoryId(1);
        product2.setDistributorId(1);
        product2.setPrice(new BigDecimal("200.00"));
        product2.setCostPrice(new BigDecimal("150.00"));
        product2.setMinPrice(new BigDecimal("180.00"));
        product2.setManufactureDate(LocalDate.now());
        product2.setExpireDate(LocalDate.now().plusYears(2));
        product2.setEnabled(true);
        product2.setDeleted(false);
        product2.setCreatedUserId(adminUserId);
        product2.setUpdatedUserId(adminUserId);

        // Mock authUtils methods
        when(authUtils.getLoggedInUser()).thenReturn(mockUser);
        when(authUtils.getUserById(any(Integer.class))).thenReturn(mockUser);

        // Mock repository methods
        when(categoryRepository.findById(1)).thenReturn(Optional.of(mockCategory));
        when(distributorRepository.findById(1)).thenReturn(Optional.of(mockDistributor));
        when(inventoryRepository.findByProductId(1)).thenReturn(Optional.of(mockInventory));
        when(inventoryRepository.findByProductId(2)).thenReturn(Optional.of(mockInventory));

        // Mock modelMapper
        when(modelMapper.map(any(), any())).thenAnswer(invocation -> {
            Object source = invocation.getArgument(0);
            Class<?> targetClass = invocation.getArgument(1);

            if (source instanceof Product && targetClass == ProductDTO.class) {
                Product product = (Product) source;
                ProductDTO dto = new ProductDTO();
                dto.setId(product.getId());
                dto.setProductId(product.getProductId());
                dto.setSku(product.getSku());
                dto.setProductName(product.getProductName());
                dto.setDescription(product.getDescription());
                dto.setPrice(product.getPrice());
                dto.setCostPrice(product.getCostPrice());
                dto.setMinPrice(product.getMinPrice());
                dto.setManufactureDate(product.getManufactureDate());
                dto.setExpireDate(product.getExpireDate());
                dto.setEnabled(product.isEnabled());
                dto.setDeleted(product.isDeleted());
                return dto;
            } else if (source instanceof Category && targetClass == CategoryDTO.class) {
                Category category = (Category) source;
                CategoryDTO dto = new CategoryDTO();
                dto.setCategoryId(category.getCategoryId());
                dto.setName(category.getName());
                return dto;
            } else if (source instanceof Distributor && targetClass == DistributorDTO.class) {
                Distributor distributor = (Distributor) source;
                DistributorDTO dto = new DistributorDTO();
                dto.setDistributorId(distributor.getDistributorId());
                dto.setCompanyName(distributor.getCompanyName());
                return dto;
            } else if (source instanceof Inventory && targetClass == InventoryDTO.class) {
                Inventory inventory = (Inventory) source;
                InventoryDTO dto = new InventoryDTO();
                dto.setInventoryId(inventory.getInventoryId());
                dto.setQuantity(inventory.getQuantity());
                dto.setStockAlertLevel(inventory.getStockAlertLevel());
                dto.setStockWarningLevel(inventory.getStockWarningLevel());
                return dto;
            } else if (source instanceof UsersDTO && targetClass == CreatedUpdatedUserDTO.class) {
                UsersDTO user = (UsersDTO) source;
                CreatedUpdatedUserDTO dto = new CreatedUpdatedUserDTO();
                dto.setUserId(user.getUserId());
                return dto;
            }

            return null;
        });
    }

    @Test
    void testGetAllProducts() {
        when(productRepository.findAll()).thenReturn(Arrays.asList(product1, product2));

        List<ProductDTO> result = productService.getAllProducts();

        assertEquals(2, result.size());
        assertEquals("Product 1", result.get(0).getProductName());
        assertEquals("Product 2", result.get(1).getProductName());
        verify(productRepository, times(1)).findAll();
    }

    @Test
    void testGetAllExistProducts() {
        when(productRepository.findByDeletedFalse()).thenReturn(Arrays.asList(product1, product2));

        List<ProductDTO> result = productService.getAllExistProducts();

        assertEquals(2, result.size());
        assertEquals("Product 1", result.get(0).getProductName());
        assertEquals("Product 2", result.get(1).getProductName());
        verify(productRepository, times(1)).findByDeletedFalse();
    }

    @Test
    void testGetProductDTOById_ProductExists() {
        when(productRepository.findById(1)).thenReturn(Optional.of(product1));

        ProductDTO result = productService.getProductDTOById(1);

        assertNotNull(result);
        assertEquals("Product 1", result.getProductName());
        verify(productRepository, times(1)).findById(1);
    }

    @Test
    void testGetProductDTOById_ProductNotFound() {
        when(productRepository.findById(999)).thenReturn(Optional.empty());

        Exception exception = assertThrows(NotFoundException.class, () -> productService.getProductDTOById(999));

        assertTrue(exception.getMessage().contains(ErrorMessages.PRODUCT_NOT_FOUND));
        verify(productRepository, times(1)).findById(999);
    }

    @Test
    void testCheckProductDeletedByProductId_ProductDeleted() {
        when(productRepository.findByProductIdAndDeletedTrue("PROD001")).thenReturn(Optional.of(product1));

        boolean result = productService.checkProductDeletedByProductId("PROD001");

        assertTrue(result);
        verify(productRepository, times(1)).findByProductIdAndDeletedTrue("PROD001");
    }

    @Test
    void testCheckProductDeletedByProductId_ProductNotDeleted() {
        when(productRepository.findByProductIdAndDeletedTrue("PROD001")).thenReturn(Optional.empty());

        boolean result = productService.checkProductDeletedByProductId("PROD001");

        assertFalse(result);
        verify(productRepository, times(1)).findByProductIdAndDeletedTrue("PROD001");
    }

    @Test
    void testCheckProductDeletedById_ProductDeleted() {
        when(productRepository.findByIdAndDeletedTrue(1)).thenReturn(Optional.of(product1));

        boolean result = productService.checkProductDeletedById(1);

        assertTrue(result);
        verify(productRepository, times(1)).findByIdAndDeletedTrue(1);
    }

    @Test
    void testCheckProductDeletedById_ProductNotDeleted() {
        when(productRepository.findByIdAndDeletedTrue(1)).thenReturn(Optional.empty());

        boolean result = productService.checkProductDeletedById(1);

        assertFalse(result);
        verify(productRepository, times(1)).findByIdAndDeletedTrue(1);
    }

    @Test
    void testCheckProductEnabledByProductId_ProductEnabled() {
        when(productRepository.findByProductIdAndEnabledTrue("PROD001")).thenReturn(Optional.of(product1));

        boolean result = productService.checkProductEnabledByProductId("PROD001");

        assertTrue(result);
        verify(productRepository, times(1)).findByProductIdAndEnabledTrue("PROD001");
    }

    @Test
    void testCheckProductEnabledByProductId_ProductNotEnabled() {
        when(productRepository.findByProductIdAndEnabledTrue("PROD001")).thenReturn(Optional.empty());

        boolean result = productService.checkProductEnabledByProductId("PROD001");

        assertFalse(result);
        verify(productRepository, times(1)).findByProductIdAndEnabledTrue("PROD001");
    }

    @Test
    void testCheckProductEnabledById_ProductEnabled() {
        when(productRepository.findByIdAndEnabledTrue(1)).thenReturn(Optional.of(product1));

        boolean result = productService.checkProductEnabledById(1);

        assertTrue(result);
        verify(productRepository, times(1)).findByIdAndEnabledTrue(1);
    }

    @Test
    void testCheckProductEnabledById_ProductNotEnabled() {
        when(productRepository.findByIdAndEnabledTrue(1)).thenReturn(Optional.empty());

        boolean result = productService.checkProductEnabledById(1);

        assertFalse(result);
        verify(productRepository, times(1)).findByIdAndEnabledTrue(1);
    }

    @Test
    void testCreateProduct_NewProduct() {
        CreateProductDTO createProductDTO = new CreateProductDTO();
        createProductDTO.setProductName("New Product");
        createProductDTO.setSku("NEWSKU123");
        createProductDTO.setDescription("New Description");
        createProductDTO.setCategoryId(1);
        createProductDTO.setDistributorId(1);
        createProductDTO.setPrice(new BigDecimal("150.00"));
        createProductDTO.setCostPrice(new BigDecimal("100.00"));
        createProductDTO.setMinPrice(new BigDecimal("120.00"));
        createProductDTO.setManufactureDate(LocalDate.now().toString());
        createProductDTO.setExpireDate(LocalDate.now().plusYears(1).toString());
        createProductDTO.setEnabled(true);
        createProductDTO.setInitialStock(10);
        createProductDTO.setStockAlertLevel(2);
        createProductDTO.setStockWarningLevel(5);

        Product newProduct = new Product();
        newProduct.setId(3);
        newProduct.setProductId("PROD003");
        newProduct.setSku("NEWSKU123");
        newProduct.setProductName("New Product");
        newProduct.setDescription("New Description");
        newProduct.setCategoryId(1);
        newProduct.setDistributorId(1);
        newProduct.setPrice(new BigDecimal("150.00"));
        newProduct.setCostPrice(new BigDecimal("100.00"));
        newProduct.setMinPrice(new BigDecimal("120.00"));
        newProduct.setEnabled(true);
        newProduct.setDeleted(false);
        newProduct.setCreatedUserId(adminUserId);
        newProduct.setUpdatedUserId(adminUserId);

        when(productRepository.findByProductNameAndDeletedFalse("New Product")).thenReturn(Optional.empty());
        when(productRepository.findByProductNameAndDeletedTrue("New Product")).thenReturn(Optional.empty());
        when(productRepository.save(any(Product.class))).thenReturn(newProduct);
        when(inventoryRepository.findByProductId(3)).thenReturn(Optional.of(mockInventory));
        when(inventoryRepository.save(any(Inventory.class))).thenReturn(mockInventory);

        ProductDTO result = productService.createProduct(createProductDTO);

        assertNotNull(result);
        assertEquals("New Product", result.getProductName());
        verify(productRepository, times(1)).save(any(Product.class));
        verify(inventoryRepository, times(1)).save(any(Inventory.class));
    }

    @Test
    void testCreateProduct_ProductAlreadyExists() {
        CreateProductDTO createProductDTO = new CreateProductDTO();
        createProductDTO.setProductName("Product 1");

        when(productRepository.findByProductNameAndDeletedFalse("Product 1")).thenReturn(Optional.of(product1));

        Exception exception = assertThrows(IllegalArgumentException.class, 
                () -> productService.createProduct(createProductDTO));

        assertTrue(exception.getMessage().contains(ErrorMessages.PRODUCT_NAME_EXISTS));
        verify(productRepository, times(1)).findByProductNameAndDeletedFalse("Product 1");
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void testUpdateProduct_ProductExists() {
        CreateProductDTO updateProductDTO = new CreateProductDTO();
        updateProductDTO.setProductName("Updated Product");
        updateProductDTO.setSku("UPDSKU123");
        updateProductDTO.setDescription("Updated Description");
        updateProductDTO.setCategoryId(1);
        updateProductDTO.setDistributorId(1);
        updateProductDTO.setPrice(new BigDecimal("160.00"));
        updateProductDTO.setCostPrice(new BigDecimal("110.00"));
        updateProductDTO.setMinPrice(new BigDecimal("130.00"));
        updateProductDTO.setManufactureDate(LocalDate.now().toString());
        updateProductDTO.setExpireDate(LocalDate.now().plusYears(1).toString());
        updateProductDTO.setEnabled(true);

        when(productRepository.findById(1)).thenReturn(Optional.of(product1));
        when(productRepository.save(any(Product.class))).thenReturn(product1);

        ProductDTO result = productService.updateProduct(1, updateProductDTO);

        assertNotNull(result);
        assertEquals("Updated Product", product1.getProductName());
        assertEquals("UPDSKU123", product1.getSku());
        assertEquals(new BigDecimal("160.00"), product1.getPrice());
        verify(productRepository, times(1)).findById(1);
        verify(productRepository, times(1)).save(product1);
    }

    @Test
    void testUpdateProduct_ProductNotFound() {
        CreateProductDTO updateProductDTO = new CreateProductDTO();
        updateProductDTO.setProductName("Updated Product");

        when(productRepository.findById(999)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, 
                () -> productService.updateProduct(999, updateProductDTO));

        assertTrue(exception.getMessage().contains(ErrorMessages.PRODUCT_NOT_FOUND));
        verify(productRepository, times(1)).findById(999);
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void testSoftDeleteProduct_ProductExists() {
        when(productRepository.findById(1)).thenReturn(Optional.of(product1));

        productService.softDeleteProduct(1);

        assertTrue(product1.isDeleted());
        assertNotNull(product1.getDeletedAt());
        assertEquals(adminUserId, product1.getDeletedUserId());
        verify(productRepository, times(1)).findById(1);
        verify(productRepository, times(1)).save(product1);
    }

    @Test
    void testSoftDeleteProduct_ProductNotFound() {
        when(productRepository.findById(999)).thenReturn(Optional.empty());

        Exception exception = assertThrows(NotFoundException.class, () -> productService.softDeleteProduct(999));

        assertTrue(exception.getMessage().contains(ErrorMessages.PRODUCT_NOT_FOUND));
        verify(productRepository, times(1)).findById(999);
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void testDeleteProduct_ProductExists() {
        when(productRepository.findById(1)).thenReturn(Optional.of(product1));

        productService.deleteProduct(1);

        verify(productRepository, times(1)).findById(1);
        verify(productRepository, times(1)).delete(product1);
    }

    @Test
    void testDeleteProduct_ProductNotFound() {
        when(productRepository.findById(999)).thenReturn(Optional.empty());

        Exception exception = assertThrows(NotFoundException.class, () -> productService.deleteProduct(999));

        assertTrue(exception.getMessage().contains(ErrorMessages.PRODUCT_NOT_FOUND));
        verify(productRepository, times(1)).findById(999);
        verify(productRepository, never()).delete(any(Product.class));
    }
}
