package com.syos.application.service;

import java.util.ArrayList;
import java.util.List;

import com.syos.application.factory.BillItemFactory;
import com.syos.domain.model.Bill;
import com.syos.domain.model.BillItem;
import com.syos.domain.model.Product;
import com.syos.infrastructure.repository.BillingRepository;
import com.syos.infrastructure.repository.BillingRepositoryImpl;
import com.syos.infrastructure.repository.ProductRepository;
import com.syos.infrastructure.repository.ProductRepositoryImpl;
import com.syos.infrastructure.repository.ShelfStockRepository;
import com.syos.infrastructure.repository.ShelfStockRepositoryImpl;
import com.syos.infrastructure.singleton.InventoryManager;
import com.syos.application.strategy.DiscountPricingStrategy;
import com.syos.application.strategy.ExpiryAwareFifoStrategy;
import com.syos.application.strategy.NoDiscountStrategy;

public class BillingService {
    private final ProductRepository productRepository = new ProductRepositoryImpl();
    private final ShelfStockRepository shelfStockRepository = new ShelfStockRepositoryImpl(productRepository);
    private final BillingRepository billingRepository = new BillingRepositoryImpl();
    private final BillItemFactory billItemFactory = new BillItemFactory(
            new DiscountPricingStrategy(new NoDiscountStrategy()));
    private final InventoryManager inventoryManager;

    public BillingService() {
        inventoryManager = InventoryManager.getInstance(new ExpiryAwareFifoStrategy());
    }

    public Bill createBill(BillRequest billRequest) {
        List<BillItem> billItems = new ArrayList<>();

        for (BillItemRequest itemReq : billRequest.getItems()) {
            Product product = validateProduct(itemReq.getProductCode());
            if (product == null) {
                throw new IllegalArgumentException("Product not found: " + itemReq.getProductCode());
            }

            int availableStock = inventoryManager.getAvailableStock(itemReq.getProductCode());
            if (availableStock < itemReq.getQuantity()) {
                throw new IllegalArgumentException("Insufficient stock for " + itemReq.getProductCode());
            }

            billItems.add(billItemFactory.create(product, itemReq.getQuantity()));
        }

        double totalDue = billItems.stream().mapToDouble(BillItem::getTotalPrice).sum();
        if (billRequest.getCashTendered() < totalDue) {
            throw new IllegalArgumentException("Cash tendered is less than total due");
        }

        int serialNumber = billingRepository.nextSerial();
        Bill bill = new Bill.BillBuilder(serialNumber, billItems).withCashTendered(billRequest.getCashTendered()).build();

        billingRepository.save(bill);
        deductStock(billItems);

        return bill;
    }

    private Product validateProduct(String productCode) {
        if (shelfStockRepository.findByCode(productCode) == null) {
            return null;
        }
        return productRepository.findByCode(productCode);
    }

    private void deductStock(List<BillItem> billItems) {
        for (BillItem item : billItems) {
            inventoryManager.deductFromShelf(item.getProduct().getCode(), item.getQuantity());
        }
    }

    public static class BillRequest {
        private List<BillItemRequest> items;
        private double cashTendered;

        public List<BillItemRequest> getItems() { return items; }
        public void setItems(List<BillItemRequest> items) { this.items = items; }
        public double getCashTendered() { return cashTendered; }
        public void setCashTendered(double cashTendered) { this.cashTendered = cashTendered; }
    }

    public static class BillItemRequest {
        private String productCode;
        private int quantity;

        public String getProductCode() { return productCode; }
        public void setProductCode(String productCode) { this.productCode = productCode; }
        public int getQuantity() { return quantity; }
        public void setQuantity(int quantity) { this.quantity = quantity; }
    }

    public List<Bill> getAllBills() {
        return billingRepository.findAll();
    }

    public Bill getBillBySerial(int serialNumber) {
        return billingRepository.findBySerial(serialNumber);
    }
}