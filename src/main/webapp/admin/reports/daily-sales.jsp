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
                <a href="<%= request.getContextPath() %>/admin/reports/daily-sales.jsp" class="btn btn-outline-primary active">Daily Sales</a>
                <a href="<%= request.getContextPath() %>/admin/reports/transactions.jsp" class="btn btn-outline-primary">Transactions</a>
                <a href="<%= request.getContextPath() %>/admin/reports/product-stock.jsp" class="btn btn-outline-primary">Stock Report</a>
            </div>
        </div>

        <%-- Error/Success messages --%>
        <% String error = (String) request.getAttribute("error"); %>
        <% if (error != null) { %>
            <div class="alert alert-danger" role="alert">
                <%= error %>
            </div>
        <% } %>

        <!-- Date Selection -->
        <div class="card mb-4">
            <div class="card-body">
                <form method="get" class="row g-3">
                    <div class="col-md-4">
                        <label for="reportDate" class="form-label">Report Date</label>
                        <input type="date" class="form-control" id="reportDate" name="date"
                               value="<%= LocalDate.now() %>">
                    </div>
                    <div class="col-md-2">
                        <label class="form-label">&nbsp;</label>
                        <button type="submit" class="btn btn-primary d-block">
                            <i class="fas fa-search me-1"></i>Generate Report
                        </button>
                    </div>
                </form>
            </div>
        </div>

        <!-- Report Summary Cards -->
        <div class="row mb-4">
            <div class="col-md-3 mb-3">
                <div class="card text-center">
                    <div class="card-body">
                        <i class="fas fa-shopping-cart fa-2x text-success mb-2"></i>
                        <h4 class="mb-0">--</h4>
                        <small class="text-muted">Total Sales</small>
                    </div>
                </div>
            </div>
            <div class="col-md-3 mb-3">
                <div class="card text-center">
                    <div class="card-body">
                        <i class="fas fa-receipt fa-2x text-primary mb-2"></i>
                        <h4 class="mb-0">--</h4>
                        <small class="text-muted">Transactions</small>
                    </div>
                </div>
            </div>
            <div class="col-md-3 mb-3">
                <div class="card text-center">
                    <div class="card-body">
                        <i class="fas fa-dollar-sign fa-2x text-warning mb-2"></i>
                        <h4 class="mb-0">$--</h4>
                        <small class="text-muted">Average Sale</small>
                    </div>
                </div>
            </div>
            <div class="col-md-3 mb-3">
                <div class="card text-center">
                    <div class="card-body">
                        <i class="fas fa-chart-line fa-2x text-info mb-2"></i>
                        <h4 class="mb-0">--%</h4>
                        <small class="text-muted">Growth</small>
                    </div>
                </div>
            </div>
        </div>

        <!-- Sales Details Table -->
        <div class="card">
            <div class="card-header">
                <h5 class="mb-0">Sales Details</h5>
            </div>
            <div class="card-body">
                <div class="table-responsive">
                    <table class="table table-striped">
                        <thead>
                            <tr>
                                <th>Time</th>
                                <th>Bill #</th>
                                <th>Items</th>
                                <th>Total</th>
                                <th>Payment</th>
                            </tr>
                        </thead>
                        <tbody>
                            <!-- Sales data would be populated here -->
                            <tr>
                                <td colspan="5" class="text-center py-4">
                                    <i class="fas fa-chart-bar fa-2x text-muted mb-2"></i>
                                    <p class="text-muted mb-0">No sales data available for the selected date</p>
                                    <small class="text-muted">Sales transactions will appear here</small>
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
    const date = document.getElementById('reportDate').value;
    const url = '<%= request.getContextPath() %>/admin/reports/export?format=' + format + '&date=' + date + '&type=daily-sales';

    // In a real implementation, this would trigger a download
    alert('Export functionality would download ' + format.toUpperCase() + ' report for ' + date);
    // window.open(url, '_blank');
}
</script>

<%@ include file="/WEB-INF/jsp/common/footer.jsp" %>