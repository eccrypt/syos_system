package com.syos.application.command;

import java.util.Scanner;

import com.syos.application.service.DiscountCreationService;
import com.syos.infrastructure.repository.DiscountRepository;

public class CreateDiscountCommand implements Command {
	private final DiscountCreationService discountCreationService;

	public CreateDiscountCommand(Scanner scanner, DiscountRepository discountRepository) {
		this.discountCreationService = new DiscountCreationService(scanner, discountRepository);
	}

	@Override
	public void execute() {
		discountCreationService.createDiscount();
	}
}
