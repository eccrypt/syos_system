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
import com.syos.domain.model.User;
import com.syos.infrastructure.repository.ProductRepository;
import com.syos.infrastructure.repository.ProductRepositoryImpl;

public class ProductWebServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private final ProductRepository productRepository;
    private final ProductService productService;

    public ProductWebServlet() {
        this.productRepository = new ProductRepositoryImpl();
        this.productService = new ProductServiceImpl(productRepository);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getPathInfo();
        if (path == null) path = "/";

        if ("/list".equals(path)) {
            listProducts(req, resp);
        } else {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getPathInfo();
        if (path == null) path = "/";

        // Check authentication for admin operations
        User user = (User) req.getSession().getAttribute("user");
        String userRole = (String) req.getSession().getAttribute("userRole");

        if (user == null || (!"ADMIN".equals(userRole) && !"STAFF".equals(userRole))) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Admin access required");
            return;
        }

        if ("/add".equals(path)) {
            addProduct(req, resp);
        } else if ("/update".equals(path)) {
            updateProduct(req, resp);
        } else if ("/delete".equals(path)) {
            deleteProduct(req, resp);
        } else {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    private void listProducts(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            List<Product> products = productRepository.findAll();
            req.setAttribute("products", products);
            req.getRequestDispatcher("/WEB-INF/jsp/admin/products/list.jsp").forward(req, resp);
        } catch (Exception e) {
            req.setAttribute("error", "Failed to load products: " + e.getMessage());
            req.getRequestDispatcher("/WEB-INF/jsp/admin/products/list.jsp").forward(req, resp);
        }
    }

    private void addProduct(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        String code = req.getParameter("code");
        String name = req.getParameter("name");
        String priceStr = req.getParameter("price");

        // Validation
        if (code == null || code.trim().isEmpty()) {
            req.setAttribute("error", "Product code is required");
            req.getRequestDispatcher("/WEB-INF/jsp/admin/products/add.jsp").forward(req, resp);
            return;
        }

        if (name == null || name.trim().isEmpty()) {
            req.setAttribute("error", "Product name is required");
            req.getRequestDispatcher("/WEB-INF/jsp/admin/products/add.jsp").forward(req, resp);
            return;
        }

        double price;
        try {
            price = Double.parseDouble(priceStr);
            if (price <= 0) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            req.setAttribute("error", "Valid price is required");
            req.getRequestDispatcher("/WEB-INF/jsp/admin/products/add.jsp").forward(req, resp);
            return;
        }

        try {
            productService.addProduct(code, name, price);
            req.setAttribute("success", "Product added successfully");
            resp.sendRedirect(req.getContextPath() + "/admin/products/list.jsp");
        } catch (Exception e) {
            req.setAttribute("error", "Failed to add product: " + e.getMessage());
            req.getRequestDispatcher("/WEB-INF/jsp/admin/products/add.jsp").forward(req, resp);
        }
    }

    private void updateProduct(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        // Implementation for product update
        req.setAttribute("error", "Product update not implemented yet");
        resp.sendRedirect(req.getContextPath() + "/admin/products/list.jsp");
    }

    private void deleteProduct(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        String code = req.getParameter("code");

        if (code == null || code.trim().isEmpty()) {
            req.setAttribute("error", "Product code is required");
            resp.sendRedirect(req.getContextPath() + "/admin/products/list.jsp");
            return;
        }

        try {
            // Note: ProductService might not have delete method, this is a placeholder
            req.setAttribute("error", "Product deletion not implemented in service layer");
            resp.sendRedirect(req.getContextPath() + "/admin/products/list.jsp");
        } catch (Exception e) {
            req.setAttribute("error", "Failed to delete product: " + e.getMessage());
            resp.sendRedirect(req.getContextPath() + "/admin/products/list.jsp");
        }
    }
}