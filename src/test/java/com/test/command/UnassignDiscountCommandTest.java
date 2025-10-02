package com.test.command;

import com.syos.application.command.UnassignDiscountCommand;
import com.syos.domain.model.Discount;
import com.syos.domain.model.Product;
import com.syos.infrastructure.repository.DiscountRepository;
import com.syos.infrastructure.repository.ProductRepository;
import com.syos.domain.enums.DiscountType;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UnassignDiscountCommandTest {

	@Mock
	private DiscountRepository discountRepository;
	@Mock
	private ProductRepository productRepository;

	@Test
	@DisplayName("Should successfully unassign an active discount from a product")
	void shouldSuccessfullyUnassignDiscount() {
		String productCode = "PROD001";
		Product product = new Product(productCode, "Laptop", 1200.0);
		int discountIdToUnassign = 1;
		Discount discountToUnassign = new Discount(discountIdToUnassign, "Summer Sale", DiscountType.PERCENT, 15.0,
				LocalDate.now().minusDays(5), LocalDate.now().plusDays(5));
		Discount otherActiveDiscount = new Discount(2, "Student Discount", DiscountType.AMOUNT, 50.0,
				LocalDate.now().minusDays(10), LocalDate.now().plusDays(10));
		List<Discount> activeDiscounts = Arrays.asList(discountToUnassign, otherActiveDiscount);

		UnassignDiscountCommand unassignDiscountCommand = new UnassignDiscountCommand(productCode, discountIdToUnassign, discountRepository, productRepository);

		when(productRepository.findByCode(productCode)).thenReturn(product);
		when(discountRepository.findDiscountsByProductCode(productCode, LocalDate.now())).thenReturn(activeDiscounts);
		when(discountRepository.findById(discountIdToUnassign)).thenReturn(discountToUnassign);
		when(discountRepository.unassignDiscountFromProduct(productCode, discountIdToUnassign)).thenReturn(true);

		unassignDiscountCommand.execute();

		verify(productRepository, times(1)).findByCode(productCode);
		verify(discountRepository, times(1)).findDiscountsByProductCode(productCode, LocalDate.now());
		verify(discountRepository, times(1)).findById(discountIdToUnassign);
		verify(discountRepository, times(1)).unassignDiscountFromProduct(productCode, discountIdToUnassign);
	}

	@Test
	@DisplayName("Should not unassign if product is not found")
	void shouldNotUnassignIfProductNotFound() {
		String productCode = "NONEXISTENT_PROD";
		int discountId = 1;
		UnassignDiscountCommand unassignDiscountCommand = new UnassignDiscountCommand(productCode, discountId, discountRepository, productRepository);

		when(productRepository.findByCode(productCode)).thenReturn(null);

		unassignDiscountCommand.execute();

		verify(productRepository, times(1)).findByCode(productCode);
		verify(discountRepository, never()).findDiscountsByProductCode(anyString(), any(LocalDate.class));
		verify(discountRepository, never()).findById(anyInt());
		verify(discountRepository, never()).unassignDiscountFromProduct(anyString(), anyInt());
	}


	@Test
	@DisplayName("Should not unassign if product has no active discounts")
	void shouldNotUnassignIfNoActiveDiscounts() {
		String productCode = "PROD003";
		Product product = new Product(productCode, "Mouse", 25.0);
		int discountId = 1;
		UnassignDiscountCommand unassignDiscountCommand = new UnassignDiscountCommand(productCode, discountId, discountRepository, productRepository);

		when(productRepository.findByCode(productCode)).thenReturn(product);
		when(discountRepository.findDiscountsByProductCode(productCode, LocalDate.now()))
				.thenReturn(Collections.emptyList());

		unassignDiscountCommand.execute();

		verify(productRepository, times(1)).findByCode(productCode);
		verify(discountRepository, times(1)).findDiscountsByProductCode(productCode, LocalDate.now());
		verify(discountRepository, never()).findById(anyInt());
		verify(discountRepository, never()).unassignDiscountFromProduct(anyString(), anyInt());
	}



	@Test
	@DisplayName("Should not unassign if discount with given ID is not found")
	void shouldNotUnassignIfDiscountNotFound() {
		String productCode = "PROD006";
		Product product = new Product(productCode, "Headphones", 100.0);
		int nonExistentDiscountId = 999;
		List<Discount> activeDiscounts = Collections.singletonList(new Discount(1, "Existing Discount",
				DiscountType.PERCENT, 10.0, LocalDate.now().minusDays(1), LocalDate.now().plusDays(1)));

		UnassignDiscountCommand unassignDiscountCommand = new UnassignDiscountCommand(productCode, nonExistentDiscountId, discountRepository, productRepository);

		when(productRepository.findByCode(productCode)).thenReturn(product);
		when(discountRepository.findDiscountsByProductCode(productCode, LocalDate.now())).thenReturn(activeDiscounts);
		when(discountRepository.findById(nonExistentDiscountId)).thenReturn(null);

		unassignDiscountCommand.execute();

		verify(productRepository, times(1)).findByCode(productCode);
		verify(discountRepository, times(1)).findDiscountsByProductCode(productCode, LocalDate.now());
		verify(discountRepository, times(1)).findById(nonExistentDiscountId);
		verify(discountRepository, never()).unassignDiscountFromProduct(anyString(), anyInt());
	}

	@Test
	@DisplayName("Should not unassign if discount ID is not assigned to the product")
	void shouldNotUnassignIfDiscountNotAssignedToProduct() {
		String productCode = "PROD007";
		Product product = new Product(productCode, "Charger", 20.0);
		int discountIdNotAssigned = 3;
		Discount foundDiscount = new Discount(discountIdNotAssigned, "Another Store Discount", DiscountType.AMOUNT, 2.0,
				LocalDate.now().minusDays(5), LocalDate.now().plusDays(5));

		List<Discount> activeDiscounts = Collections.singletonList(new Discount(1, "Existing Discount",
				DiscountType.PERCENT, 10.0, LocalDate.now().minusDays(1), LocalDate.now().plusDays(1)));

		UnassignDiscountCommand unassignDiscountCommand = new UnassignDiscountCommand(productCode, discountIdNotAssigned, discountRepository, productRepository);

		when(productRepository.findByCode(productCode)).thenReturn(product);
		when(discountRepository.findDiscountsByProductCode(productCode, LocalDate.now())).thenReturn(activeDiscounts);
		when(discountRepository.findById(discountIdNotAssigned)).thenReturn(foundDiscount);

		unassignDiscountCommand.execute();

		verify(productRepository, times(1)).findByCode(productCode);
		verify(discountRepository, times(1)).findDiscountsByProductCode(productCode, LocalDate.now());
		verify(discountRepository, times(1)).findById(discountIdNotAssigned);
		verify(discountRepository, never()).unassignDiscountFromProduct(anyString(), anyInt());
	}

	@Test
	@DisplayName("Should call unassign even if repository returns false")
	void shouldCallUnassignEvenIfRepositoryFails() {
		String productCode = "PROD008";
		Product product = new Product(productCode, "USB Drive", 15.0);
		int discountId = 1;
		Discount discountToUnassign = new Discount(discountId, "Winter Sale", DiscountType.AMOUNT, 3.0,
				LocalDate.now().minusDays(5), LocalDate.now().plusDays(5));
		List<Discount> activeDiscounts = Collections.singletonList(discountToUnassign);

		UnassignDiscountCommand unassignDiscountCommand = new UnassignDiscountCommand(productCode, discountId, discountRepository, productRepository);

		when(productRepository.findByCode(productCode)).thenReturn(product);
		when(discountRepository.findDiscountsByProductCode(productCode, LocalDate.now())).thenReturn(activeDiscounts);
		when(discountRepository.findById(discountId)).thenReturn(discountToUnassign);
		when(discountRepository.unassignDiscountFromProduct(productCode, discountId)).thenReturn(false);

		unassignDiscountCommand.execute();

		verify(productRepository, times(1)).findByCode(productCode);
		verify(discountRepository, times(1)).findDiscountsByProductCode(productCode, LocalDate.now());
		verify(discountRepository, times(1)).findById(discountId);
		verify(discountRepository, times(1)).unassignDiscountFromProduct(productCode, discountId);
	}
}
