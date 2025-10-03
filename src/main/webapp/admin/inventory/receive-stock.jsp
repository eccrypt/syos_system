<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="com.syos.domain.model.Product" %>
<%@ page import="com.syos.domain.model.User" %>
<%@ page import="com.syos.infrastructure.repository.ProductRepository" %>
<%@ page import="com.syos.infrastructure.repository.ProductRepositoryImpl" %>
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

    request.setAttribute("pageTitle", "Receive Stock");
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
                <a class="nav-link active" href="<%= request.getContextPath() %>/admin/inventory/stocks.jsp">
                    <i class="fas fa-warehouse me-2"></i>Inventory
                </a>
                <a class="nav-link" href="<%= request.getContextPath() %>/admin/discounts/list.jsp">
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
            <h2>Receive Stock</h2>
            <a href="<%= request.getContextPath() %>/admin/inventory/stocks.jsp" class="btn btn-secondary">
                <i class="fas fa-arrow-left me-1"></i>Back to Stocks
            </a>
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

        <!-- Receive Stock Form -->
        <div class="card">
            <div class="card-header">
                <h5 class="mb-0">Stock Receipt Details</h5>
            </div>
            <div class="card-body">
                <form method="post" action="<%= request.getContextPath() %>/inventory/receive-stock">
                    <div class="row">
                        <div class="col-md-6 mb-3">
                            <label for="productCode" class="form-label">Product <span class="text-danger">*</span></label>
                            <select class="form-control" id="productCode" name="productCode" required>
                                <option value="">Select Product</option>
                                <%
                                    ProductRepository productRepository = new ProductRepositoryImpl();
                                    List<Product> products = productRepository.findAll();
                                    if (products != null) {
                                        for (Product product : products) {
                                %>
                                    <option value="<%= product.getCode() %>"><%= product.getCode() %> - <%= product.getName() %></option>
                                <%
                                        }
                                    }
                                %>
                            </select>
                        </div>
                        <div class="col-md-6 mb-3">
                            <label for="supplier" class="form-label">Supplier</label>
                            <input type="text" class="form-control" id="supplier" name="supplier" placeholder="Enter supplier name">
                        </div>
                    </div>

                    <div class="row">
                        <div class="col-md-4 mb-3">
                            <label for="quantity" class="form-label">Quantity <span class="text-danger">*</span></label>
                            <input type="number" class="form-control" id="quantity" name="quantity" min="1" required placeholder="Enter quantity">
                        </div>
                        <div class="col-md-4 mb-3">
                            <label for="purchasePrice" class="form-label">Purchase Price per Unit</label>
                            <input type="number" class="form-control" id="purchasePrice" name="purchasePrice" step="0.01" min="0" placeholder="0.00">
                        </div>
                        <div class="col-md-4 mb-3">
                            <label for="sellingPrice" class="form-label">Selling Price per Unit</label>
                            <input type="number" class="form-control" id="sellingPrice" name="sellingPrice" step="0.01" min="0" placeholder="0.00">
                        </div>
                    </div>

                    <div class="row">
                        <div class="col-md-6 mb-3">
                            <label for="purchaseDate" class="form-label">Purchase Date <span class="text-danger">*</span></label>
                            <input type="date" class="form-control" id="purchaseDate" name="purchaseDate" required value="<%= java.time.LocalDate.now() %>">
                        </div>
                        <div class="col-md-6 mb-3">
                            <label for="expiryDate" class="form-label">Expiry Date</label>
                            <input type="date" class="form-control" id="expiryDate" name="expiryDate">
                        </div>
                    </div>

                    <div class="mb-3">
                        <label for="notes" class="form-label">Notes</label>
                        <textarea class="form-control" id="notes" name="notes" rows="3" placeholder="Additional notes about the stock receipt"></textarea>
                    </div>

                    <div class="d-flex justify-content-end">
                        <button type="button" class="btn btn-secondary me-2" onclick="history.back()">Cancel</button>
                        <button type="submit" class="btn btn-success">
                            <i class="fas fa-save me-1"></i>Receive Stock
                        </button>
                    </div>
                </form>
            </div>
        </div>

        <!-- Recent Stock Receipts -->
        <div class="card mt-4">
            <div class="card-header">
                <h5 class="mb-0">Recent Stock Receipts</h5>
            </div>
            <div class="card-body">
                <div class="table-responsive">
                    <table class="table table-striped">
                        <thead>
                            <tr>
                                <th>Date</th>
                                <th>Product</th>
                                <th>Quantity</th>
                                <th>Supplier</th>
                                <th>Status</th>
                            </tr>
                        </thead>
                        <tbody>
                            <!-- Recent receipts would be populated here -->
                            <tr>
                                <td colspan="5" class="text-center py-4">
                                    <i class="fas fa-history fa-2x text-muted mb-2"></i>
                                    <p class="text-muted mb-0">No recent stock receipts</p>
                                    <small class="text-muted">Recent stock receipts will appear here</small>
                                </td>
                            </tr>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    </div>
</div>

<script>
// Auto-fill expiry date based on product selection if needed
document.getElementById('productCode').addEventListener('change', function() {
    // Could add logic to auto-fill expiry date based on product type
});
</script>

<%@ include file="/WEB-INF/jsp/common/footer.jsp" %>