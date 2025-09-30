package com.syos.application.command;

import java.util.Scanner;

import com.syos.application.service.DiscountAssignmentService;
import com.syos.infrastructure.repository.DiscountRepository;
import com.syos.infrastructure.repository.ProductRepository;

public class AssignDiscountCommand implements Command {
	private final DiscountAssignmentService discountAssignmentService;

	public AssignDiscountCommand(Scanner scanner, DiscountRepository discountRepository,
			ProductRepository productRepository) {
		this.discountAssignmentService = new DiscountAssignmentService(scanner, discountRepository, productRepository);
	}

	@Override
	public void execute() {
		discountAssignmentService.assignDiscountToProduct();
	}
}
