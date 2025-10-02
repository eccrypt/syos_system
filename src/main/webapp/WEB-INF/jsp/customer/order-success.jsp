<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.syos.domain.model.Bill" %>
<%@ page import="com.syos.domain.model.User" %>
<%
    // Check authentication
    User user = (User) session.getAttribute("user");
    if (user == null) {
        response.sendRedirect(request.getContextPath() + "/login.jsp");
        return;
    }

    Bill bill = (Bill) request.getAttribute("bill");
    if (bill == null) {
        response.sendRedirect(request.getContextPath() + "/customer/dashboard.jsp");
        return;
    }

    request.setAttribute("pageTitle", "Order Successful");
%>
<%@ include file="/WEB-INF/jsp/common/header.jsp" %>

<div class="row justify-content-center">
    <div class="col-md-8 col-lg-6">
        <div class="card shadow">
            <div class="card-body text-center p-5">
                <div class="mb-4">
                    <i class="fas fa-check-circle fa-4x text-success"></i>
                </div>
                <h2 class="mb-3">Order Successful!</h2>
                <p class="text-muted mb-4">Thank you for your purchase. Your order has been processed successfully.</p>

                <div class="card mb-4">
                    <div class="card-header">
                        <h5 class="mb-0">Order Details</h5>
                    </div>
                    <div class="card-body text-start">
                        <div class="row">
                            <div class="col-sm-6">
                                <strong>Order Number:</strong><br>
                                #<%= bill.getSerialNumber() %>
                            </div>
                            <div class="col-sm-6">
                                <strong>Date:</strong><br>
                                <%= bill.getDate() %>
                            </div>
                        </div>
                        <hr>
                        <div class="row">
                            <div class="col-sm-6">
                                <strong>Total Amount:</strong><br>
                                $<%= String.format("%.2f", bill.getTotalAmount()) %>
                            </div>
                            <div class="col-sm-6">
                                <strong>Cash Tendered:</strong><br>
                                $<%= String.format("%.2f", bill.getCashTendered()) %>
                            </div>
                        </div>
                        <% if (bill.getChangeReturned() > 0) { %>
                        <div class="row mt-2">
                            <div class="col-12">
                                <strong class="text-success">Change Returned:</strong>
                                $<%= String.format("%.2f", bill.getChangeReturned()) %>
                            </div>
                        </div>
                        <% } %>
                    </div>
                </div>

                <div class="d-grid gap-2">
                    <a href="<%= request.getContextPath() %>/customer/orders.jsp" class="btn btn-primary">
                        <i class="fas fa-list me-2"></i>View All Orders
                    </a>
                    <a href="<%= request.getContextPath() %>/products.jsp" class="btn btn-outline-primary">
                        <i class="fas fa-shopping-bag me-2"></i>Continue Shopping
                    </a>
                    <a href="<%= request.getContextPath() %>/customer/dashboard.jsp" class="btn btn-outline-secondary">
                        <i class="fas fa-home me-2"></i>Back to Dashboard
                    </a>
                </div>
            </div>
        </div>
    </div>
</div>

<%@ include file="/WEB-INF/jsp/common/footer.jsp" %>