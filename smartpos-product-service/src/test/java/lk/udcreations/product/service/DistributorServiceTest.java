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

import lk.udcreations.common.dto.distributor.DistributorDTO;
import lk.udcreations.common.dto.user.UsersDTO;
import lk.udcreations.product.constants.ErrorMessages;
import lk.udcreations.product.entity.Distributor;
import lk.udcreations.product.exception.NotFoundException;
import lk.udcreations.product.repository.DistributorRepository;
import lk.udcreations.product.security.AuthUtils;

class DistributorServiceTest {

	@Mock
	private DistributorRepository distributorRepository;

	@Mock
	private AuthUtils authUtils;

	@Mock
	private ModelMapper modelMapper;

	@InjectMocks
	private DistributorService distributorService;

	private Integer adminUser;
	private UsersDTO mockUser;
	private Distributor distributor1;
	private Distributor distributor2;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);

		distributor1 = new Distributor();
		distributor1.setDistributorId(1);
		distributor1.setCompanyName("Company A");
		distributor1.setEmail("companya@example.com");
		distributor1.setPhoneNo1("1234567890");

		distributor2 = new Distributor();
		distributor2.setDistributorId(2);
		distributor2.setCompanyName("Company B");
		distributor2.setEmail("companyb@example.com");
		distributor2.setPhoneNo1("0987654321");

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

			if (source instanceof Distributor && targetClass == DistributorDTO.class) {
				Distributor distributor = (Distributor) source;
				DistributorDTO dto = new DistributorDTO();
				dto.setDistributorId(distributor.getDistributorId());
				dto.setCompanyName(distributor.getCompanyName());
				dto.setEmail(distributor.getEmail());
				dto.setPhoneNo1(distributor.getPhoneNo1());
				dto.setPhoneNo2(distributor.getPhoneNo2());
				dto.setAddress(distributor.getAddress());
				dto.setEnabled(distributor.isEnabled());
				dto.setDeleted(distributor.isDeleted());
				return dto;
			}

			return null;
		});
	}

	@Test
	void testGetAllDistributors() {
		when(distributorRepository.findAll()).thenReturn(Arrays.asList(distributor1, distributor2));

		List<DistributorDTO> result = distributorService.getAllDistributors();

		assertEquals(2, result.size());
		assertEquals("Company A", result.get(0).getCompanyName());
		assertEquals("Company B", result.get(1).getCompanyName());
		verify(distributorRepository, times(1)).findAll();
	}

	@Test
	void testGetAllExistDistributors() {
		distributor1.setDeleted(false);
		distributor2.setDeleted(false);

		when(distributorRepository.findByDeletedFalse()).thenReturn(Arrays.asList(distributor1, distributor2));

		List<DistributorDTO> result = distributorService.getAllExistDistributors();

		assertEquals(2, result.size());
		assertEquals("Company A", result.get(0).getCompanyName());
		assertEquals("Company B", result.get(1).getCompanyName());
		verify(distributorRepository, times(1)).findByDeletedFalse();
	}

	@Test
	void testGetDistributorById_DistributorExists() {
		when(distributorRepository.findById(1)).thenReturn(Optional.of(distributor1));

		DistributorDTO result = distributorService.getDistributorById(1);

		assertNotNull(result);
		assertEquals("Company A", result.getCompanyName());
		verify(distributorRepository, times(1)).findById(1);
	}

	@Test
	void testGetDistributorById_DistributorNotFound() {
		when(distributorRepository.findById(1)).thenReturn(Optional.empty());

		Exception exception = assertThrows(NotFoundException.class, () -> distributorService.getDistributorById(1));

		assertTrue(exception.getMessage().contains(ErrorMessages.DISTRIBUTOR_NOT_FOUND));
		verify(distributorRepository, times(1)).findById(1);
	}

	@Test
	void testCreateDistributor_NewDistributor() {
		Distributor newDistributor = new Distributor();
		newDistributor.setCompanyName("Company A");
		newDistributor.setEmail("companya@example.com");
		newDistributor.setPhoneNo1("1234567890");

		Distributor savedDistributor = new Distributor();
		savedDistributor.setDistributorId(1);
		savedDistributor.setCompanyName("Company A");
		savedDistributor.setEmail("companya@example.com");
		savedDistributor.setPhoneNo1("1234567890");

		when(distributorRepository.findByCompanyNameAndDeletedFalse("Company A")).thenReturn(Optional.empty());
		when(distributorRepository.findByCompanyNameAndDeletedTrue("Company A")).thenReturn(Optional.empty());
		when(distributorRepository.findByEmail("companya@example.com")).thenReturn(Optional.empty());
		when(distributorRepository.save(any(Distributor.class))).thenReturn(savedDistributor);

		DistributorDTO result = distributorService.createDistributor(newDistributor);

		assertNotNull(result);
		assertEquals("Company A", result.getCompanyName());
		verify(distributorRepository, times(1)).save(any(Distributor.class));
	}

	@Test
	void testCreateDistributor_DistributorAlreadyExists() {
		when(distributorRepository.findByCompanyNameAndDeletedFalse("Company A"))
				.thenReturn(Optional.of(distributor1));

		Exception exception = assertThrows(IllegalArgumentException.class,
				() -> distributorService.createDistributor(distributor1));

		assertTrue(exception.getMessage().contains(ErrorMessages.DISTRIBUTOR_NAME_EXISTS));
		verify(distributorRepository, times(1)).findByCompanyNameAndDeletedFalse("Company A");
		verify(distributorRepository, never()).save(any(Distributor.class));
	}

	@Test
	void testUpdateDistributor_DistributorExists() {
		Distributor updatedDistributor = new Distributor();
		updatedDistributor.setCompanyName("Updated Company A");
		updatedDistributor.setEmail("updated@example.com");
		updatedDistributor.setPhoneNo1("1111111111");

		when(distributorRepository.findById(1)).thenReturn(Optional.of(distributor1));
		when(distributorRepository.save(any(Distributor.class))).thenReturn(distributor1);

		DistributorDTO result = distributorService.updateDistributor(1, updatedDistributor);

		assertNotNull(result);
		assertEquals("Updated Company A", result.getCompanyName());
		verify(distributorRepository, times(1)).findById(1);
		verify(distributorRepository, times(1)).save(distributor1);
	}

	@Test
	void testUpdateDistributor_DistributorNotFound() {
		Distributor updatedDistributor = new Distributor();
		updatedDistributor.setCompanyName("Updated Company A");
		updatedDistributor.setEmail("updated@example.com");
		updatedDistributor.setPhoneNo1("1111111111");

		when(distributorRepository.findById(1)).thenReturn(Optional.empty());

		Exception exception = assertThrows(RuntimeException.class, () -> distributorService.updateDistributor(1, updatedDistributor));

		assertTrue(exception.getMessage().contains(ErrorMessages.DISTRIBUTOR_NOT_FOUND));
		verify(distributorRepository, times(1)).findById(1);
	}

	@Test
	void testSoftDeleteDistributor_DistributorExists() {
		when(distributorRepository.findById(1)).thenReturn(Optional.of(distributor1));

		distributorService.softDeleteDistributor(1);

		assertTrue(distributor1.isDeleted());
		verify(distributorRepository, times(1)).findById(1);
		verify(distributorRepository, times(1)).save(distributor1);
	}

	@Test
	void testSoftDeleteDistributor_DistributorNotFound() {
		when(distributorRepository.findById(1)).thenReturn(Optional.empty());

		Exception exception = assertThrows(NotFoundException.class, () -> distributorService.softDeleteDistributor(1));

		assertTrue(exception.getMessage().contains(ErrorMessages.DISTRIBUTOR_NOT_FOUND));
		verify(distributorRepository, times(1)).findById(1);
	}

	@Test
	void testDeleteDistributor_DistributorExists() {
		when(distributorRepository.findById(1)).thenReturn(Optional.of(distributor1));

		distributorService.deleteDistributor(1);

		verify(distributorRepository, times(1)).findById(1);
		verify(distributorRepository, times(1)).delete(distributor1);
	}

	@Test
	void testDeleteDistributor_DistributorNotFound() {
		when(distributorRepository.findById(1)).thenReturn(Optional.empty());

		Exception exception = assertThrows(NotFoundException.class, () -> distributorService.deleteDistributor(1));

		assertTrue(exception.getMessage().contains(ErrorMessages.DISTRIBUTOR_NOT_FOUND));
		verify(distributorRepository, times(1)).findById(1);
		verify(distributorRepository, never()).delete(any(Distributor.class));
	}
}
