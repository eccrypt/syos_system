package com.syos.presentation;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.syos.domain.model.Customer;
import com.syos.domain.model.User;
import com.syos.infrastructure.repository.CustomerRepository;
import com.syos.infrastructure.repository.CustomerRepositoryImpl;
import org.mindrot.jbcrypt.BCrypt;

public class AuthServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private final CustomerRepository customerRepository = new CustomerRepositoryImpl();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");

        if ("login".equals(action)) {
            handleLogin(req, resp);
        } else if ("register".equals(action)) {
            handleRegister(req, resp);
        } else {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid action");
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");

        if ("logout".equals(action)) {
            handleLogout(req, resp);
        } else {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid action");
        }
    }

    private void handleLogin(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        String email = req.getParameter("email");
        String password = req.getParameter("password");

        if (email == null || email.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            req.setAttribute("error", "Email and password are required");
            req.getRequestDispatcher("/login.jsp").forward(req, resp);
            return;
        }

        try {
            Customer customer = customerRepository.findByEmail(email);

            if (customer == null || !BCrypt.checkpw(password, customer.getPassword())) {
                req.setAttribute("error", "Invalid email or password");
                req.getRequestDispatcher("/login.jsp").forward(req, resp);
                return;
            }

            // Create session
            HttpSession session = req.getSession(true);
            session.setAttribute("user", customer);
            session.setAttribute("userRole", "CUSTOMER");
            session.setMaxInactiveInterval(30 * 60); // 30 minutes

            // Redirect to customer dashboard
            resp.sendRedirect(req.getContextPath() + "/customer/dashboard.jsp");

        } catch (Exception e) {
            req.setAttribute("error", "Login failed: " + e.getMessage());
            req.getRequestDispatcher("/login.jsp").forward(req, resp);
        }
    }

    private void handleRegister(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        String firstName = req.getParameter("firstName");
        String lastName = req.getParameter("lastName");
        String email = req.getParameter("email");
        String password = req.getParameter("password");
        String confirmPassword = req.getParameter("confirmPassword");

        // Validation
        if (firstName == null || firstName.trim().isEmpty()) {
            req.setAttribute("error", "First name is required");
            req.getRequestDispatcher("/register.jsp").forward(req, resp);
            return;
        }

        if (lastName == null || lastName.trim().isEmpty()) {
            req.setAttribute("error", "Last name is required");
            req.getRequestDispatcher("/register.jsp").forward(req, resp);
            return;
        }

        if (email == null || email.trim().isEmpty()) {
            req.setAttribute("error", "Email is required");
            req.getRequestDispatcher("/register.jsp").forward(req, resp);
            return;
        }

        if (password == null || password.length() < 6) {
            req.setAttribute("error", "Password must be at least 6 characters long");
            req.getRequestDispatcher("/register.jsp").forward(req, resp);
            return;
        }

        if (!password.equals(confirmPassword)) {
            req.setAttribute("error", "Passwords do not match");
            req.getRequestDispatcher("/register.jsp").forward(req, resp);
            return;
        }

        try {
            // Check if email already exists
            Customer existingCustomer = customerRepository.findByEmail(email);
            if (existingCustomer != null) {
                req.setAttribute("error", "Email already registered");
                req.getRequestDispatcher("/register.jsp").forward(req, resp);
                return;
            }

            // Create new customer
            Customer customer = new Customer.CustomerBuilder()
                    .firstName(firstName)
                    .lastName(lastName)
                    .email(email)
                    .password(BCrypt.hashpw(password, BCrypt.gensalt()))
                    .build();

            customerRepository.save(customer);

            req.setAttribute("success", "Registration successful! Please login.");
            req.getRequestDispatcher("/login.jsp").forward(req, resp);

        } catch (Exception e) {
            req.setAttribute("error", "Registration failed: " + e.getMessage());
            req.getRequestDispatcher("/register.jsp").forward(req, resp);
        }
    }

    private void handleLogout(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        resp.sendRedirect(req.getContextPath() + "/index.jsp");
    }
}