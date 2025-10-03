<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.time.LocalDate" %>
<%@ page import="com.syos.domain.model.User" %>
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

    request.setAttribute("pageTitle", "Reports & Analytics");
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
                <a class="nav-link" href="<%= request.getContextPath() %>/admin/discounts/list.jsp">
                    <i class="fas fa-tags me-2"></i>Discounts
                </a>
                <a class="nav-link" href="<%= request.getContextPath() %>/admin/billing/create-bill.jsp">
                    <i class="fas fa-cash-register me-2"></i>Billing
                </a>
                <a class="nav-link active" href="<%= request.getContextPath() %>/admin/reports/daily-sales.jsp">
                    <i class="fas fa-chart-bar me-2"></i>Reports
                </a>
            </nav>
        </div>
    </div>

    <!-- Main content -->
    <div class="col-md-9 col-lg-10 main-content">
        <div class="d-flex justify-content-between align-items-center mb-4">
            <h2>Reports & Analytics</h2>
            <div class="btn-group" role="group">
                <a href="<%= request.getContextPath() %>/admin/reports/daily-sales.jsp" class="btn btn-outline-primary">Daily Sales</a>
                <a href="<%= request.getContextPath() %>/admin/reports/transactions.jsp" class="btn btn-outline-primary">Transactions</a>
                <a href="<%= request.getContextPath() %>/admin/reports/product-stock.jsp" class="btn btn-outline-primary active">Stock Report</a>
            </div>
        </div>

        <%-- Error/Success messages --%>
        <% String error = (String) request.getAttribute("error"); %>
        <% if (error != null) { %>
            <div class="alert alert-danger" role="alert">
                <%= error %>
            </div>
        <% } %>

        <!-- Filters -->
        <div class="card mb-4">
            <div class="card-body">
                <form method="get" class="row g-3">
                    <div class="col-md-4">
                        <label for="category" class="form-label">Category</label>
                        <select class="form-control" id="category" name="category">
                            <option value="">All Categories</option>
                            <option value="food">Food</option>
                            <option value="beverages">Beverages</option>
                            <option value="household">Household</option>
                        </select>
                    </div>
                    <div class="col-md-4">
                        <label for="stockStatus" class="form-label">Stock Status</label>
                        <select class="form-control" id="stockStatus" name="stockStatus">
                            <option value="">All Status</option>
                            <option value="in-stock">In Stock</option>
                            <option value="low-stock">Low Stock</option>
                            <option value="out-of-stock">Out of Stock</option>
                        </select>
                    </div>
                    <div class="col-md-2">
                        <label class="form-label">&nbsp;</label>
                        <button type="submit" class="btn btn-primary d-block">
                            <i class="fas fa-filter me-1"></i>Filter
                        </button>
                    </div>
                </form>
            </div>
        </div>

        <!-- Stock Summary Cards -->
        <div class="row mb-4">
            <div class="col-md-3 mb-3">
                <div class="card text-center">
                    <div class="card-body">
                        <i class="fas fa-boxes fa-2x text-success mb-2"></i>
                        <h4 class="mb-0">--</h4>
                        <small class="text-muted">Total Products</small>
                    </div>
                </div>
            </div>
            <div class="col-md-3 mb-3">
                <div class="card text-center">
                    <div class="card-body">
                        <i class="fas fa-check-circle fa-2x text-primary mb-2"></i>
                        <h4 class="mb-0">--</h4>
                        <small class="text-muted">In Stock</small>
                    </div>
                </div>
            </div>
            <div class="col-md-3 mb-3">
                <div class="card text-center">
                    <div class="card-body">
                        <i class="fas fa-exclamation-triangle fa-2x text-warning mb-2"></i>
                        <h4 class="mb-0">--</h4>
                        <small class="text-muted">Low Stock</small>
                    </div>
                </div>
            </div>
            <div class="col-md-3 mb-3">
                <div class="card text-center">
                    <div class="card-body">
                        <i class="fas fa-times-circle fa-2x text-danger mb-2"></i>
                        <h4 class="mb-0">--</h4>
                        <small class="text-muted">Out of Stock</small>
                    </div>
                </div>
            </div>
        </div>

        <!-- Product Stock Table -->
        <div class="card">
            <div class="card-header">
                <h5 class="mb-0">Product Stock Report</h5>
            </div>
            <div class="card-body">
                <div class="table-responsive">
                    <table class="table table-striped">
                        <thead>
                            <tr>
                                <th>Product ID</th>
                                <th>Product Name</th>
                                <th>Category</th>
                                <th>Current Stock</th>
                                <th>Min Stock Level</th>
                                <th>Status</th>
                                <th>Last Updated</th>
                            </tr>
                        </thead>
                        <tbody>
                            <!-- Product stock data would be populated here -->
                            <tr>
                                <td colspan="7" class="text-center py-4">
                                    <i class="fas fa-box-open fa-2x text-muted mb-2"></i>
                                    <p class="text-muted mb-0">No product stock data available</p>
                                    <small class="text-muted">Product stock information will appear here</small>
                                </td>
                            </tr>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>

        <!-- Export Options -->
        <div class="card mt-4">
            <div class="card-body">
                <h6 class="card-title">Export Report</h6>
                <div class="btn-group">
                    <button class="btn btn-outline-secondary" onclick="exportReport('pdf')">
                        <i class="fas fa-file-pdf me-1"></i>PDF
                    </button>
                    <button class="btn btn-outline-secondary" onclick="exportReport('excel')">
                        <i class="fas fa-file-excel me-1"></i>Excel
                    </button>
                    <button class="btn btn-outline-secondary" onclick="exportReport('csv')">
                        <i class="fas fa-file-csv me-1"></i>CSV
                    </button>
                </div>
            </div>
        </div>
    </div>
</div>

<script>
function exportReport(format) {
    const category = document.getElementById('category').value;
    const stockStatus = document.getElementById('stockStatus').value;
    let url = '<%= request.getContextPath() %>/admin/reports/export?format=' + format + '&type=product-stock';
    if (category) url += '&category=' + category;
    if (stockStatus) url += '&stockStatus=' + stockStatus;

    // In a real implementation, this would trigger a download
    alert('Export functionality would download ' + format.toUpperCase() + ' report');
    // window.open(url, '_blank');
}
</script>

<%@ include file="/WEB-INF/jsp/common/footer.jsp" %>