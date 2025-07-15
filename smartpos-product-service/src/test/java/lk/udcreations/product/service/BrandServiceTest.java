package lk.udcreations.product.service;

import lk.udcreations.common.dto.brand.BrandDTO;
import lk.udcreations.common.dto.user.UsersDTO;
import lk.udcreations.product.config.UserServiceClient;
import lk.udcreations.product.constants.ErrorMessages;
import lk.udcreations.product.entity.Brand;
import lk.udcreations.product.exception.NotFoundException;
import lk.udcreations.product.repository.BrandRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class BrandServiceTest {

	@Mock
	private BrandRepository brandRepository;

	@Mock
	private UserServiceClient userServiceClient;

	@Mock
	private ModelMapper modelMapper;

	@InjectMocks
	private BrandService brandService;

	private final String adminUsername = "admin";

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);

		// Mock a logged-in admin user
		Integer adminUserId = 999;
		UsersDTO mockUser = new UsersDTO();
		mockUser.setUserId(adminUserId);

		when(userServiceClient.getUserById(adminUserId)).thenReturn(mockUser);
		when(userServiceClient.getUserDetails(adminUsername)).thenReturn(mockUser);

		// Mock modelMapper
		when(modelMapper.map(any(), any())).thenAnswer(invocation -> {
			Object source = invocation.getArgument(0);
			Class<?> targetClass = invocation.getArgument(1);

			if (source instanceof Brand brand && targetClass == BrandDTO.class) {
				BrandDTO dto = new BrandDTO();
				dto.setBrandId(brand.getBrandId());
				dto.setName(brand.getName());
				dto.setDescription(brand.getDescription());
				dto.setEnabled(brand.isEnabled());
				dto.setDeleted(brand.isDeleted());
				return dto;
			}

			return null;
		});
	}

	@Test
	void testGetAllBrands() {
		Brand brand1 = new Brand();
		brand1.setBrandId(1);
		brand1.setName("Nike");
		brand1.setDescription("Sportswear brand");

		Brand brand2 = new Brand();
		brand2.setBrandId(2);
		brand2.setName("Adidas");
		brand2.setDescription("Another sportswear brand");

		when(brandRepository.findAll()).thenReturn(Arrays.asList(brand1, brand2));

		List<BrandDTO> result = brandService.getAllBrands();

		assertEquals(2, result.size());
		assertEquals("Nike", result.get(0).getName());
		assertEquals("Adidas", result.get(1).getName());
		verify(brandRepository, times(1)).findAll();
	}

	@Test
	void testGetBrandById_BrandExists() {
		Brand brand = new Brand();
		brand.setBrandId(1);
		brand.setName("Nike");
		brand.setDescription("Sportswear brand");
		brand.setEnabled(true);
		brand.setDeleted(false);
		brand.setCreatedAt(LocalDateTime.now());

		when(brandRepository.findById(1)).thenReturn(Optional.of(brand));

		BrandDTO result = brandService.getBrandById(1);

		assertNotNull(result);
		assertEquals("Nike", result.getName());
		verify(brandRepository, times(1)).findById(1);
	}

	@Test
	void testGetBrandById_BrandNotFound() {
		when(brandRepository.findById(1)).thenReturn(Optional.empty());

		Exception exception = assertThrows(NotFoundException.class, () -> brandService.getBrandById(1));

		assertTrue(exception.getMessage().contains(ErrorMessages.BRAND_NOT_FOUND));
		verify(brandRepository, times(1)).findById(1);
	}

	@Test
	void testCreateBrand_NewBrand() {
		Brand newBrand = new Brand();
		newBrand.setName("Nike");
		newBrand.setDescription("Sportswear brand");
		newBrand.setEnabled(true);
		newBrand.setDeleted(false);

		Brand savedBrand = new Brand();
		savedBrand.setBrandId(1);
		savedBrand.setName("Nike");
		savedBrand.setDescription("Sportswear brand");

		when(brandRepository.findByNameAndDeletedFalse("Nike")).thenReturn(Optional.empty());
		when(brandRepository.findByNameAndDeletedTrue("Nike")).thenReturn(Optional.empty());
		when(brandRepository.save(any(Brand.class))).thenReturn(savedBrand);

		BrandDTO result = brandService.createBrand(newBrand, adminUsername);

		assertNotNull(result);
		assertEquals("Nike", result.getName());
		verify(brandRepository, times(1)).save(any(Brand.class));
	}

	@Test
	void testCreateBrand_BrandAlreadyExists() {
		Brand existingBrand = new Brand();
		existingBrand.setBrandId(1);
		existingBrand.setName("Nike");
		existingBrand.setDescription("Sportswear brand");

		when(brandRepository.findByNameAndDeletedFalse("Nike")).thenReturn(Optional.of(existingBrand));

		Exception exception = assertThrows(IllegalArgumentException.class,
				() -> brandService.createBrand(existingBrand, adminUsername));

		assertTrue(exception.getMessage().contains(ErrorMessages.BRAND_NAME_EXISTS));
		verify(brandRepository, times(1)).findByNameAndDeletedFalse("Nike");
		verify(brandRepository, never()).save(any(Brand.class));
	}

	@Test
	void testUpdateBrand_BrandExists() {
		Brand existingBrand = new Brand();
		existingBrand.setBrandId(1);
		existingBrand.setName("Nike");
		existingBrand.setDescription("Sportswear brand");

		Brand updatedBrand = new Brand();
		updatedBrand.setBrandId(1);
		updatedBrand.setName("Updated Nike");
		updatedBrand.setDescription("Updated brand");

		when(brandRepository.findById(1)).thenReturn(Optional.of(existingBrand));
		when(brandRepository.findByNameAndDeletedFalse("Updated Nike")).thenReturn(Optional.empty());
		when(brandRepository.findByNameAndDeletedTrue("Updated Nike")).thenReturn(Optional.empty());

		when(brandRepository.findById(1)).thenReturn(Optional.of(existingBrand));
		when(brandRepository.save(any(Brand.class))).thenReturn(existingBrand);

		BrandDTO result = brandService.updateBrand(1, updatedBrand, adminUsername);

		assertNotNull(result);
		assertEquals("Updated Nike", result.getName());
		verify(brandRepository, times(1)).findById(1);
		verify(brandRepository, times(1)).save(existingBrand);
	}

	@Test
	void testUpdateBrand_BrandNotFound() {
		Brand updatedBrand = new Brand();
		updatedBrand.setBrandId(1);
		updatedBrand.setName("Updated Nike");
		updatedBrand.setDescription("Updated brand");

		when(brandRepository.findByNameAndDeletedFalse("Updated Nike")).thenReturn(Optional.empty());
		when(brandRepository.findByNameAndDeletedTrue("Updated Nike")).thenReturn(Optional.empty());

		when(brandRepository.findById(1)).thenReturn(Optional.empty());

		Exception exception = assertThrows(RuntimeException.class, () -> brandService.updateBrand(1, updatedBrand, adminUsername));

		assertTrue(exception.getMessage().contains(ErrorMessages.BRAND_NOT_FOUND));
		verify(brandRepository, times(1)).findById(1);
	}

	@Test
	void testSoftDeleteBrand_BrandExists() {
		Brand existingBrand = new Brand();
		existingBrand.setBrandId(1);
		existingBrand.setName("Nike");
		existingBrand.setDescription("Sportswear brand");

		when(brandRepository.findById(1)).thenReturn(Optional.of(existingBrand));

		brandService.softDeleteBrand(1, adminUsername);

		assertTrue(existingBrand.isDeleted());
		verify(brandRepository, times(1)).findById(1);
		verify(brandRepository, times(1)).save(existingBrand);
	}

	@Test
	void testSoftDeleteBrand_BrandNotFound() {
		when(brandRepository.findById(1)).thenReturn(Optional.empty());

		Exception exception = assertThrows(NotFoundException.class, () -> brandService.softDeleteBrand(1, adminUsername));

		assertTrue(exception.getMessage().contains(ErrorMessages.BRAND_NOT_FOUND));
		verify(brandRepository, times(1)).findById(1);
	}

	@Test
	void testGetAllExistBrands() {
		Brand brand1 = new Brand();
		brand1.setBrandId(1);
		brand1.setName("Nike");
		brand1.setDescription("Sportswear brand");
		brand1.setDeleted(false);

		Brand brand2 = new Brand();
		brand2.setBrandId(2);
		brand2.setName("Adidas");
		brand2.setDescription("Another sportswear brand");
		brand2.setDeleted(false);

		Brand brand3 = new Brand();
		brand3.setBrandId(3);
		brand3.setName("Puma");
		brand3.setDescription("Yet another sportswear brand");
		brand3.setDeleted(true); // This brand is deleted and should not be returned

		when(brandRepository.findByDeletedFalse()).thenReturn(Arrays.asList(brand1, brand2));

		List<BrandDTO> result = brandService.getAllExistBrands();

		assertEquals(2, result.size());
		assertEquals("Nike", result.get(0).getName());
		assertEquals("Adidas", result.get(1).getName());
		verify(brandRepository, times(1)).findByDeletedFalse();
	}

	@Test
	void testDeleteBrand_BrandExists() {
		Brand existingBrand = new Brand();
		existingBrand.setBrandId(1);
		existingBrand.setName("Nike");
		existingBrand.setDescription("Sportswear brand");

		when(brandRepository.findById(1)).thenReturn(Optional.of(existingBrand));

		brandService.deleteBrand(1);

		verify(brandRepository, times(1)).findById(1);
		verify(brandRepository, times(1)).delete(existingBrand);
	}

	@Test
	void testDeleteBrand_BrandNotFound() {
		when(brandRepository.findById(1)).thenReturn(Optional.empty());

		Exception exception = assertThrows(NotFoundException.class, () -> brandService.deleteBrand(1));

		assertTrue(exception.getMessage().contains(ErrorMessages.BRAND_NOT_FOUND));
		verify(brandRepository, times(1)).findById(1);
		verify(brandRepository, never()).delete(any(Brand.class));
	}
}
