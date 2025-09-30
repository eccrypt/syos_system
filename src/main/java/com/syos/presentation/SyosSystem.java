package com.syos.presentation;

import java.util.Scanner;

import com.syos.infrastructure.repository.ProductRepository;
import com.syos.infrastructure.repository.ProductRepositoryImpl;
import com.syos.infrastructure.repository.ShelfStockRepository;
import com.syos.infrastructure.repository.ShelfStockRepositoryImpl;
import com.syos.infrastructure.repository.StockBatchRepository;
import com.syos.infrastructure.repository.StockBatchRepositoryImpl;
import com.syos.application.service.InventoryService;
import com.syos.application.service.OnlineStoreService;
import com.syos.application.service.ReportService;
import com.syos.application.service.StoreBillingService;
import com.syos.infrastructure.singleton.InventoryManager;
import com.syos.application.strategy.ExpiryAwareFifoStrategy;
import com.syos.application.strategy.ShelfStrategy;

public class SyosSystem {
	public static void main(String[] args) {

		ShelfStrategy strategy = new ExpiryAwareFifoStrategy();
		InventoryManager.getInstance(strategy);
		Scanner scanner = new Scanner(System.in);
		ProductRepository productRepository = new ProductRepositoryImpl();
		ShelfStockRepository shelfStockRepository = new ShelfStockRepositoryImpl(productRepository);
		StockBatchRepository stockBatchRepository = new StockBatchRepositoryImpl();
		StoreBillingService billingService = new StoreBillingService();
		InventoryService inventoryService = new InventoryService();
		OnlineStoreService onlineStoreService = new OnlineStoreService();

		ReportService reportService = new ReportService(scanner, productRepository, shelfStockRepository,
				stockBatchRepository);

		while (true) {
			System.out.println("\n=== SYOS Main Menu ===");
			System.out.println(" 1) Store Billing");
			System.out.println(" 2) Online Store");
			System.out.println(" 3) Inventory");
			System.out.println(" 4) Reports");
			System.out.println(" 5) Exit");

			System.out.print("\n Select an option : ");
			String choice = scanner.nextLine().trim();

			switch (choice) {
			case "1":
				billingService.run();
				break;
			case "2":
				onlineStoreService.run();
				break;
			case "3":
				inventoryService.run();
				break;
			case "4":
				reportService.run();
				break;
			case "5":
				System.out.println("Goodbye!");
				scanner.close();
				return;
			default:
				System.out.println("Invalid selection.");
				break;
			}
		}
	}
}
