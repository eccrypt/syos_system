<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.syos.domain.model.User" %>
<%
    // Check if user is logged in
    User user = (User) session.getAttribute("user");
    String userRole = (String) session.getAttribute("userRole");

    // Redirect based on user role
    if (user != null) {
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
    <title>SYOS - Supermarket Billing System</title>
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
                            <p class="text-muted">Supermarket Billing System</p>
                        </div>

                        <div class="d-grid gap-2">
                            <a href="<%= request.getContextPath() %>/login.jsp" class="btn btn-primary btn-lg">
                                Employee Login
                            </a>
                            <a href="<%= request.getContextPath() %>/register.jsp" class="btn btn-outline-primary btn-lg">
                                Register New Employee
                            </a>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>