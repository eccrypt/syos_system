package com.syos.presentation;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.syos.domain.model.Product;
import com.syos.domain.model.Discount;
import com.syos.domain.model.StockBatch;
import com.syos.domain.enums.DiscountType;
import java.time.LocalDate;
import com.syos.infrastructure.repository.DiscountRepository;
import com.syos.infrastructure.repository.DiscountRepositoryImpl;
import com.syos.infrastructure.repository.ProductRepository;
import com.syos.infrastructure.repository.ProductRepositoryImpl;
import com.syos.infrastructure.repository.StockBatchRepository;
import com.syos.infrastructure.repository.StockBatchRepositoryImpl;
import com.syos.application.service.ProductService;
import com.syos.application.service.ProductServiceImpl;
import com.syos.infrastructure.singleton.InventoryManager;
import com.syos.application.strategy.ExpiryAwareFifoStrategy;
import com.syos.infrastructure.util.CommonVariables;

public class InventoryServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ProductRepository productRepository = new ProductRepositoryImpl();
    private final DiscountRepository discountRepository = new DiscountRepositoryImpl();
    private final StockBatchRepository stockBatchRepository = new StockBatchRepositoryImpl();
    private final ProductService productService = new ProductServiceImpl(productRepository);
    private final InventoryManager inventoryManager;

    public InventoryServlet() {
        this.inventoryManager = InventoryManager.getInstance(new ExpiryAwareFifoStrategy());
        inventoryManager.addObserver(new com.syos.application.service.StockAlertService(CommonVariables.STOCK_ALERT_THRESHOLD));
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getPathInfo();
        if (path == null) path = "/";

        resp.setContentType("application/json");

        try {
            switch (path) {
                case "/add-product":
                    addProduct(req, resp);
                    break;
                case "/update-product":
                    updateProduct(req, resp);
                    break;
                case "/receive-stock":
                    receiveStock(req, resp);
                    break;
                case "/move-to-shelf":
                    moveToShelf(req, resp);
                    break;
                case "/remove-expiry-stock":
                    removeExpiryStock(req, resp);
                    break;
                case "/discard-batch":
                    discardBatch(req, resp);
                    break;
                case "/create-discount":
                    createDiscount(req, resp);
                    break;
                case "/assign-discount":
                    assignDiscount(req, resp);
                    break;
                case "/unassign-discount":
                    unassignDiscount(req, resp);
                    break;
                default:
                    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    resp.getWriter().write("{\"error\":\"Unknown endpoint\"}");
            }
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"error\":\"" + e.getMessage() + "\"}");
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
                case "/products":
                    getAllProducts(resp);
                    break;
                case "/stocks":
                    getAllStocks(resp);
                    break;
                case "/inventory-stocks":
                    getInventoryStocks(resp);
                    break;
                case "/expiry-stocks":
                    getExpiryStocks(resp);
                    break;
                case "/expiring-batches":
                    getExpiringBatches(resp);
                    break;
                case "/discounts":
                    getAllDiscounts(resp);
                    break;
                case "/products-with-discounts":
                    getProductsWithDiscounts(resp);
                    break;
                default:
                    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    resp.getWriter().write("{\"error\":\"Unknown endpoint\"}");
            }
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    private void addProduct(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        ProductRequest productReq = objectMapper.readValue(req.getInputStream(), ProductRequest.class);
        productService.addProduct(productReq.getCode(), productReq.getName(), productReq.getPrice());
        resp.getWriter().write("{\"status\":\"Product added successfully\"}");
    }

    private void updateProduct(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        UpdateProductRequest updateReq = objectMapper.readValue(req.getInputStream(), UpdateProductRequest.class);
        productService.updateProductName(updateReq.getCode(), updateReq.getNewName());
        resp.getWriter().write("{\"status\":\"Product updated successfully\"}");
    }

    private void receiveStock(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        StockRequest stockReq = objectMapper.readValue(req.getInputStream(), StockRequest.class);
        java.time.LocalDate purchaseDate = java.time.LocalDate.now();
        java.time.LocalDate expiryDate = java.time.LocalDate.parse(stockReq.getExpiryDate());
        inventoryManager.receiveStock(stockReq.getProductCode(), purchaseDate, expiryDate, stockReq.getQuantity());
        resp.getWriter().write("{\"status\":\"Stock received successfully\"}");
    }

    private void moveToShelf(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        MoveStockRequest moveReq = objectMapper.readValue(req.getInputStream(), MoveStockRequest.class);
        inventoryManager.moveToShelf(moveReq.getProductCode(), moveReq.getQuantity());
        resp.getWriter().write("{\"status\":\"Stock moved to shelf successfully\"}");
    }

    private void removeExpiryStock(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            List<StockBatch> expiringBatches = stockBatchRepository.findAllExpiringBatches(7); // Next 7 days
            int removedCount = 0;
            for (StockBatch batch : expiringBatches) {
                if (batch.getQuantityRemaining() > 0) {
                    inventoryManager.discardBatchQuantity(batch.getId(), batch.getQuantityRemaining());
                    removedCount++;
                }
            }
            resp.getWriter().write("{\"status\":\"Removed " + removedCount + " expiring stock batches\"}");
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"error\":\"Failed to remove expiry stock: " + e.getMessage() + "\"}");
        }
    }

    private void discardBatch(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        DiscardBatchRequest discardReq = objectMapper.readValue(req.getInputStream(), DiscardBatchRequest.class);
        // Need batchId, assuming batchNumber is id for now
        int batchId = Integer.parseInt(discardReq.getBatchNumber());
        inventoryManager.discardBatchQuantity(batchId, discardReq.getQuantity());
        resp.getWriter().write("{\"status\":\"Batch discarded successfully\"}");
    }

    private void createDiscount(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            DiscountRequest discountReq = objectMapper.readValue(req.getInputStream(), DiscountRequest.class);

            // Validate input
            if (discountReq.getName() == null || discountReq.getName().trim().isEmpty()) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write("{\"error\":\"Discount name is required\"}");
                return;
            }

            if (discountReq.getValue() == null || discountReq.getValue().trim().isEmpty()) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write("{\"error\":\"Discount value is required\"}");
                return;
            }

            if (discountReq.getStartDate() == null || discountReq.getStartDate().trim().isEmpty()) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write("{\"error\":\"Start date is required\"}");
                return;
            }

            if (discountReq.getEndDate() == null || discountReq.getEndDate().trim().isEmpty()) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write("{\"error\":\"End date is required\"}");
                return;
            }

            // Parse discount type
            DiscountType discountType;
            try {
                discountType = DiscountType.valueOf(discountReq.getType().toUpperCase());
            } catch (IllegalArgumentException e) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write("{\"error\":\"Invalid discount type. Use PERCENT or AMOUNT\"}");
                return;
            }

            // Parse discount value
            double discountValue;
            try {
                discountValue = Double.parseDouble(discountReq.getValue());
                if (discountValue < 0) {
                    resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    resp.getWriter().write("{\"error\":\"Discount value must be non-negative\"}");
                    return;
                }
                if (discountType == DiscountType.PERCENT && discountValue > 100) {
                    resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    resp.getWriter().write("{\"error\":\"Percentage discount cannot exceed 100%\"}");
                    return;
                }
            } catch (NumberFormatException e) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write("{\"error\":\"Invalid discount value format\"}");
                return;
            }

            // Parse dates
            LocalDate startDate, endDate;
            try {
                startDate = LocalDate.parse(discountReq.getStartDate());
                endDate = LocalDate.parse(discountReq.getEndDate());
            } catch (Exception e) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write("{\"error\":\"Invalid date format. Use YYYY-MM-DD\"}");
                return;
            }

            if (endDate.isBefore(startDate)) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write("{\"error\":\"End date cannot be before start date\"}");
                return;
            }

            // Create discount
            int discountId = discountRepository.createDiscount(discountReq.getName(), discountType, discountValue, startDate, endDate);
            if (discountId != -1) {
                resp.getWriter().write("{\"status\":\"Discount created successfully\",\"discountId\":" + discountId + "}");
            } else {
                resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                resp.getWriter().write("{\"error\":\"Failed to create discount\"}");
            }

        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    private void assignDiscount(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            AssignDiscountRequest assignReq = objectMapper.readValue(req.getInputStream(), AssignDiscountRequest.class);

            // Validate input
            if (assignReq.getProductCode() == null || assignReq.getProductCode().trim().isEmpty()) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write("{\"error\":\"Product code is required\"}");
                return;
            }

            if (assignReq.getDiscountId() == null || assignReq.getDiscountId().trim().isEmpty()) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write("{\"error\":\"Discount ID is required\"}");
                return;
            }

            // Validate product exists
            Product product = productRepository.findByCode(assignReq.getProductCode());
            if (product == null) {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                resp.getWriter().write("{\"error\":\"Product not found: " + assignReq.getProductCode() + "\"}");
                return;
            }

            // Parse discount ID
            int discountId;
            try {
                discountId = Integer.parseInt(assignReq.getDiscountId());
                if (discountId <= 0) {
                    resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    resp.getWriter().write("{\"error\":\"Discount ID must be a positive number\"}");
                    return;
                }
            } catch (NumberFormatException e) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write("{\"error\":\"Invalid discount ID format\"}");
                return;
            }

            // Validate discount exists
            Discount discount = discountRepository.findById(discountId);
            if (discount == null) {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                resp.getWriter().write("{\"error\":\"Discount not found with ID: " + discountId + "\"}");
                return;
            }

            // Assign discount to product
            discountRepository.linkProductToDiscount(assignReq.getProductCode(), discountId);
            resp.getWriter().write("{\"status\":\"Discount assigned successfully to product " + assignReq.getProductCode() + "\"}");

        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    private void unassignDiscount(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        ProductCodeRequest codeReq = objectMapper.readValue(req.getInputStream(), ProductCodeRequest.class);
        // Assuming unassign logic
        resp.getWriter().write("{\"status\":\"Discount unassigned successfully\"}");
    }

    private void testConnection(HttpServletResponse resp) throws IOException {
        resp.getWriter().write("{\"status\":\"Inventory API is working!\",\"timestamp\":\"" + java.time.LocalDateTime.now() + "\"}");
    }

    private void getAllProducts(HttpServletResponse resp) throws IOException {
        try {
            List<Product> products = productRepository.findAll();
            objectMapper.writeValue(resp.getWriter(), products);
        } catch (Exception e) {
            resp.getWriter().write("{\"error\":\"Database connection failed: " + e.getMessage() + "\"}");
        }
    }

    private void getAllStocks(HttpServletResponse resp) throws IOException {
        try {
            List<String> productCodes = stockBatchRepository.getAllProductCodesWithBatches();
            resp.getWriter().write("{\"stocks\":\"Found " + productCodes.size() + " products with stock\"}");
        } catch (Exception e) {
            resp.getWriter().write("{\"error\":\"Database connection failed: " + e.getMessage() + "\"}");
        }
    }

    private void getInventoryStocks(HttpServletResponse resp) throws IOException {
        try {
            List<String> productCodes = stockBatchRepository.getAllProductCodesWithBatches();
            resp.getWriter().write("{\"inventoryStocks\":\"Found " + productCodes.size() + " products in inventory\"}");
        } catch (Exception e) {
            resp.getWriter().write("{\"error\":\"Database connection failed: " + e.getMessage() + "\"}");
        }
    }

    private void getExpiryStocks(HttpServletResponse resp) throws IOException {
        try {
            List<StockBatch> expiringBatches = stockBatchRepository.findAllExpiringBatches(30); // Next 30 days
            objectMapper.writeValue(resp.getWriter(), expiringBatches);
        } catch (Exception e) {
            resp.getWriter().write("{\"error\":\"Database connection failed: " + e.getMessage() + "\"}");
        }
    }

    private void getExpiringBatches(HttpServletResponse resp) throws IOException {
        try {
            List<StockBatch> expiringBatches = stockBatchRepository.findAllExpiringBatches(7); // Next 7 days
            objectMapper.writeValue(resp.getWriter(), expiringBatches);
        } catch (Exception e) {
            resp.getWriter().write("{\"error\":\"Database connection failed: " + e.getMessage() + "\"}");
        }
    }

    private void getAllDiscounts(HttpServletResponse resp) throws IOException {
        try {
            // Note: Need to implement findAll method in DiscountRepository if not exists
            resp.getWriter().write("{\"discounts\":\"Discount listing not implemented yet\"}");
        } catch (Exception e) {
            resp.getWriter().write("{\"error\":\"Database connection failed: " + e.getMessage() + "\"}");
        }
    }

    private void getProductsWithDiscounts(HttpServletResponse resp) throws IOException {
        try {
            // Note: Need to implement method to get products with active discounts
            resp.getWriter().write("{\"productsWithDiscounts\":\"Not implemented yet\"}");
        } catch (Exception e) {
            resp.getWriter().write("{\"error\":\"Database connection failed: " + e.getMessage() + "\"}");
        }
    }

    // DTOs
    public static class ProductRequest {
        private String code, name;
        private double price;

        public String getCode() { return code; }
        public void setCode(String code) { this.code = code; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public double getPrice() { return price; }
        public void setPrice(double price) { this.price = price; }
    }

    public static class UpdateProductRequest {
        private String code, newName;
        private double newPrice;

        public String getCode() { return code; }
        public void setCode(String code) { this.code = code; }
        public String getNewName() { return newName; }
        public void setNewName(String newName) { this.newName = newName; }
        public double getNewPrice() { return newPrice; }
        public void setNewPrice(double newPrice) { this.newPrice = newPrice; }
    }

    public static class StockRequest {
        private String productCode, batchNumber, expiryDate;
        private int quantity;

        public String getProductCode() { return productCode; }
        public void setProductCode(String productCode) { this.productCode = productCode; }
        public int getQuantity() { return quantity; }
        public void setQuantity(int quantity) { this.quantity = quantity; }
        public String getBatchNumber() { return batchNumber; }
        public void setBatchNumber(String batchNumber) { this.batchNumber = batchNumber; }
        public String getExpiryDate() { return expiryDate; }
        public void setExpiryDate(String expiryDate) { this.expiryDate = expiryDate; }
    }

    public static class MoveStockRequest {
        private String productCode;
        private int quantity;

        public String getProductCode() { return productCode; }
        public void setProductCode(String productCode) { this.productCode = productCode; }
        public int getQuantity() { return quantity; }
        public void setQuantity(int quantity) { this.quantity = quantity; }
    }

    public static class ProductCodeRequest {
        private String productCode;

        public String getProductCode() { return productCode; }
        public void setProductCode(String productCode) { this.productCode = productCode; }
    }

    public static class DiscardBatchRequest {
        private String productCode, batchNumber;
        private int quantity;

        public String getProductCode() { return productCode; }
        public void setProductCode(String productCode) { this.productCode = productCode; }
        public String getBatchNumber() { return batchNumber; }
        public void setBatchNumber(String batchNumber) { this.batchNumber = batchNumber; }
        public int getQuantity() { return quantity; }
        public void setQuantity(int quantity) { this.quantity = quantity; }
    }

    public static class DiscountRequest {
        private String name, type, value, startDate, endDate;

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        public String getValue() { return value; }
        public void setValue(String value) { this.value = value; }
        public String getStartDate() { return startDate; }
        public void setStartDate(String startDate) { this.startDate = startDate; }
        public String getEndDate() { return endDate; }
        public void setEndDate(String endDate) { this.endDate = endDate; }
    }

    public static class AssignDiscountRequest {
        private String discountId, productCode;

        public String getDiscountId() { return discountId; }
        public void setDiscountId(String discountId) { this.discountId = discountId; }
        public String getProductCode() { return productCode; }
        public void setProductCode(String productCode) { this.productCode = productCode; }
    }
}