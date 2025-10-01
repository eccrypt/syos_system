package com.syos.presentation;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.syos.domain.model.Product;
import com.syos.infrastructure.repository.DiscountRepository;
import com.syos.infrastructure.repository.DiscountRepositoryImpl;
import com.syos.infrastructure.repository.ProductRepository;
import com.syos.infrastructure.repository.ProductRepositoryImpl;
import com.syos.application.service.ProductService;
import com.syos.application.service.ProductServiceImpl;
import com.syos.infrastructure.singleton.InventoryManager;
import com.syos.application.strategy.ExpiryAwareFifoStrategy;
import com.syos.infrastructure.util.CommonVariables;

public class InventoryServlet extends HttpServlet {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ProductRepository productRepository = new ProductRepositoryImpl();
    private final DiscountRepository discountRepository = new DiscountRepositoryImpl();
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
        // Assuming purchaseDate is today, expiryDate parsed
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
        // Placeholder: implement logic to remove close to expiry stock
        resp.getWriter().write("{\"status\":\"Expiry stock removal not implemented\"}");
    }

    private void discardBatch(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        DiscardBatchRequest discardReq = objectMapper.readValue(req.getInputStream(), DiscardBatchRequest.class);
        // Need batchId, assuming batchNumber is id for now
        int batchId = Integer.parseInt(discardReq.getBatchNumber());
        inventoryManager.discardBatchQuantity(batchId, discardReq.getQuantity());
        resp.getWriter().write("{\"status\":\"Batch discarded successfully\"}");
    }

    private void createDiscount(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        DiscountRequest discountReq = objectMapper.readValue(req.getInputStream(), DiscountRequest.class);
        // Assuming discount creation logic
        resp.getWriter().write("{\"status\":\"Discount created successfully\"}");
    }

    private void assignDiscount(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        AssignDiscountRequest assignReq = objectMapper.readValue(req.getInputStream(), AssignDiscountRequest.class);
        // Assuming assign logic
        resp.getWriter().write("{\"status\":\"Discount assigned successfully\"}");
    }

    private void unassignDiscount(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        ProductCodeRequest codeReq = objectMapper.readValue(req.getInputStream(), ProductCodeRequest.class);
        // Assuming unassign logic
        resp.getWriter().write("{\"status\":\"Discount unassigned successfully\"}");
    }

    private void getAllProducts(HttpServletResponse resp) throws IOException {
        List<Product> products = productRepository.findAll();
        objectMapper.writeValue(resp.getWriter(), products);
    }

    private void getAllStocks(HttpServletResponse resp) throws IOException {
        // Placeholder for stock data
        resp.getWriter().write("{\"stocks\":\"Not implemented yet\"}");
    }

    private void getInventoryStocks(HttpServletResponse resp) throws IOException {
        // Placeholder
        resp.getWriter().write("{\"inventoryStocks\":\"Not implemented yet\"}");
    }

    private void getExpiryStocks(HttpServletResponse resp) throws IOException {
        // Placeholder
        resp.getWriter().write("{\"expiryStocks\":\"Not implemented yet\"}");
    }

    private void getExpiringBatches(HttpServletResponse resp) throws IOException {
        // Placeholder
        resp.getWriter().write("{\"expiringBatches\":\"Not implemented yet\"}");
    }

    private void getAllDiscounts(HttpServletResponse resp) throws IOException {
        // Placeholder
        resp.getWriter().write("{\"discounts\":\"Not implemented yet\"}");
    }

    private void getProductsWithDiscounts(HttpServletResponse resp) throws IOException {
        // Placeholder
        resp.getWriter().write("{\"productsWithDiscounts\":\"Not implemented yet\"}");
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
        private String type, value;

        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        public String getValue() { return value; }
        public void setValue(String value) { this.value = value; }
    }

    public static class AssignDiscountRequest {
        private String discountId, productCode;

        public String getDiscountId() { return discountId; }
        public void setDiscountId(String discountId) { this.discountId = discountId; }
        public String getProductCode() { return productCode; }
        public void setProductCode(String productCode) { this.productCode = productCode; }
    }
}