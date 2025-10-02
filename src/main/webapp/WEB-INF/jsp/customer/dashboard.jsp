<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.syos.domain.model.User" %>
<%
    // Check authentication
    User user = (User) session.getAttribute("user");
    if (user == null) {
        response.sendRedirect(request.getContextPath() + "/login.jsp");
        return;
    }

    String userRole = (String) session.getAttribute("userRole");
    if ("ADMIN".equals(userRole) || "STAFF".equals(userRole)) {
        response.sendRedirect(request.getContextPath() + "/admin/dashboard.jsp");
        return;
    }

    request.setAttribute("pageTitle", "Customer Dashboard");
%>
<%@ include file="/WEB-INF/jsp/common/header.jsp" %>

<div class="row">
    <div class="col-12">
        <div class="d-flex justify-content-between align-items-center mb-4">
            <h2>Welcome back, <%= user.getFirstName() %>!</h2>
            <span class="badge bg-primary">Customer</span>
        </div>

        <%-- Error/Success messages --%>
        <% String error = (String) request.getAttribute("error"); %>
        <% if (error != null) { %>
            <div class="alert alert-danger" role="alert">
                <%= error %>
            </div>
        <% } %>

        <% String success = (String) request.getAttribute("success"); %>
        <% if (success != null) { %>
            <div class="alert alert-success" role="alert">
                <%= success %>
            </div>
        <% } %>

        <div class="row">
            <div class="col-md-6 col-lg-3 mb-4">
                <div class="card h-100">
                    <div class="card-body text-center">
                        <i class="fas fa-shopping-bag fa-3x text-primary mb-3"></i>
                        <h5 class="card-title">Shop Now</h5>
                        <p class="card-text">Browse our wide selection of products</p>
                        <a href="<%= request.getContextPath() %>/products.jsp" class="btn btn-primary">Start Shopping</a>
                    </div>
                </div>
            </div>

            <div class="col-md-6 col-lg-3 mb-4">
                <div class="card h-100">
                    <div class="card-body text-center">
                        <i class="fas fa-shopping-cart fa-3x text-success mb-3"></i>
                        <h5 class="card-title">My Cart</h5>
                        <p class="card-text">View and manage your shopping cart</p>
                        <a href="<%= request.getContextPath() %>/customer/cart.jsp" class="btn btn-success">View Cart</a>
                    </div>
                </div>
            </div>

            <div class="col-md-6 col-lg-3 mb-4">
                <div class="card h-100">
                    <div class="card-body text-center">
                        <i class="fas fa-history fa-3x text-info mb-3"></i>
                        <h5 class="card-title">Order History</h5>
                        <p class="card-text">View your past orders and receipts</p>
                        <a href="<%= request.getContextPath() %>/customer/orders.jsp" class="btn btn-info">View Orders</a>
                    </div>
                </div>
            </div>

            <div class="col-md-6 col-lg-3 mb-4">
                <div class="card h-100">
                    <div class="card-body text-center">
                        <i class="fas fa-user fa-3x text-secondary mb-3"></i>
                        <h5 class="card-title">My Profile</h5>
                        <p class="card-text">Manage your account information</p>
                        <a href="<%= request.getContextPath() %>/customer/profile.jsp" class="btn btn-secondary">View Profile</a>
                    </div>
                </div>
            </div>
        </div>

        <div class="row mt-4">
            <div class="col-12">
                <div class="card">
                    <div class="card-header">
                        <h5 class="mb-0">Recent Activity</h5>
                    </div>
                    <div class="card-body">
                        <div class="text-center py-4">
                            <i class="fas fa-clock fa-2x text-muted mb-3"></i>
                            <p class="text-muted mb-0">No recent activity to display</p>
                            <small class="text-muted">Your order history and recent actions will appear here</small>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<%@ include file="/WEB-INF/jsp/common/footer.jsp" %>