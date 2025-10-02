package com.syos.application.service;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import com.syos.application.command.AddProductCommand;
import com.syos.application.command.AssignDiscountCommand;
import com.syos.application.command.Command;
import com.syos.application.command.CreateDiscountCommand;
import com.syos.application.command.MoveToShelfCommand;
import com.syos.application.command.ReceiveStockCommand;
import com.syos.application.command.RemoveCloseToExpiryStockCommand;
import com.syos.application.command.ViewStockCommand;
import com.syos.application.command.ViewExpiryStockCommand;
import com.syos.application.command.UnassignDiscountCommand;
import com.syos.application.command.UpdateProductCommand;
import com.syos.application.command.ViewAllInventoryStocksCommand;
import com.syos.application.command.ViewExpiringBatchesCommand;
import com.syos.application.command.DiscardExpiringBatchesCommand;
import com.syos.application.command.ViewAllProductsCommand;
import com.syos.application.command.ViewAllProductsWithDiscountsCommand;
import com.syos.application.command.ViewAllDiscountsCommand;

import com.syos.infrastructure.repository.DiscountRepository;
import com.syos.infrastructure.repository.DiscountRepositoryImpl;
import com.syos.infrastructure.repository.ProductRepository;
import com.syos.infrastructure.repository.ProductRepositoryImpl;
import com.syos.infrastructure.singleton.InventoryManager;

import com.syos.application.strategy.ExpiryAwareFifoStrategy;
import com.syos.infrastructure.util.CommonVariables;

public class InventoryService {
	private final InventoryManager inventoryManager;
	private final Scanner scanner = new Scanner(System.in);
	private final Map<String, Command> commandMap = new HashMap<>();
	private final DiscountRepository discountRepository;
	private final ProductRepository productRepository;

	public InventoryService() {
		this.productRepository = new ProductRepositoryImpl();
		this.discountRepository = new DiscountRepositoryImpl();

		this.inventoryManager = InventoryManager.getInstance(new ExpiryAwareFifoStrategy());

		inventoryManager.addObserver(new StockAlertService(CommonVariables.STOCK_ALERT_THRESHOLD));

		ProductService productService = new ProductServiceImpl(productRepository);

		commandMap.put("1", new AddProductCommand(productService, scanner, productRepository));
		commandMap.put("2", new ViewAllProductsCommand(productRepository, scanner));
		commandMap.put("3", new UpdateProductCommand(productService, scanner));
		commandMap.put("4", new ReceiveStockCommand(inventoryManager, scanner));
		commandMap.put("5", new MoveToShelfCommand(inventoryManager, scanner));
		commandMap.put("6", new ViewStockCommand(inventoryManager, scanner));
		commandMap.put("7", new ViewAllInventoryStocksCommand(inventoryManager, scanner));
		commandMap.put("8", new ViewExpiryStockCommand(inventoryManager, scanner));
		commandMap.put("9", new RemoveCloseToExpiryStockCommand(inventoryManager, scanner));
		commandMap.put("10", new ViewExpiringBatchesCommand(inventoryManager, scanner));
		commandMap.put("11", new DiscardExpiringBatchesCommand(inventoryManager, scanner));
		commandMap.put("12", new CreateDiscountCommand(scanner, discountRepository));
		commandMap.put("13", new AssignDiscountCommand(scanner, discountRepository, productRepository));
		commandMap.put("14", new ViewAllDiscountsCommand(discountRepository, scanner));
		commandMap.put("15", new ViewAllProductsWithDiscountsCommand(discountRepository, productRepository));
	}

	public void run() {
		while (true) {
			System.out.println("\n=== Inventory Menu ===");
			System.out.println("1) Add new product");
			System.out.println("2) View all registered products");
			System.out.println("3) Update product details");
			System.out.println();
			System.out.println("4) Receive stocks to inventory");
			System.out.println("5) Move stocks from inventory to shelf");
			System.out.println();
			System.out.println("6) View all shelf stocks");
			System.out.println("7) View all inventory stocks");
			System.out.println("8) View close to expiry shelf stocks");
			System.out.println("9) Discard close to expiry stock from shelf");
			System.out.println("10) View all expiring inventory batches");
			System.out.println("11) Discard quantity from inventory batch");
			System.out.println();
			System.out.println("12) Create a new discount");
			System.out.println("13) Assign discount to products");
			System.out.println("14) View all discounts");
			System.out.println("15) View all products with discounts");
			System.out.println("16) Unassign discount from product");
			System.out.println();
			System.out.println("17) Exit");
			System.out.print("Choose an option: ");

			String choice = scanner.nextLine().trim();
			if ("17".equals(choice)) {
				System.out.println("Exiting Inventory Menu.");
				break;
			}

			if ("16".equals(choice)) {
				System.out.print("Enter Product Code: ");
				String productCode = scanner.nextLine().trim();
				System.out.print("Enter Discount ID to unassign: ");
				try {
					int discountId = Integer.parseInt(scanner.nextLine().trim());
					Command command = new UnassignDiscountCommand(productCode, discountId, discountRepository, productRepository);
					command.execute();
				} catch (NumberFormatException e) {
					System.out.println("Invalid Discount ID. Please enter a number.");
				}
			} else {
				Command command = commandMap.get(choice);
				if (command != null) {
					command.execute();
				} else {
					System.out.println("Invalid option. Please choose from the available numbers.");
				}
			}
		}
	}
}
