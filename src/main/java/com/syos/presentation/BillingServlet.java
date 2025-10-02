package com.syos.presentation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.syos.application.service.BillingService;
import com.syos.domain.model.Bill;

public class BillingServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final BillingService billingService = new BillingService();

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