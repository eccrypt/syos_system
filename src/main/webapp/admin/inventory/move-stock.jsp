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

    request.setAttribute("pageTitle", "Move Stock to Shelf");
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
            <h2>Move Stock to Shelf</h2>
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

        <!-- Move Stock Form -->
        <div class="card">
            <div class="card-header">
                <h5 class="mb-0">Stock Movement Details</h5>
            </div>
            <div class="card-body">
                <form method="post" action="<%= request.getContextPath() %>/inventory/move-to-shelf">
                    <div class="row">
                        <div class="col-md-6 mb-3">
                            <label for="batchId" class="form-label">Stock Batch <span class="text-danger">*</span></label>
                            <select class="form-control" id="batchId" name="batchId" required>
                                <option value="">Select Stock Batch</option>
                                <%
                                    StockBatchRepository stockBatchRepository = new StockBatchRepositoryImpl();
                                    List<StockBatch> stockBatches = stockBatchRepository.findAllExpiringBatches(10000); // Large threshold to get all
                                    if (stockBatches != null) {
                                        for (StockBatch batch : stockBatches) {
                                            if (batch.getQuantityRemaining() > 0) {
                                %>
                                    <option value="<%= batch.getId() %>">
                                        Batch <%= batch.getId() %> - <%= batch.getProductCode() %> (Available: <%= batch.getQuantityRemaining() %>)
                                    </option>
                                <%
                                            }
                                        }
                                    }
                                %>
                            </select>
                        </div>
                        <div class="col-md-6 mb-3">
                            <label for="quantity" class="form-label">Quantity to Move <span class="text-danger">*</span></label>
                            <input type="number" class="form-control" id="quantity" name="quantity" min="1" required placeholder="Enter quantity to move">
                        </div>
                    </div>

                    <div class="row">
                        <div class="col-md-6 mb-3">
                            <label for="shelfLocation" class="form-label">Shelf Location</label>
                            <input type="text" class="form-control" id="shelfLocation" name="shelfLocation" placeholder="e.g., A-01, Shelf 5">
                        </div>
                        <div class="col-md-6 mb-3">
                            <label for="reason" class="form-label">Reason for Movement</label>
                            <select class="form-control" id="reason" name="reason">
                                <option value="restock">Restock Shelf</option>
                                <option value="reorganization">Shelf Reorganization</option>
                                <option value="damaged">Replace Damaged Stock</option>
                                <option value="other">Other</option>
                            </select>
                        </div>
                    </div>

                    <div class="mb-3">
                        <label for="notes" class="form-label">Notes</label>
                        <textarea class="form-control" id="notes" name="notes" rows="3" placeholder="Additional notes about the stock movement"></textarea>
                    </div>

                    <div class="d-flex justify-content-end">
                        <button type="button" class="btn btn-secondary me-2" onclick="history.back()">Cancel</button>
                        <button type="submit" class="btn btn-primary">
                            <i class="fas fa-exchange-alt me-1"></i>Move Stock
                        </button>
                    </div>
                </form>
            </div>
        </div>

        <!-- Available Stock Batches -->
        <div class="card mt-4">
            <div class="card-header">
                <h5 class="mb-0">Available Stock Batches</h5>
            </div>
            <div class="card-body">
                <div class="table-responsive">
                    <table class="table table-striped">
                        <thead>
                            <tr>
                                <th>Batch ID</th>
                                <th>Product Code</th>
                                <th>Purchase Date</th>
                                <th>Expiry Date</th>
                                <th>Available Quantity</th>
                                <th>Status</th>
                                <th>Action</th>
                            </tr>
                        </thead>
                        <tbody>
                            <%
                                if (stockBatches != null && !stockBatches.isEmpty()) {
                                    for (StockBatch batch : stockBatches) {
                                        if (batch.getQuantityRemaining() > 0) {
                            %>
                                <tr>
                                    <td><%= batch.getId() %></td>
                                    <td><%= batch.getProductCode() %></td>
                                    <td><%= batch.getPurchaseDate() %></td>
                                    <td><%= batch.getExpiryDate() %></td>
                                    <td><%= batch.getQuantityRemaining() %></td>
                                    <td><span class="badge bg-success">Available</span></td>
                                    <td>
                                        <button class="btn btn-sm btn-outline-primary select-batch-btn"
                                                data-batch-id="<%= batch.getId() %>"
                                                data-max-quantity="<%= batch.getQuantityRemaining() %>">
                                            <i class="fas fa-hand-pointer"></i> Select
                                        </button>
                                    </td>
                                </tr>
                            <%
                                        }
                                    }
                                } else {
                            %>
                                <tr>
                                    <td colspan="7" class="text-center py-4">
                                        <i class="fas fa-box-open fa-2x text-muted mb-2"></i>
                                        <p class="text-muted mb-0">No available stock batches</p>
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
// Handle batch selection
document.addEventListener('click', function(e) {
    if (e.target.classList.contains('select-batch-btn') || e.target.closest('.select-batch-btn')) {
        const button = e.target.classList.contains('select-batch-btn') ? e.target : e.target.closest('.select-batch-btn');
        const batchId = button.getAttribute('data-batch-id');
        const maxQuantity = button.getAttribute('data-max-quantity');

        // Set the batch ID in the form
        document.getElementById('batchId').value = batchId;

        // Set max quantity for validation
        document.getElementById('quantity').max = maxQuantity;

        // Scroll to form
        document.querySelector('.card').scrollIntoView({ behavior: 'smooth' });
    }
});

// Update quantity max when batch changes
document.getElementById('batchId').addEventListener('change', function() {
    const selectedOption = this.options[this.selectedIndex];
    if (selectedOption.value) {
        const maxQuantity = selectedOption.text.match(/Available: (\d+)/)?.[1];
        if (maxQuantity) {
            document.getElementById('quantity').max = maxQuantity;
        }
    }
});
</script>

<%@ include file="/WEB-INF/jsp/common/footer.jsp" %>