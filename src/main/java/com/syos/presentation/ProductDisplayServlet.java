package com.syos.presentation;

import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.syos.application.service.ProductService;
import com.syos.application.service.ProductServiceImpl;
import com.syos.domain.model.Product;
import com.syos.infrastructure.repository.ProductRepository;
import com.syos.infrastructure.repository.ProductRepositoryImpl;

public class ProductDisplayServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private final ProductRepository productRepository;
    private final ProductService productService;

    public ProductDisplayServlet() {
        this.productRepository = new ProductRepositoryImpl();
        this.productService = new ProductServiceImpl(productRepository);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getPathInfo();
        if (path == null) path = "/";

        try {
            if ("/".equals(path) || "/catalog".equals(path)) {
                showProductCatalog(req, resp);
            } else if (path.startsWith("/product/")) {
                String productCode = path.substring("/product/".length());
                showProductDetails(productCode, req, resp);
            } else {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (Exception e) {
            req.setAttribute("error", "Failed to load products: " + e.getMessage());
            req.getRequestDispatcher("/products.jsp").forward(req, resp);
        }
    }

    private void showProductCatalog(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Get search/filter parameters
        String search = req.getParameter("search");
        String category = req.getParameter("category");

        List<Product> products;

        if (search != null && !search.trim().isEmpty()) {
            // In a real implementation, you'd have a search method
            products = productRepository.findAll();
            // Filter by search term (basic implementation)
            products = products.stream()
                    .filter(p -> p.getName().toLowerCase().contains(search.toLowerCase()) ||
                               p.getCode().toLowerCase().contains(search.toLowerCase()))
                    .toList();
        } else {
            products = productRepository.findAll();
        }

        req.setAttribute("products", products);
        req.setAttribute("searchTerm", search);
        req.setAttribute("selectedCategory", category);

        req.getRequestDispatcher("/products.jsp").forward(req, resp);
    }

    private void showProductDetails(String productCode, HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        Product product = productService.findProductByCode(productCode);

        if (product == null) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Product not found");
            return;
        }

        req.setAttribute("product", product);
        req.getRequestDispatcher("/product-details.jsp").forward(req, resp);
    }
}