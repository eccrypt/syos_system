<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
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

    request.setAttribute("pageTitle", "Add New Product");
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
                <a class="nav-link active" href="<%= request.getContextPath() %>/admin/products/list.jsp">
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
                <a class="nav-link" href="<%= request.getContextPath() %>/admin/reports/daily-sales.jsp">
                    <i class="fas fa-chart-bar me-2"></i>Reports
                </a>
            </nav>
        </div>
    </div>

    <!-- Main content -->
    <div class="col-md-9 col-lg-10 main-content">
        <div class="d-flex justify-content-between align-items-center mb-4">
            <h2>Add New Product</h2>
            <a href="<%= request.getContextPath() %>/admin/products/list.jsp" class="btn btn-secondary">
                <i class="fas fa-arrow-left me-2"></i>Back to Products
            </a>
        </div>


        <div class="row justify-content-center">
            <div class="col-md-8">
                <div class="card">
                    <div class="card-header">
                        <h5 class="mb-0">Product Information</h5>
                    </div>
                    <div class="card-body">
                        <form action="<%= request.getContextPath() %>/admin/products/add" method="post" id="addProductForm">
                            <div class="mb-3">
                                <label for="code" class="form-label">Product Code <span class="text-danger">*</span></label>
                                <input type="text" class="form-control" id="code" name="code"
                                       placeholder="Enter unique product code" required maxlength="20">
                                <div class="form-text">Unique identifier for the product (e.g., MILK001)</div>
                            </div>

                            <div class="mb-3">
                                <label for="name" class="form-label">Product Name <span class="text-danger">*</span></label>
                                <input type="text" class="form-control" id="name" name="name"
                                       placeholder="Enter product name" required maxlength="100">
                            </div>

                            <div class="mb-3">
                                <label for="price" class="form-label">Price <span class="text-danger">*</span></label>
                                <div class="input-group">
                                    <span class="input-group-text">$</span>
                                    <input type="number" class="form-control" id="price" name="price"
                                           placeholder="0.00" step="0.01" min="0" required>
                                </div>
                                <div class="form-text">Enter the selling price per unit</div>
                            </div>

                            <div class="mb-3">
                                <label for="description" class="form-label">Description</label>
                                <textarea class="form-control" id="description" name="description"
                                          rows="3" placeholder="Optional product description" maxlength="255"></textarea>
                            </div>

                            <div class="row">
                                <div class="col-md-6 mb-3">
                                    <label for="category" class="form-label">Category</label>
                                    <select class="form-select" id="category" name="category">
                                        <option value="">Select category</option>
                                        <option value="dairy">Dairy</option>
                                        <option value="bakery">Bakery</option>
                                        <option value="produce">Produce</option>
                                        <option value="meat">Meat</option>
                                        <option value="beverages">Beverages</option>
                                        <option value="snacks">Snacks</option>
                                        <option value="household">Household</option>
                                        <option value="other">Other</option>
                                    </select>
                                </div>

                                <div class="col-md-6 mb-3">
                                    <label for="unit" class="form-label">Unit</label>
                                    <select class="form-select" id="unit" name="unit">
                                        <option value="piece">Piece</option>
                                        <option value="kg">Kilogram</option>
                                        <option value="liter">Liter</option>
                                        <option value="pack">Pack</option>
                                        <option value="box">Box</option>
                                        <option value="bottle">Bottle</option>
                                    </select>
                                </div>
                            </div>

                            <div class="d-grid gap-2">
                                <button type="submit" class="btn btn-primary btn-lg">
                                    <i class="fas fa-save me-2"></i>Add Product
                                </button>
                                <a href="<%= request.getContextPath() %>/admin/products/list.jsp" class="btn btn-outline-secondary">
                                    Cancel
                                </a>
                            </div>
                        </form>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<script>
// Auto-generate product code suggestion
document.getElementById('name').addEventListener('input', function() {
    const name = this.value.trim();
    if (name.length > 0 && !document.getElementById('code').value) {
        const code = name.substring(0, 3).toUpperCase() + '001';
        document.getElementById('code').value = code;
    }
});

// Form validation
document.getElementById('addProductForm').addEventListener('submit', function(e) {
    const code = document.getElementById('code').value.trim();
    const name = document.getElementById('name').value.trim();
    const price = parseFloat(document.getElementById('price').value);

    if (code.length < 3) {
        e.preventDefault();
        alert('Product code must be at least 3 characters long.');
        document.getElementById('code').focus();
        return;
    }

    if (name.length < 2) {
        e.preventDefault();
        alert('Product name must be at least 2 characters long.');
        document.getElementById('name').focus();
        return;
    }

    if (price <= 0) {
        e.preventDefault();
        alert('Price must be greater than 0.');
        document.getElementById('price').focus();
        return;
    }
});
</script>

<%@ include file="/WEB-INF/jsp/common/footer.jsp" %>