<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="com.syos.domain.model.Bill" %>
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

    request.setAttribute("pageTitle", "Bill History - POS");
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
                <a class="nav-link active" href="<%= request.getContextPath() %>/admin/billing/create-bill.jsp">
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
            <h2>Bill History</h2>
            <a href="<%= request.getContextPath() %>/admin/billing/create-bill.jsp" class="btn btn-primary">
                <i class="fas fa-plus me-2"></i>Create New Bill
            </a>
        </div>

        <%-- Error/Success messages are handled in header.jsp --%>

        <div class="card">
            <div class="card-header">
                <h5 class="mb-0">All Bills</h5>
            </div>
            <div class="card-body">
                <div id="billsTable">
                    <div class="text-center py-4">
                        <i class="fas fa-spinner fa-spin fa-2x mb-2"></i>
                        <p>Loading bills...</p>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<script>
document.addEventListener('DOMContentLoaded', function() {
    loadBills();
});

function loadBills() {
    fetch('<%= request.getContextPath() %>/billing/bills')
        .then(response => {
            if (!response.ok) {
                throw new Error('Failed to load bills');
            }
            return response.json();
        })
        .then(bills => {
            displayBills(bills);
        })
        .catch(error => {
            document.getElementById('billsTable').innerHTML = `
                <div class="alert alert-danger">
                    <i class="fas fa-exclamation-circle me-2"></i>
                    Error loading bills: ${error.message}
                </div>
            `;
        });
}

function displayBills(bills) {
    const tableDiv = document.getElementById('billsTable');

    if (bills.length === 0) {
        tableDiv.innerHTML = `
            <div class="text-center py-4 text-muted">
                <i class="fas fa-receipt fa-2x mb-2"></i>
                <p>No bills found</p>
            </div>
        `;
        return;
    }

    let html = `
        <div class="table-responsive">
            <table class="table table-striped">
                <thead>
                    <tr>
                        <th>Serial #</th>
                        <th>Date</th>
                        <th>Total Amount</th>
                        <th>Cash Tendered</th>
                        <th>Change</th>
                        <th>Items</th>
                        <th>Actions</th>
                    </tr>
                </thead>
                <tbody>
    `;

    bills.forEach(bill => {
        const billDate = new Date(bill.billDate).toLocaleString();
        html += `
            <tr>
                <td>${bill.serialNumber}</td>
                <td>${billDate}</td>
                <td>$${bill.totalAmount.toFixed(2)}</td>
                <td>$${bill.cashTendered.toFixed(2)}</td>
                <td>$${bill.changeReturned.toFixed(2)}</td>
                <td>${bill.itemCount} items</td>
                <td>
                    <button class="btn btn-sm btn-outline-primary" onclick="viewBillDetails(${bill.serialNumber})">
                        <i class="fas fa-eye"></i> View
                    </button>
                </td>
            </tr>
        `;
    });

    html += `
                </tbody>
            </table>
        </div>
    `;

    tableDiv.innerHTML = html;
}

function viewBillDetails(serialNumber) {
    fetch('<%= request.getContextPath() %>/billing/bills/' + serialNumber)
        .then(response => {
            if (!response.ok) {
                throw new Error('Bill not found');
            }
            return response.json();
        })
        .then(bill => {
            showBillModal(bill);
        })
        .catch(error => {
            alert('Error loading bill details: ' + error.message);
        });
}

function showBillModal(bill) {
    const billDate = new Date(bill.billDate).toLocaleString();

    let itemsHtml = '';
    if (bill.items && bill.items.length > 0) {
        bill.items.forEach(item => {
            itemsHtml += `
                <tr>
                    <td>${item.productCode}</td>
                    <td>${item.productName}</td>
                    <td>${item.quantity}</td>
                    <td>$${item.totalPrice.toFixed(2)}</td>
                    <td>$${item.discountAmount.toFixed(2)}</td>
                </tr>
            `;
        });
    }

    const modalHtml = `
        <div class="modal fade" id="billModal" tabindex="-1">
            <div class="modal-dialog modal-lg">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title">Bill #${bill.serialNumber}</h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                    </div>
                    <div class="modal-body">
                        <div class="row mb-3">
                            <div class="col-md-6">
                                <strong>Serial Number:</strong> ${bill.serialNumber}
                            </div>
                            <div class="col-md-6">
                                <strong>Date:</strong> ${billDate}
                            </div>
                        </div>
                        <div class="row mb-3">
                            <div class="col-md-4">
                                <strong>Total Amount:</strong> $${bill.totalAmount.toFixed(2)}
                            </div>
                            <div class="col-md-4">
                                <strong>Cash Tendered:</strong> $${bill.cashTendered.toFixed(2)}
                            </div>
                            <div class="col-md-4">
                                <strong>Change:</strong> $${bill.changeReturned.toFixed(2)}
                            </div>
                        </div>

                        <h6>Items:</h6>
                        <div class="table-responsive">
                            <table class="table table-sm">
                                <thead>
                                    <tr>
                                        <th>Code</th>
                                        <th>Name</th>
                                        <th>Qty</th>
                                        <th>Total</th>
                                        <th>Discount</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    ${itemsHtml}
                                </tbody>
                            </table>
                        </div>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Close</button>
                    </div>
                </div>
            </div>
        </div>
    `;

    // Remove existing modal if any
    const existingModal = document.getElementById('billModal');
    if (existingModal) {
        existingModal.remove();
    }

    document.body.insertAdjacentHTML('beforeend', modalHtml);
    const modal = new bootstrap.Modal(document.getElementById('billModal'));
    modal.show();
}
</script>

<%@ include file="/WEB-INF/jsp/common/footer.jsp" %>