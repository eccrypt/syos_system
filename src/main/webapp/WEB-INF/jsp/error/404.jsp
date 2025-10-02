<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isErrorPage="true" %>
<%@ page import="com.syos.domain.model.User" %>
<%
    request.setAttribute("pageTitle", "Page Not Found");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>404 - Page Not Found</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="<%= request.getContextPath() %>/css/custom.css" rel="stylesheet">
</head>
<body class="bg-light">
    <div class="container-fluid">
        <div class="row justify-content-center align-items-center min-vh-100">
            <div class="col-md-6 col-lg-4 text-center">
                <div class="card shadow">
                    <div class="card-body p-5">
                        <div class="mb-4">
                            <i class="fas fa-exclamation-triangle fa-4x text-warning"></i>
                        </div>
                        <h1 class="h2 mb-3">404</h1>
                        <h2 class="h4 mb-3">Page Not Found</h2>
                        <p class="text-muted mb-4">
                            The page you're looking for doesn't exist or has been moved.
                        </p>
                        <div class="d-grid gap-2">
                            <a href="<%= request.getContextPath() %>/index.jsp" class="btn btn-primary">
                                Go Home
                            </a>
                            <button onclick="history.back()" class="btn btn-outline-secondary">
                                Go Back
                            </button>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>