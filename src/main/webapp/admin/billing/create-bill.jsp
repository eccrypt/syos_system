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

    request.setAttribute("pageTitle", "Create Bill - POS");
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
            <h2>Point of Sale</h2>
            <div>
                <button class="btn btn-outline-secondary me-2" onclick="clearBill()">
                    <i class="fas fa-trash me-1"></i>Clear
                </button>
                <a href="<%= request.getContextPath() %>/admin/billing/bills.jsp" class="btn btn-outline-info">
                    <i class="fas fa-list me-1"></i>View Bills
                </a>
            </div>
        </div>

        <%-- Error/Success messages are handled in header.jsp --%>

        <div class="row">
            <!-- Product Selection -->
            <div class="col-md-8">
                <div class="card mb-4">
                    <div class="card-header">
                        <h5 class="mb-0">Add Products</h5>
                    </div>
                    <div class="card-body">

                        <div id="productResults">
                            <div class="text-center py-4">
                                <i class="fas fa-spinner fa-spin fa-2x mb-2"></i>
                                <p>Loading products...</p>
                            </div>
                        </div>
                    </div>
                </div>

                <!-- Current Bill Items -->
                <div class="card">
                    <div class="card-header">
                        <h5 class="mb-0">Current Bill Items</h5>
                    </div>
                    <div class="card-body">
                        <div id="billItems">
                            <div class="text-center py-4 text-muted">
                                <i class="fas fa-shopping-cart fa-2x mb-2"></i>
                                <p>No items added yet</p>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            <!-- Bill Summary & Checkout -->
            <div class="col-md-4">
                <div class="card">
                    <div class="card-header">
                        <h5 class="mb-0">Bill Summary</h5>
                    </div>
                    <div class="card-body">
                        <div id="billSummary">
                            <div class="mb-3">
                                <strong>Total Items:</strong> <span id="totalItems">0</span>
                            </div>
                            <div class="mb-3">
                                <strong>Subtotal:</strong> $<span id="subtotal">0.00</span>
                            </div>
                            <div class="mb-3">
                                <strong>Tax (10%):</strong> $<span id="tax">0.00</span>
                            </div>
                            <hr>
                            <div class="mb-3">
                                <strong class="fs-5">Total:</strong> $<span id="total" class="fs-5">0.00</span>
                            </div>
                        </div>

                        <form id="checkoutForm" style="display: none;">
                            <div class="mb-3">
                                <label for="paymentMethod" class="form-label">Payment Method</label>
                                <select class="form-select" id="paymentMethod" name="paymentMethod" required>
                                    <option value="">Select payment method</option>
                                    <option value="cash">Cash</option>
                                    <option value="card">Card</option>
                                </select>
                            </div>

                            <div class="mb-3" id="cashAmountDiv" style="display: none;">
                                <label for="cashTendered" class="form-label">Cash Tendered</label>
                                <input type="number" class="form-control" id="cashTendered" name="cashTendered"
                                       step="0.01" min="0" placeholder="0.00">
                                <div class="mt-1">
                                    <small>Change: $<span id="changeAmount">0.00</span></small>
                                </div>
                            </div>

                            <button type="submit" class="btn btn-success btn-lg w-100" id="checkoutBtn">
                                <i class="fas fa-credit-card me-2"></i>Complete Sale
                            </button>
                        </form>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<script>
let billItems = [];
let itemCount = 0;

document.addEventListener('DOMContentLoaded', function() {
    loadAllProducts();
});

function formatPrice(price) {
    if (typeof price === 'number' && !isNaN(price)) {
        return price.toFixed(2);
    }
    return '0.00';
}

function formatPriceValue(price) {
    if (typeof price === 'number' && !isNaN(price)) {
        return price;
    }
    return 0;
}

function loadAllProducts() {
    fetch('<%= request.getContextPath() %>/admin/products/search?q=')
        .then(response => {
            if (!response.ok) {
                throw new Error('Failed to load products');
            }
            return response.json();
        })
        .then(products => {
            displayProducts(products);
        })
        .catch(error => {
            document.getElementById('productResults').innerHTML = `
                <div class="alert alert-danger">
                    <i class="fas fa-exclamation-circle me-2"></i>
                    Error loading products: ${error.message}
                </div>
            `;
        });
}

function displayProducts(products) {
    const resultsDiv = document.getElementById('productResults');

    if (products.length === 0) {
        resultsDiv.innerHTML = `
            <div class="text-center py-4 text-muted">
                <i class="fas fa-box-open fa-2x mb-2"></i>
                <p>No products available</p>
            </div>
        `;
        return;
    }

    let html = `
        <div class="table-responsive">
            <table class="table table-striped table-hover">
                <thead class="table-dark">
                    <tr>
                        <th>Code</th>
                        <th>Name</th>
                        <th>Price</th>
                        <th>Action</th>
                    </tr>
                </thead>
                <tbody>
    `;

    products.forEach(product => {
        html += `
            <tr>
                <td><code>${product.code}</code></td>
                <td>${product.name}</td>
                <td>$` + formatPrice(product.price) + `</td>
                <td>
                    <button class="btn btn-primary btn-sm" onclick="addItemToBill('${product.code}', '${product.name}', formatPriceValue(product.price))">
                        <i class="fas fa-plus me-1"></i>Add
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

    resultsDiv.innerHTML = html;
}


// Add item to bill (mock function)
function addItemToBill(productCode, productName, price, quantity = 1) {
    const existingItem = billItems.find(item => item.productCode === productCode);
    if (existingItem) {
        existingItem.quantity += quantity;
    } else {
        billItems.push({
            productCode: productCode,
            productName: productName,
            price: price,
            quantity: quantity
        });
    }
    updateBillDisplay();
}

// Remove item from bill
function removeItem(productCode) {
    billItems = billItems.filter(item => item.productCode !== productCode);
    updateBillDisplay();
}

// Update bill display
function updateBillDisplay() {
    const billItemsDiv = document.getElementById('billItems');
    const checkoutForm = document.getElementById('checkoutForm');

    if (billItems.length === 0) {
        billItemsDiv.innerHTML = `
            <div class="text-center py-4 text-muted">
                <i class="fas fa-shopping-cart fa-2x mb-2"></i>
                <p>No items added yet</p>
            </div>
        `;
        checkoutForm.style.display = 'none';
        return;
    }

    let html = '';
    let subtotal = 0;
    let totalItems = 0;

    billItems.forEach(item => {
        const itemTotal = item.price * item.quantity;
        subtotal += itemTotal;
        totalItems += item.quantity;

        html += `
            <div class="d-flex justify-content-between align-items-center border-bottom py-2">
                <div class="flex-grow-1">
                    <strong>${item.productName}</strong><br>
                    <small class="text-muted">Code: ${item.productCode} | $${item.price.toFixed(2)} x ${item.quantity}</small>
                </div>
                <div class="text-end">
                    <div class="fw-bold">$${itemTotal.toFixed(2)}</div>
                    <button class="btn btn-sm btn-outline-danger mt-1" onclick="removeItem('${item.productCode}')">
                        <i class="fas fa-times"></i>
                    </button>
                </div>
            </div>
        `;
    });

    billItemsDiv.innerHTML = html;

    const tax = subtotal * 0.10;
    const total = subtotal + tax;

    document.getElementById('totalItems').textContent = totalItems;
    document.getElementById('subtotal').textContent = subtotal.toFixed(2);
    document.getElementById('tax').textContent = tax.toFixed(2);
    document.getElementById('total').textContent = total.toFixed(2);

    checkoutForm.style.display = 'block';
}

// Clear bill
function clearBill() {
    if (billItems.length > 0 && confirm('Are you sure you want to clear the current bill?')) {
        billItems = [];
        updateBillDisplay();
    }
}

// Payment method change
document.getElementById('paymentMethod').addEventListener('change', function() {
    const cashDiv = document.getElementById('cashAmountDiv');
    const cashInput = document.getElementById('cashTendered');

    if (this.value === 'cash') {
        cashDiv.style.display = 'block';
        cashInput.required = true;
    } else {
        cashDiv.style.display = 'none';
        cashInput.required = false;
        cashInput.value = '';
        document.getElementById('changeAmount').textContent = '0.00';
    }
});

// Cash tendered calculation
document.getElementById('cashTendered').addEventListener('input', function() {
    const tendered = parseFloat(this.value) || 0;
    const total = parseFloat(document.getElementById('total').textContent) || 0;
    const change = Math.max(0, tendered - total);
    document.getElementById('changeAmount').textContent = change.toFixed(2);
});

// Form submission
document.getElementById('checkoutForm').addEventListener('submit', function(e) {
    e.preventDefault();

    if (billItems.length === 0) {
        alert('No items in the bill');
        return;
    }

    const paymentMethod = document.getElementById('paymentMethod').value;
    if (!paymentMethod) {
        alert('Please select a payment method');
        return;
    }

    let cashTendered = 0;
    if (paymentMethod === 'cash') {
        cashTendered = parseFloat(document.getElementById('cashTendered').value) || 0;
        const total = parseFloat(document.getElementById('total').textContent) || 0;

        if (cashTendered < total) {
            alert('Cash tendered is less than the total amount');
            return;
        }
    } else {
        // For card payment, assume full amount is tendered
        cashTendered = parseFloat(document.getElementById('total').textContent) || 0;
    }

    const checkoutBtn = document.getElementById('checkoutBtn');
    const originalText = checkoutBtn.innerHTML;
    checkoutBtn.innerHTML = '<i class="fas fa-spinner fa-spin me-2"></i>Processing...';
    checkoutBtn.disabled = true;

    const billRequest = {
        items: billItems.map(item => ({
            productCode: item.productCode,
            quantity: item.quantity
        })),
        cashTendered: cashTendered
    };

    fetch('<%= request.getContextPath() %>/billing/create', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(billRequest)
    })
    .then(response => {
        if (!response.ok) {
            return response.json().then(err => { throw new Error(err.error || 'Failed to create bill'); });
        }
        return response.json();
    })
    .then(data => {
        alert(`Bill created successfully!\nSerial Number: ${data.serialNumber}\nTotal: $${data.totalAmount.toFixed(2)}\nCash Tendered: $${data.cashTendered.toFixed(2)}\nChange: $${data.changeReturned.toFixed(2)}`);
        clearBill();
    })
    .catch(error => {
        alert('Error creating bill: ' + error.message);
    })
    .finally(() => {
        checkoutBtn.innerHTML = originalText;
        checkoutBtn.disabled = false;
    });
});
</script>

<%@ include file="/WEB-INF/jsp/common/footer.jsp" %>