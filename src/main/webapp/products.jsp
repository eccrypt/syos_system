<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="com.syos.domain.model.Product" %>
<%@ page import="com.syos.domain.model.Discount" %>
<%
    request.setAttribute("pageTitle", "Products");
%>
<%@ include file="/WEB-INF/jsp/common/header.jsp" %>

<div class="row">
    <div class="col-12">
        <div class="d-flex justify-content-between align-items-center mb-4">
            <h2>Our Products</h2>
            <div class="d-flex gap-2">
                <input type="text" class="form-control" id="searchInput" placeholder="Search products...">
                <select class="form-select" id="categoryFilter">
                    <option value="">All Categories</option>
                    <!-- Categories would be populated dynamically -->
                </select>
            </div>
        </div>

        <%-- Error/Success messages --%>
        <% String error = (String) request.getAttribute("error"); %>
        <% if (error != null) { %>
            <div class="alert alert-danger" role="alert">
                <%= error %>
            </div>
        <% } %>

        <div class="row" id="productsContainer">
            <%
                // This would normally come from a servlet, but for now we'll show a placeholder
                List<Product> products = (List<Product>) request.getAttribute("products");
                if (products != null && !products.isEmpty()) {
                    for (Product product : products) {
            %>
                <div class="col-md-6 col-lg-4 mb-4">
                    <div class="card product-card h-100">
                        <%-- Check if product has discount --%>
                        <% Discount discount = (Discount) product.getDiscount(); %>
                        <% if (discount != null) { %>
                            <div class="discount-badge">
                                <%= discount.getDiscountType() == com.syos.domain.enums.DiscountType.PERCENT ?
                                    discount.getValue() + "% OFF" : "$" + discount.getValue() + " OFF" %>
                            </div>
                        <% } %>

                        <div class="card-body d-flex flex-column">
                            <h5 class="card-title"><%= product.getName() %></h5>
                            <p class="card-text text-muted small">Code: <%= product.getCode() %></p>

                            <div class="mt-auto">
                                <% if (discount != null) { %>
                                    <div class="mb-2">
                                        <span class="original-price">$<%= String.format("%.2f", product.getPrice()) %></span>
                                        <span class="price ms-2">$<%= String.format("%.2f", product.getDiscountedPrice()) %></span>
                                    </div>
                                <% } else { %>
                                    <div class="price mb-2">$<%= String.format("%.2f", product.getPrice()) %></div>
                                <% } %>

                                <div class="d-flex gap-2">
                                    <input type="number" class="form-control form-control-sm" value="1" min="1"
                                           id="quantity-<%= product.getCode() %>" style="width: 80px;">
                                    <button class="btn btn-primary btn-sm flex-grow-1 add-to-cart-btn"
                                            data-product-code="<%= product.getCode() %>"
                                            data-product-name="<%= product.getName().replaceAll("\"", """) %>"
                                            data-price="<%= product.getDiscountedPrice() %>">
                                        Add to Cart
                                    </button>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            <%
                    }
                } else {
            %>
                <div class="col-12">
                    <div class="text-center py-5">
                        <div class="mb-3">
                            <i class="fas fa-box-open fa-3x text-muted"></i>
                        </div>
                        <h4 class="text-muted">No products available</h4>
                        <p class="text-muted">Please check back later or contact support.</p>
                    </div>
                </div>
            <% } %>
        </div>

        <%-- Sample products for demonstration (remove when real data is available) --%>
        <script>
            // This would normally be loaded via AJAX or server-side rendering
            if (document.querySelectorAll('#productsContainer .col-md-6').length === 0) {
                const sampleProducts = [
                    { code: 'MILK001', name: 'Fresh Milk 1L', price: 2.50, discountedPrice: 2.50 },
                    { code: 'BREAD001', name: 'Whole Wheat Bread', price: 3.00, discountedPrice: 2.50 },
                    { code: 'EGGS001', name: 'Farm Eggs (12)', price: 4.50, discountedPrice: 4.50 },
                    { code: 'BANANA001', name: 'Bananas (kg)', price: 1.20, discountedPrice: 1.20 },
                    { code: 'CHEESE001', name: 'Cheddar Cheese 200g', price: 5.50, discountedPrice: 4.95 },
                    { code: 'TOMATO001', name: 'Tomatoes (kg)', price: 2.80, discountedPrice: 2.80 }
                ];

                let html = '';
                sampleProducts.forEach(product => {
                    html += `
                        <div class="col-md-6 col-lg-4 mb-4">
                            <div class="card product-card h-100">
                                <div class="card-body d-flex flex-column">
                                    <h5 class="card-title">${product.name}</h5>
                                    <p class="card-text text-muted small">Code: ${product.code}</p>
                                    <div class="mt-auto">
                                        <div class="price mb-2">$${product.discountedPrice.toFixed(2)}</div>
                                        <div class="d-flex gap-2">
                                            <input type="number" class="form-control form-control-sm" value="1" min="1"
                                                   id="quantity-${product.code}" style="width: 80px;">
                                            <button class="btn btn-primary btn-sm flex-grow-1"
                                                    onclick="addToCart('${product.code}', '${product.name}', ${product.discountedPrice}, parseInt(document.getElementById('quantity-${product.code}').value))">
                                                Add to Cart
                                            </button>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    `;
                });
                document.getElementById('productsContainer').innerHTML = html;
            }
        </script>
    </div>
</div>

<script>
// Add to cart functionality
document.addEventListener('click', function(e) {
    if (e.target.classList.contains('add-to-cart-btn')) {
        const button = e.target;
        const productCode = button.getAttribute('data-product-code');
        const productName = button.getAttribute('data-product-name');
        const price = parseFloat(button.getAttribute('data-price'));
        const quantityInput = document.getElementById('quantity-' + productCode);
        const quantity = parseInt(quantityInput.value);

        addToCart(productCode, productName, price, quantity);
    }
});

// Search functionality
document.getElementById('searchInput').addEventListener('input', function() {
    const searchTerm = this.value.toLowerCase();
    const productCards = document.querySelectorAll('.product-card');

    productCards.forEach(card => {
        const productName = card.querySelector('.card-title').textContent.toLowerCase();
        const productCode = card.querySelector('.card-text').textContent.toLowerCase();

        if (productName.includes(searchTerm) || productCode.includes(searchTerm)) {
            card.style.display = 'block';
        } else {
            card.style.display = 'none';
        }
    });
});
</script>

<%@ include file="/WEB-INF/jsp/common/footer.jsp" %>