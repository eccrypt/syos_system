<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.syos.domain.model.User" %>
<%
    // Redirect if already logged in
    User user = (User) session.getAttribute("user");
    if (user != null) {
        String userRole = (String) session.getAttribute("userRole");
        if ("ADMIN".equals(userRole) || "STAFF".equals(userRole)) {
            response.sendRedirect(request.getContextPath() + "/admin/dashboard.jsp");
        } else {
            response.sendRedirect(request.getContextPath() + "/index.jsp");
        }
        return;
    }
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Employee Login - SYOS</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="<%= request.getContextPath() %>/css/custom.css" rel="stylesheet">
</head>
<body class="bg-light">
    <div class="container-fluid">
        <div class="row justify-content-center align-items-center min-vh-100">
            <div class="col-md-6 col-lg-4">
                <div class="card shadow">
                    <div class="card-body p-5">
                        <div class="text-center mb-4">
                            <h1 class="h3 mb-3 font-weight-normal">SYOS</h1>
                            <p class="text-muted">Employee Login</p>
                        </div>

                        <%-- Error message --%>
                        <% String error = (String) request.getAttribute("error"); %>
                        <% if (error != null) { %>
                            <div class="alert alert-danger" role="alert">
                                <%= error %>
                            </div>
                        <% } %>

                        <form action="<%= request.getContextPath() %>/auth" method="post">
                            <input type="hidden" name="action" value="login">

                            <div class="mb-3">
                                <label for="email" class="form-label">email</label>
                                <input type="text" class="form-control" id="email" name="email"
                                       placeholder="Enter your email" required>
                            </div>

                            <div class="mb-3">
                                <label for="password" class="form-label">Password</label>
                                <input type="password" class="form-control" id="password" name="password"
                                       placeholder="Enter your password" required>
                            </div>

                            <div class="d-grid">
                                <button type="submit" class="btn btn-primary btn-lg">
                                    Login
                                </button>
                            </div>
                        </form>

                        <div class="text-center mt-3">
                            <p class="mb-0">Don't have an account? <a href="<%= request.getContextPath() %>/register.jsp">Register here</a></p>
                        </div>

                    </div>
                </div>
            </div>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>