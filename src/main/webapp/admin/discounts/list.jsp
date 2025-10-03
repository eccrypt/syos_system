<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="java.time.LocalDate" %>
<%@ page import="com.syos.domain.model.Discount" %>
<%@ page import="com.syos.domain.model.User" %>
<%@ page import="com.syos.infrastructure.repository.DiscountRepository" %>
<%@ page import="com.syos.infrastructure.repository.DiscountRepositoryImpl" %>
<%
    // Check authentication and role
    User user = (User) session.getAttribute("user");
    if (user == null) {
        response.sendRedirect(request.getContextPath() + "/login.jsp");
        return;
    }

    String userRole = (String) session.getAttribute("userRole");
    if (!"ADMIN".equals(userRole) && !"STAFF".equals(userRole)) {
        response.sendRedirect(request.getContextPath() + "/index.jsp");
        return;
    }

    request.setAttribute("pageTitle", "Discount Management");
%>
<%@ include file="/WEB-INF/jsp/common/header.jsp" %>

<div class="row">
    <!-- Sidebar -->
    <div class="col-md-3 col-lg-2 px-0">
        <div class="sidebar">
            <nav class="nav flex-column py-3">
                <a class="nav-link" href="<%= request.getContextPath() %>/admin/dashboard.jsp">
                    <i class="fas fa-tachometer-alt me-2"></i>Dashboard
                </a>
                <a class="nav-link" href="<%= request.getContextPath() %>/admin/products/list.jsp">
                    <i class="fas fa-box me-2"></i>Products
                </a>
                <a class="nav-link" href="<%= request.getContextPath() %>/admin/inventory/stocks.jsp">
                    <i class="fas fa-warehouse me-2"></i>Inventory
                </a>
                <a class="nav-link active" href="<%= request.getContextPath() %>/admin/discounts/list.jsp">
                    <i class="fas fa-tags me-2"></i>Discounts
                </a>
                <a class="nav-link" href="<%= request.getContextPath() %>/admin/billing/create-bill.jsp">
                    <i class="fas fa-cash-register me-2"></i>Billing
                </a>
                <a class="nav-link" href="<%= request.getContextPath() %>/admin/reports/daily-sales.jsp">
                    <i class="fas fa-chart-bar me-2"></i>Reports
                </a>
            </nav>
        </div>
    </div>

    <!-- Main content -->
    <div class="col-md-9 col-lg-10 main-content">
        <div class="d-flex justify-content-between align-items-center mb-4">
            <h2>Discount Management</h2>
            <a href="<%= request.getContextPath() %>/admin/discounts/create.jsp" class="btn btn-success">
                <i class="fas fa-plus me-1"></i>Create Discount
            </a>
        </div>


        <div class="card">
            <div class="card-header">
                <h5 class="mb-0">Active Discounts</h5>
            </div>
            <div class="card-body">
                <div class="table-responsive">
                    <table class="table table-striped">
                        <thead>
                            <tr>
                                <th>ID</th>
                                <th>Name</th>
                                <th>Type</th>
                                <th>Value</th>
                                <th>Start Date</th>
                                <th>End Date</th>
                                <th>Status</th>
                                <th>Actions</th>
                            </tr>
                        </thead>
                        <tbody>
                            <%
                                // Fetch discounts from repository
                                DiscountRepository discountRepository = new DiscountRepositoryImpl();
                                List<Discount> discounts = discountRepository.findAll();
                                if (discounts != null && !discounts.isEmpty()) {
                                    for (Discount discount : discounts) {
                            %>
                                <tr>
                                    <td><%= discount.getId() %></td>
                                    <td><%= discount.getName() %></td>
                                    <td>
                                        <span class="badge bg-<%= discount.getType() == com.syos.domain.enums.DiscountType.PERCENT ? "info" : "success" %>">
                                            <%= discount.getType() %>
                                        </span>
                                    </td>
                                    <td>
                                        <%= discount.getType() == com.syos.domain.enums.DiscountType.PERCENT ?
                                            discount.getValue() + "%" : "$" + discount.getValue() %>
                                    </td>
                                    <td><%= discount.getStart() %></td>
                                    <td><%= discount.getEnd() %></td>
                                    <td>
                                        <% LocalDate now = LocalDate.now(); %>
                                        <% if (discount.isActiveOn(now)) { %>
                                            <span class="badge bg-success">Active</span>
                                        <% } else if (discount.getEnd().isBefore(now)) { %>
                                            <span class="badge bg-secondary">Expired</span>
                                        <% } else { %>
                                            <span class="badge bg-warning">Scheduled</span>
                                        <% } %>
                                    </td>
                                    <td>
                                        <div class="btn-group btn-group-sm">
                                            <button class="btn btn-outline-primary" title="Assign to Products">
                                                <i class="fas fa-link"></i>
                                            </button>
                                            <button class="btn btn-outline-secondary" title="Edit">
                                                <i class="fas fa-edit"></i>
                                            </button>
                                            <button class="btn btn-outline-danger" title="Delete">
                                                <i class="fas fa-trash"></i>
                                            </button>
                                        </div>
                                    </td>
                                </tr>
                            <%
                                    }
                                } else {
                            %>
                                <tr>
                                    <td colspan="8" class="text-center py-4">
                                        <i class="fas fa-tags fa-2x text-muted mb-2"></i>
                                        <p class="text-muted mb-0">No discounts found</p>
                                        <small class="text-muted">Create your first discount to get started</small>
                                    </td>
                                </tr>
                            <% } %>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    </div>
</div>

<%@ include file="/WEB-INF/jsp/common/footer.jsp" %>