package com.syos.presentation;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.syos.application.dto.BillReportDTO;
import com.syos.application.dto.ProductStockReportItemDTO;
import com.syos.application.service.ReportService;
import com.syos.infrastructure.repository.ProductRepository;
import com.syos.infrastructure.repository.ProductRepositoryImpl;
import com.syos.infrastructure.repository.ReportRepository;
import com.syos.infrastructure.repository.ShelfStockRepository;
import com.syos.infrastructure.repository.ShelfStockRepositoryImpl;
import com.syos.infrastructure.repository.StockBatchRepository;
import com.syos.infrastructure.repository.StockBatchRepositoryImpl;

import java.util.List;

public class ReportServlet extends HttpServlet {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ReportRepository reportRepository;

    public ReportServlet() {
        ProductRepository productRepository = new ProductRepositoryImpl();
        ShelfStockRepository shelfStockRepository = new ShelfStockRepositoryImpl(productRepository);
        StockBatchRepository stockBatchRepository = new StockBatchRepositoryImpl();
        this.reportRepository = new ReportRepository(productRepository, shelfStockRepository, stockBatchRepository);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getPathInfo();
        if (path == null) path = "/";

        resp.setContentType("application/json");

        try {
            switch (path) {
                case "/daily-sales":
                    generateDailySalesReport(req, resp);
                    break;
                case "/all-transactions":
                    generateAllTransactionsReport(resp);
                    break;
                case "/product-stock":
                    generateProductStockReport(resp);
                    break;
                case "/analysis":
                    generateAnalysisReport(resp);
                    break;
                default:
                    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    resp.getWriter().write("{\"error\":\"Unknown report\"}");
            }
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    private void generateDailySalesReport(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String dateParam = req.getParameter("date");
        LocalDate reportDate = null;
        if (dateParam != null) {
            try {
                reportDate = LocalDate.parse(dateParam);
            } catch (DateTimeParseException e) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write("{\"error\":\"Invalid date format. Use YYYY-MM-DD\"}");
                return;
            }
        } else {
            reportDate = LocalDate.now();
        }

        // Note: ReportService methods print to console, need to adapt for API
        // For now, return placeholder
        resp.getWriter().write("{\"report\":\"Daily sales report for " + reportDate + " - implementation needed\"}");
    }

    private void generateAllTransactionsReport(HttpServletResponse resp) throws IOException {
        resp.getWriter().write("{\"report\":\"All transactions report - implementation needed\"}");
    }

    private void generateProductStockReport(HttpServletResponse resp) throws IOException {
        List<ProductStockReportItemDTO> reportItems = reportRepository.getProductStockReportData(0);
        objectMapper.writeValue(resp.getWriter(), reportItems);
    }

    private void generateAnalysisReport(HttpServletResponse resp) throws IOException {
        resp.getWriter().write("{\"report\":\"Analysis report - implementation needed\"}");
    }
}