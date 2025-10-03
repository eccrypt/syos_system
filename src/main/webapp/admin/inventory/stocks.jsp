<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="com.syos.domain.model.StockBatch" %>
<%@ page import="com.syos.domain.model.User" %>
<%@ page import="com.syos.infrastructure.repository.StockBatchRepository" %>
<%@ page import="com.syos.infrastructure.repository.StockBatchRepositoryImpl" %>
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

    request.setAttribute("pageTitle", "Stock Management");
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
            <h2>Stock Management</h2>
            <div>
                <a href="<%= request.getContextPath() %>/admin/inventory/receive-stock.jsp" class="btn btn-success me-2">
                    <i class="fas fa-plus me-1"></i>Receive Stock
                </a>
                <a href="<%= request.getContextPath() %>/admin/inventory/move-stock.jsp" class="btn btn-primary">
                    <i class="fas fa-exchange-alt me-1"></i>Move to Shelf
                </a>
            </div>
        </div>


        <!-- Stock Overview -->
        <div class="row mb-4">
            <div class="col-md-4">
                <div class="card text-center">
                    <div class="card-body">
                        <i class="fas fa-boxes fa-2x text-primary mb-2"></i>
                        <h4 class="mb-0">--</h4>
                        <small class="text-muted">Total Stock Items</small>
                    </div>
                </div>
            </div>
            <div class="col-md-4">
                <div class="card text-center">
                    <div class="card-body">
                        <i class="fas fa-exclamation-triangle fa-2x text-warning mb-2"></i>
                        <h4 class="mb-0 text-warning">--</h4>
                        <small class="text-muted">Low Stock Items</small>
                    </div>
                </div>
            </div>
            <div class="col-md-4">
                <div class="card text-center">
                    <div class="card-body">
                        <i class="fas fa-calendar-times fa-2x text-danger mb-2"></i>
                        <h4 class="mb-0 text-danger">--</h4>
                        <small class="text-muted">Expiring Soon</small>
                    </div>
                </div>
            </div>
        </div>

        <!-- Stock Batches Table -->
        <div class="card">
            <div class="card-header">
                <h5 class="mb-0">Stock Batches</h5>
            </div>
            <div class="card-body">
                <div class="table-responsive">
                    <table class="table table-striped" id="stockTable">
                        <thead>
                            <tr>
                                <th>Product Code</th>
                                <th>Batch ID</th>
                                <th>Purchase Date</th>
                                <th>Expiry Date</th>
                                <th>Quantity</th>
                                <th>Remaining</th>
                                <th>Status</th>
                                <th>Actions</th>
                            </tr>
                        </thead>
                        <tbody>
                            <%
                                // Fetch stock batches from repository
                                StockBatchRepository stockBatchRepository = new StockBatchRepositoryImpl();
                                List<StockBatch> stockBatches = stockBatchRepository.findAllExpiringBatches(10000); // Large threshold to get all
                                if (stockBatches != null && !stockBatches.isEmpty()) {
                                    for (StockBatch batch : stockBatches) {
                            %>
                                <tr>
                                    <td><%= batch.getProductCode() %></td>
                                    <td><%= batch.getId() %></td>
                                    <td><%= batch.getPurchaseDate() %></td>
                                    <td><%= batch.getExpiryDate() %></td>
                                    <td><%= batch.getQuantityRemaining() %></td>
                                    <td><%= batch.getQuantityRemaining() %></td>
                                    <td>
                                        <% if (batch.getQuantityRemaining() == 0) { %>
                                            <span class="badge bg-secondary">Empty</span>
                                        <% } else { %>
                                            <span class="badge bg-success">Active</span>
                                        <% } %>
                                    </td>
                                    <td>
                                        <% if (batch.getQuantityRemaining() > 0) { %>
                                            <button class="btn btn-sm btn-outline-danger discard-batch-btn"
                                                    data-batch-id="<%= batch.getId() %>"
                                                    data-product-code="<%= batch.getProductCode() %>">
                                                <i class="fas fa-trash"></i> Discard
                                            </button>
                                        <% } %>
                                    </td>
                                </tr>
                            <%
                                    }
                                } else {
                            %>
                                <tr>
                                    <td colspan="8" class="text-center py-4">
                                        <i class="fas fa-box-open fa-2x text-muted mb-2"></i>
                                        <p class="text-muted mb-0">No stock batches found</p>
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

<script>
document.addEventListener('click', function(e) {
    if (e.target.classList.contains('discard-batch-btn') || e.target.closest('.discard-batch-btn')) {
        const button = e.target.classList.contains('discard-batch-btn') ? e.target : e.target.closest('.discard-batch-btn');
        const batchId = button.getAttribute('data-batch-id');
        const productCode = button.getAttribute('data-product-code');

        if (confirm(`Are you sure you want to discard batch ${batchId} for product ${productCode}?`)) {
            // This would normally make an AJAX call or submit a form
            const form = document.createElement('form');
            form.method = 'POST';
            form.action = '<%= request.getContextPath() %>/inventory/discard-batch';

            const batchIdInput = document.createElement('input');
            batchIdInput.type = 'hidden';
            batchIdInput.name = 'batchId';
            batchIdInput.value = batchId;

            form.appendChild(batchIdInput);
            document.body.appendChild(form);
            form.submit();
        }
    }
});
</script>

<%@ include file="/WEB-INF/jsp/common/footer.jsp" %>