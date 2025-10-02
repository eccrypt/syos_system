<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.syos.domain.model.User" %>
<%
    // Check authentication
    User user = (User) session.getAttribute("user");
    if (user == null) {
        response.sendRedirect(request.getContextPath() + "/login.jsp");
        return;
    }

    String userRole = (String) session.getAttribute("userRole");
    if ("ADMIN".equals(userRole) || "STAFF".equals(userRole)) {
        response.sendRedirect(request.getContextPath() + "/admin/dashboard.jsp");
        return;
    }

    request.setAttribute("pageTitle", "Shopping Cart");
%>
<%@ include file="/WEB-INF/jsp/common/header.jsp" %>

<div class="row">
    <div class="col-12">
        <h2 class="mb-4">Shopping Cart</h2>

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

        <div class="row">
            <div class="col-lg-8">
                <div class="card">
                    <div class="card-header">
                        <h5 class="mb-0">Cart Items</h5>
                    </div>
                    <div class="card-body">
                        <div id="cart-items">
                            <p class="text-muted">Loading cart items...</p>
                        </div>
                    </div>
                </div>
            </div>

            <div class="col-lg-4">
                <div class="card">
                    <div class="card-header">
                        <h5 class="mb-0">Order Summary</h5>
                    </div>
                    <div class="card-body">
                        <div id="cart-total">
                            <p class="text-muted">No items in cart</p>
                        </div>

                        <hr>

                        <form action="<%= request.getContextPath() %>/checkout" method="post" id="checkoutForm">
                            <div class="mb-3">
                                <label for="paymentMethod" class="form-label">Payment Method</label>
                                <select class="form-select" id="paymentMethod" name="paymentMethod" required>
                                    <option value="">Select payment method</option>
                                    <option value="cash">Cash</option>
                                    <option value="card">Card</option>
                                </select>
                            </div>

                            <div class="mb-3" id="cashTenderedDiv" style="display: none;">
                                <label for="cashTendered" class="form-label">Cash Tendered</label>
                                <input type="number" class="form-control" id="cashTendered" name="cashTendered"
                                       step="0.01" min="0" placeholder="0.00">
                            </div>

                            <div class="d-grid gap-2">
                                <button type="submit" class="btn btn-success btn-lg" id="checkoutBtn" disabled>
                                    Proceed to Checkout
                                </button>
                                <a href="<%= request.getContextPath() %>/products.jsp" class="btn btn-outline-primary">
                                    Continue Shopping
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
// Load cart items on page load
document.addEventListener('DOMContentLoaded', function() {
    displayCartItems();

    // Payment method change handler
    document.getElementById('paymentMethod').addEventListener('change', function() {
        const cashDiv = document.getElementById('cashTenderedDiv');
        const cashInput = document.getElementById('cashTendered');
        const checkoutBtn = document.getElementById('checkoutBtn');

        if (this.value === 'cash') {
            cashDiv.style.display = 'block';
            cashInput.required = true;
        } else {
            cashDiv.style.display = 'none';
            cashInput.required = false;
            cashInput.value = '';
        }

        // Enable checkout button if cart has items
        checkoutBtn.disabled = cart.length === 0;
    });

    // Cash tendered validation
    document.getElementById('cashTendered').addEventListener('input', function() {
        const total = cart.reduce((sum, item) => sum + (item.price * item.quantity), 0);
        const tendered = parseFloat(this.value) || 0;

        if (tendered < total) {
            this.setCustomValidity('Cash tendered must be at least the total amount');
        } else {
            this.setCustomValidity('');
        }
    });

    // Form submission
    document.getElementById('checkoutForm').addEventListener('submit', function(e) {
        if (cart.length === 0) {
            e.preventDefault();
            showError('Your cart is empty');
            return;
        }

        const paymentMethod = document.getElementById('paymentMethod').value;
        if (!paymentMethod) {
            e.preventDefault();
            showError('Please select a payment method');
            return;
        }

        // Add cart items to form as hidden inputs
        cart.forEach((item, index) => {
            const productCodeInput = document.createElement('input');
            productCodeInput.type = 'hidden';
            productCodeInput.name = `items[${index}][productCode]`;
            productCodeInput.value = item.productCode;

            const quantityInput = document.createElement('input');
            quantityInput.type = 'hidden';
            quantityInput.name = `items[${index}][quantity]`;
            quantityInput.value = item.quantity;

            this.appendChild(productCodeInput);
            this.appendChild(quantityInput);
        });
    });
});
</script>

<%@ include file="/WEB-INF/jsp/common/footer.jsp" %>