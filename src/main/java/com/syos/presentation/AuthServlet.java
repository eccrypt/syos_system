package com.syos.presentation;

import java.io.IOException;
import java.sql.Timestamp;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.syos.domain.model.User;
import com.syos.domain.model.Employee;
import com.syos.domain.enums.UserType;
import com.syos.infrastructure.repository.UserRepository;
import com.syos.infrastructure.repository.UserRepositoryImpl;
import org.mindrot.jbcrypt.BCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public class AuthServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private final UserRepository userRepository = new UserRepositoryImpl();
    
    private static final Logger logger = LoggerFactory.getLogger(AuthServlet.class);

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
        String username = req.getParameter("email");
        String password = req.getParameter("password");

        if (username == null || username.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            req.setAttribute("error", "Username and password are required");
            req.getRequestDispatcher("/login.jsp").forward(req, resp);
            return;
        }

        try {
            User user = userRepository.findByEmail(username);

            if (user == null || !BCrypt.checkpw(password, user.getPassword())) {
                req.setAttribute("error", "Invalid username or password");
                req.getRequestDispatcher("/login.jsp").forward(req, resp);
                return;
            }

            HttpSession session = req.getSession(true);
            session.setAttribute("user", user);
            session.setAttribute("userRole", user.getRole().name());
            session.setMaxInactiveInterval(30 * 60); 

            if (user.getRole() == UserType.CUSTOMER) {
                resp.sendRedirect(req.getContextPath() + "/index.jsp");
            } else {
                resp.sendRedirect(req.getContextPath() + "/admin/dashboard.jsp");
            }

        } catch (Exception e) {
            req.setAttribute("error", "Login failed: " + e.getMessage());
            req.getRequestDispatcher("/login.jsp").forward(req, resp);
        }
    }
    

    private void handleRegister(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        String firstName = req.getParameter("firstName");
        String lastName = req.getParameter("lastName");
        String username = req.getParameter("username");
        String email = req.getParameter("email");
        String password = req.getParameter("password");
        String confirmPassword = req.getParameter("confirmPassword");
        String role = req.getParameter("role");

        logger.info("Received registration request for username={}, email={}", username, email);

        if (firstName == null || firstName.trim().isEmpty() ||
            lastName == null || lastName.trim().isEmpty() ||
            username == null || username.trim().isEmpty() ||
            email == null || email.trim().isEmpty() ||
            password == null || password.trim().isEmpty() ||
            confirmPassword == null || confirmPassword.trim().isEmpty() ||
            role == null || role.trim().isEmpty()) {

            logger.warn("Validation failed: Missing fields in registration form for email={}", email);
            req.setAttribute("error", "All fields are required");
            req.getRequestDispatcher("/register.jsp").forward(req, resp);
            return;
        }

        if (!password.equals(confirmPassword)) {
            logger.warn("Password mismatch for username={}, email={}", username, email);
            req.setAttribute("error", "Passwords do not match");
            req.getRequestDispatcher("/register.jsp").forward(req, resp);
            return;
        }

        try {
            if (userRepository.existsByEmail(username)) {
                logger.warn("Username already exists: {}", username);
                req.setAttribute("error", "Username already exists");
                req.getRequestDispatcher("/register.jsp").forward(req, resp);
                return;
            }

            UserType userType = UserType.valueOf(role.toUpperCase());
            Employee employee = new Employee(email, password, firstName, lastName, userType, new Timestamp(System.currentTimeMillis()));

            logger.info("Saving new employee: username={}, email={}, role={}", username, email, userType);
            userRepository.save(employee);

            logger.info("Registration successful for username={}, email={}", username, email);
            resp.sendRedirect(req.getContextPath() + "/login.jsp");

        } catch (Exception e) {
            logger.error("Registration failed for username={}, email={}. Cause: {}", username, email, e.getMessage(), e);
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