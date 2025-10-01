package com.syos.presentation;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.syos.application.dto.CustomerRegisterRequestDTO;
import com.syos.domain.enums.UserType;
import com.syos.domain.model.Customer;
import com.syos.domain.model.Product;
import com.syos.infrastructure.repository.CustomerRepository;
import com.syos.infrastructure.repository.CustomerRepositoryImpl;
import com.syos.infrastructure.repository.ProductRepository;
import com.syos.infrastructure.repository.ProductRepositoryImpl;
import org.mindrot.jbcrypt.BCrypt;

public class OnlineStoreServlet extends HttpServlet {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final CustomerRepository customerRepository = new CustomerRepositoryImpl();
    private final ProductRepository productRepository = new ProductRepositoryImpl();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getPathInfo();
        if (path == null) path = "/";

        resp.setContentType("application/json");

        try {
            switch (path) {
                case "/register":
                    register(req, resp);
                    break;
                case "/login":
                    login(req, resp);
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
            if (path.equals("/products")) {
                getAllProducts(resp);
            } else if (path.startsWith("/products/")) {
                String code = path.substring("/products/".length());
                searchProduct(code, resp);
            } else {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                resp.getWriter().write("{\"error\":\"Unknown endpoint\"}");
            }
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    private void register(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        RegisterRequest registerReq = objectMapper.readValue(req.getInputStream(), RegisterRequest.class);
        Customer customer = new Customer.CustomerBuilder()
                .firstName(registerReq.getFirstName())
                .lastName(registerReq.getLastName())
                .email(registerReq.getEmail())
                .password(BCrypt.hashpw(registerReq.getPassword(), BCrypt.gensalt()))
                .build();
        customerRepository.save(customer);
        resp.getWriter().write("{\"status\":\"Registered successfully\"}");
    }

    private void login(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        LoginRequest loginReq = objectMapper.readValue(req.getInputStream(), LoginRequest.class);
        Customer customer = customerRepository.findByEmail(loginReq.getEmail());
        if (customer == null || !BCrypt.checkpw(loginReq.getPassword(), customer.getPassword())) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            resp.getWriter().write("{\"error\":\"Invalid credentials\"}");
            return;
        }
        LoginResponse response = new LoginResponse(customer.getId().toString(), customer.getFirstName(), customer.getLastName(), customer.getEmail());
        objectMapper.writeValue(resp.getWriter(), response);
    }

    private void getAllProducts(HttpServletResponse resp) throws IOException {
        List<Product> products = productRepository.findAll();
        objectMapper.writeValue(resp.getWriter(), products);
    }

    private void searchProduct(String code, HttpServletResponse resp) throws IOException {
        Product product = productRepository.findByCode(code);
        if (product == null) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            resp.getWriter().write("{\"error\":\"Product not found\"}");
            return;
        }
        objectMapper.writeValue(resp.getWriter(), product);
    }

    // DTOs
    public static class RegisterRequest {
        private String firstName, lastName, email, password;

        public String getFirstName() { return firstName; }
        public void setFirstName(String firstName) { this.firstName = firstName; }
        public String getLastName() { return lastName; }
        public void setLastName(String lastName) { this.lastName = lastName; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }

    public static class LoginRequest {
        private String email, password;

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }

    public static class LoginResponse {
        private String id;
        private String firstName, lastName, email;

        public LoginResponse(String id, String firstName, String lastName, String email) {
            this.id = id;
            this.firstName = firstName;
            this.lastName = lastName;
            this.email = email;
        }

        public String getId() { return id; }
        public String getFirstName() { return firstName; }
        public String getLastName() { return lastName; }
        public String getEmail() { return email; }
    }
}