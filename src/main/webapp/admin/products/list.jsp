<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="com.syos.domain.model.Product" %>
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

    request.setAttribute("pageTitle", "Product Management");
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
            <h2>Product Management</h2>
            <a href="<%= request.getContextPath() %>/admin/products/add.jsp" class="btn btn-primary">
                <i class="fas fa-plus me-2"></i>Add New Product
            </a>
        </div>


        <div class="card">
            <div class="card-header">
                <div class="d-flex justify-content-between align-items-center">
                    <h5 class="mb-0">All Products</h5>
                    <input type="text" class="form-control form-control-sm" id="searchInput"
                           placeholder="Search products..." style="width: 250px;">
                </div>
            </div>
            <div class="card-body">
                <div class="table-responsive">
                    <table class="table table-striped" id="productsTable">
                        <thead>
                            <tr>
                                <th>Code</th>
                                <th>Name</th>
                                <th>Price</th>
                                <th>Actions</th>
                            </tr>
                        </thead>
                        <tbody>
                            <%
                                // This would normally come from a servlet
                                List<Product> products = (List<Product>) request.getAttribute("products");
                                if (products != null && !products.isEmpty()) {
                                    for (Product product : products) {
                            %>
                                <tr>
                                    <td><%= product.getCode() %></td>
                                    <td><%= product.getName() %></td>
                                    <td>$<%= String.format("%.2f", product.getPrice()) %></td>
                                    <td>
                                        <a href="<%= request.getContextPath() %>/admin/products/edit.jsp?code=<%= product.getCode() %>"
                                           class="btn btn-sm btn-outline-primary me-1">
                                            <i class="fas fa-edit"></i> Edit
                                        </a>
                                        <button class="btn btn-sm btn-outline-danger"
                                                onclick="deleteProduct('<%= product.getCode() %>', '<%= product.getName() %>')">
                                            <i class="fas fa-trash"></i> Delete
                                        </button>
                                    </td>
                                </tr>
                            <%
                                    }
                                } else {
                            %>
                                <tr>
                                    <td colspan="4" class="text-center py-4">
                                        <i class="fas fa-box-open fa-2x text-muted mb-2"></i>
                                        <p class="text-muted mb-0">No products found</p>
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
// Search functionality
document.getElementById('searchInput').addEventListener('input', function() {
    const searchTerm = this.value.toLowerCase();
    const rows = document.querySelectorAll('#productsTable tbody tr');

    rows.forEach(row => {
        const text = row.textContent.toLowerCase();
        if (text.includes(searchTerm)) {
            row.style.display = '';
        } else {
            row.style.display = 'none';
        }
    });
});

function deleteProduct(code, name) {
    if (confirm(`Are you sure you want to delete the product "${name}"?`)) {
        // This would normally submit a form or make an AJAX call
        const form = document.createElement('form');
        form.method = 'POST';
        form.action = '<%= request.getContextPath() %>/admin/products/delete';

        const codeInput = document.createElement('input');
        codeInput.type = 'hidden';
        codeInput.name = 'code';
        codeInput.value = code;

        form.appendChild(codeInput);
        document.body.appendChild(form);
        form.submit();
    }
}
</script>

<%@ include file="/WEB-INF/jsp/common/footer.jsp" %>