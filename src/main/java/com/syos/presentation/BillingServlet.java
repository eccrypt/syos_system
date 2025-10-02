package com.syos.presentation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.databind.ObjectMapper;
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

public class BillingServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ProductRepository productRepository = new ProductRepositoryImpl();
    private final ShelfStockRepository shelfStockRepository = new ShelfStockRepositoryImpl(productRepository);
    private final BillingRepository billingRepository = new BillingRepositoryImpl();
    private final BillItemFactory billItemFactory = new BillItemFactory(
            new DiscountPricingStrategy(new NoDiscountStrategy()));
    private final InventoryManager inventoryManager;

    public BillingServlet() {
        inventoryManager = InventoryManager.getInstance(new ExpiryAwareFifoStrategy());
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getPathInfo();
        if (path == null) path = "/";

        resp.setContentType("application/json");

        switch (path) {
            case "/create":
                createBill(req, resp);
                break;
            default:
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                resp.getWriter().write("{\"error\":\"Unknown endpoint\"}");
                break;
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getPathInfo();
        if (path == null) path = "/";

        resp.setContentType("application/json");

        try {
            switch (path) {
                case "/test":
                    testConnection(resp);
                    break;
                case "/bills":
                    getAllBills(resp);
                    break;
                default:
                    if (path.startsWith("/bills/")) {
                        String serialStr = path.substring("/bills/".length());
                        try {
                            int serialNumber = Integer.parseInt(serialStr);
                            getBillBySerial(serialNumber, resp);
                        } catch (NumberFormatException e) {
                            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                            resp.getWriter().write("{\"error\":\"Invalid serial number format\"}");
                        }
                    } else {
                        resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                        resp.getWriter().write("{\"error\":\"Unknown endpoint\"}");
                    }
                    break;
            }
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    private void createBill(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            BillRequest billRequest = objectMapper.readValue(req.getInputStream(), BillRequest.class);
            List<BillItem> billItems = new ArrayList<>();

            for (BillItemRequest itemReq : billRequest.getItems()) {
                Product product = validateProduct(itemReq.getProductCode());
                if (product == null) {
                    resp.setContentType("application/json");
                    resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    resp.getWriter().write("{\"error\":\"Product not found: " + itemReq.getProductCode() + "\"}");
                    return;
                }

                int availableStock = inventoryManager.getAvailableStock(itemReq.getProductCode());
                if (availableStock < itemReq.getQuantity()) {
                    resp.setContentType("application/json");
                    resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    resp.getWriter().write("{\"error\":\"Insufficient stock for " + itemReq.getProductCode() + "\"}");
                    return;
                }

                billItems.add(billItemFactory.create(product, itemReq.getQuantity()));
            }

            double totalDue = billItems.stream().mapToDouble(BillItem::getTotalPrice).sum();
            if (billRequest.getCashTendered() < totalDue) {
                resp.setContentType("application/json");
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write("{\"error\":\"Cash tendered is less than total due\"}");
                return;
            }

            int serialNumber = billingRepository.nextSerial();
            Bill bill = new Bill.BillBuilder(serialNumber, billItems).withCashTendered(billRequest.getCashTendered()).build();

            billingRepository.save(bill);
            deductStock(billItems);

            BillResponse billResponse = new BillResponse(serialNumber, totalDue, billRequest.getCashTendered(), bill.getChangeReturned());
            resp.setContentType("application/json");
            objectMapper.writeValue(resp.getWriter(), billResponse);

        } catch (Exception e) {
            resp.setContentType("application/json");
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"error\":\"" + e.getMessage() + "\"}");
        }
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

    private void testConnection(HttpServletResponse resp) throws IOException {
        resp.getWriter().write("{\"status\":\"Billing API is working!\",\"timestamp\":\"" + java.time.LocalDateTime.now() + "\"}");
    }

    private void getAllBills(HttpServletResponse resp) throws IOException {
        try {
            resp.getWriter().write("{\"bills\":\"Bill listing not implemented yet\"}");
        } catch (Exception e) {
            resp.getWriter().write("{\"error\":\"Database connection failed: " + e.getMessage() + "\"}");
        }
    }

    private void getBillBySerial(int serialNumber, HttpServletResponse resp) throws IOException {
        try {
            resp.getWriter().write("{\"bill\":\"Bill with serial " + serialNumber + " not found\"}");
        } catch (Exception e) {
            resp.getWriter().write("{\"error\":\"Database connection failed: " + e.getMessage() + "\"}");
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

    public static class BillResponse {
        private int serialNumber;
        private double totalAmount;
        private double cashTendered;
        private double changeReturned;

        public BillResponse(int serialNumber, double totalAmount, double cashTendered, double changeReturned) {
            this.serialNumber = serialNumber;
            this.totalAmount = totalAmount;
            this.cashTendered = cashTendered;
            this.changeReturned = changeReturned;
        }

        public int getSerialNumber() { return serialNumber; }
        public double getTotalAmount() { return totalAmount; }
        public double getCashTendered() { return cashTendered; }
        public double getChangeReturned() { return changeReturned; }
    }
}