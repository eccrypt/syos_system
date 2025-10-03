package com.syos.presentation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.syos.application.service.BillingService;
import com.syos.domain.model.Bill;

public class BillingServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final BillingService billingService = new BillingService();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            resp.getWriter().write("{\"error\":\"Unauthorized\"}");
            return;
        }
        String userRole = (String) session.getAttribute("userRole");
        if (!"ADMIN".equals(userRole) && !"STAFF".equals(userRole)) {
            resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
            resp.getWriter().write("{\"error\":\"Forbidden\"}");
            return;
        }

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
        // Check authentication
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            resp.getWriter().write("{\"error\":\"Unauthorized\"}");
            return;
        }
        String userRole = (String) session.getAttribute("userRole");
        if (!"ADMIN".equals(userRole) && !"STAFF".equals(userRole)) {
            resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
            resp.getWriter().write("{\"error\":\"Forbidden\"}");
            return;
        }

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
            BillingService.BillRequest billRequest = objectMapper.readValue(req.getInputStream(), BillingService.BillRequest.class);
            Bill bill = billingService.createBill(billRequest);
            BillResponse billResponse = new BillResponse(bill.getSerialNumber(), bill.getTotalAmount(), bill.getCashTendered(), bill.getChangeReturned());
            resp.setContentType("application/json");
            objectMapper.writeValue(resp.getWriter(), billResponse);
        } catch (IllegalArgumentException e) {
            resp.setContentType("application/json");
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"error\":\"" + e.getMessage() + "\"}");
        } catch (Exception e) {
            resp.setContentType("application/json");
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }


    private void testConnection(HttpServletResponse resp) throws IOException {
        resp.getWriter().write("{\"status\":\"Billing API is working!\",\"timestamp\":\"" + java.time.LocalDateTime.now() + "\"}");
    }

    private void getAllBills(HttpServletResponse resp) throws IOException {
        try {
            List<Bill> bills = billingService.getAllBills();
            List<BillSummary> billSummaries = bills.stream()
                .map(bill -> new BillSummary(
                    bill.getSerialNumber(),
                    bill.getBillDate(),
                    bill.getTotalAmount(),
                    bill.getCashTendered(),
                    bill.getChangeReturned(),
                    bill.getItems() != null ? bill.getItems().size() : 0
                ))
                .toList();
            resp.setContentType("application/json");
            objectMapper.writeValue(resp.getWriter(), billSummaries);
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"error\":\"Failed to retrieve bills: " + e.getMessage() + "\"}");
        }
    }

    private static class BillSummary {
        public int serialNumber;
        public long billDate; // timestamp
        public double totalAmount;
        public double cashTendered;
        public double changeReturned;
        public int itemCount;

        public BillSummary(int serialNumber, Date billDate, double totalAmount, double cashTendered, double changeReturned, int itemCount) {
            this.serialNumber = serialNumber;
            this.billDate = billDate.getTime();
            this.totalAmount = totalAmount;
            this.cashTendered = cashTendered;
            this.changeReturned = changeReturned;
            this.itemCount = itemCount;
        }
    }

    private void getBillBySerial(int serialNumber, HttpServletResponse resp) throws IOException {
        try {
            Bill bill = billingService.getBillBySerial(serialNumber);
            if (bill != null) {
                BillDetail billDetail = new BillDetail(
                    bill.getId(),
                    bill.getSerialNumber(),
                    bill.getBillDate(),
                    bill.getTotalAmount(),
                    bill.getCashTendered(),
                    bill.getChangeReturned(),
                    bill.getItems() != null ? bill.getItems().stream()
                        .map(item -> new BillItemDetail(
                            item.getProduct().getCode(),
                            item.getProduct().getName(),
                            item.getQuantity(),
                            item.getTotalPrice(),
                            item.getDiscountAmount()
                        ))
                        .toList() : new ArrayList<>()
                );
                resp.setContentType("application/json");
                objectMapper.writeValue(resp.getWriter(), billDetail);
            } else {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                resp.getWriter().write("{\"error\":\"Bill with serial " + serialNumber + " not found\"}");
            }
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"error\":\"Failed to retrieve bill: " + e.getMessage() + "\"}");
        }
    }

    private static class BillDetail {
        public int id;
        public int serialNumber;
        public long billDate;
        public double totalAmount;
        public double cashTendered;
        public double changeReturned;
        public List<BillItemDetail> items;

        public BillDetail(int id, int serialNumber, Date billDate, double totalAmount, double cashTendered, double changeReturned, List<BillItemDetail> items) {
            this.id = id;
            this.serialNumber = serialNumber;
            this.billDate = billDate.getTime();
            this.totalAmount = totalAmount;
            this.cashTendered = cashTendered;
            this.changeReturned = changeReturned;
            this.items = items;
        }
    }

    private static class BillItemDetail {
        public String productCode;
        public String productName;
        public int quantity;
        public double totalPrice;
        public double discountAmount;

        public BillItemDetail(String productCode, String productName, int quantity, double totalPrice, double discountAmount) {
            this.productCode = productCode;
            this.productName = productName;
            this.quantity = quantity;
            this.totalPrice = totalPrice;
            this.discountAmount = discountAmount;
        }
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