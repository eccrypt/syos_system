package com.syos.application.command;

import com.syos.infrastructure.repository.DiscountRepository;
import com.syos.infrastructure.repository.ProductRepository;
import com.syos.domain.model.Product;
import com.syos.domain.model.Discount;

import java.util.List;
import java.time.LocalDate;

public class UnassignDiscountCommand implements Command {
	private final String productCode;
	private final int discountId;
	private final DiscountRepository discountRepository;
	private final ProductRepository productRepository;

	public UnassignDiscountCommand(String productCode, int discountId, DiscountRepository discountRepository,
			ProductRepository productRepository) {
		this.productCode = productCode;
		this.discountId = discountId;
		this.discountRepository = discountRepository;
		this.productRepository = productRepository;
	}

	@Override
	public void execute() {
		Product product = productRepository.findByCode(productCode);
		if (product == null) {
			return;
		}

		List<Discount> currentDiscounts = discountRepository.findDiscountsByProductCode(productCode, LocalDate.now());
		if (currentDiscounts.isEmpty()) {
			return;
		}

		if (!isDiscountAssignedToProduct(currentDiscounts, discountId)) {
			return;
		}

		Discount discountToUnassign = discountRepository.findById(discountId);
		if (discountToUnassign == null) {
			return;
		}

		discountRepository.unassignDiscountFromProduct(productCode, discountToUnassign.getId());
	}

	private boolean isDiscountAssignedToProduct(List<Discount> currentDiscounts, int discountId) {
		return currentDiscounts.stream().anyMatch(discount -> discount.getId() == discountId);
	}
}
