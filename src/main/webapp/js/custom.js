// SYOS Custom JavaScript

$(document).ready(function() {
    // Auto-hide alerts after 5 seconds
    setTimeout(function() {
        $('.alert').fadeOut('slow');
    }, 5000);

    // Form validation
    $('form').on('submit', function(e) {
        const form = $(this);
        let isValid = true;

        // Check required fields
        form.find('[required]').each(function() {
            if ($(this).val().trim() === '') {
                $(this).addClass('is-invalid');
                isValid = false;
            } else {
                $(this).removeClass('is-invalid');
            }
        });

        // Email validation
        form.find('input[type="email"]').each(function() {
            const email = $(this).val();
            const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
            if (email && !emailRegex.test(email)) {
                $(this).addClass('is-invalid');
                isValid = false;
            }
        });

        if (!isValid) {
            e.preventDefault();
            showError('Please fill in all required fields correctly.');
        }
    });

    // Remove validation styling on input
    $('input, select, textarea').on('input change', function() {
        $(this).removeClass('is-invalid');
    });
});

// Utility functions
function showError(message) {
    const alertHtml = `
        <div class="alert alert-danger alert-dismissible fade show" role="alert">
            ${message}
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        </div>
    `;
    $('.container-fluid').prepend(alertHtml);
}

function showSuccess(message) {
    const alertHtml = `
        <div class="alert alert-success alert-dismissible fade show" role="alert">
            ${message}
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        </div>
    `;
    $('.container-fluid').prepend(alertHtml);
}

function showLoading(button) {
    const originalText = button.html();
    button.prop('disabled', true).html('<span class="loading me-2"></span>Processing...');
    return originalText;
}

function hideLoading(button, originalText) {
    button.prop('disabled', false).html(originalText);
}

function apiCall(url, method, data, successCallback, errorCallback) {
    $.ajax({
        url: url,
        method: method,
        contentType: 'application/json',
        data: JSON.stringify(data),
        success: function(response) {
            if (successCallback) successCallback(response);
        },
        error: function(xhr) {
            let errorMessage = 'An error occurred';
            try {
                const errorData = JSON.parse(xhr.responseText);
                errorMessage = errorData.error || errorMessage;
            } catch (e) {
                // Use default message
            }
            if (errorCallback) {
                errorCallback(errorMessage);
            } else {
                showError(errorMessage);
            }
        }
    });
}


let cart = [];

function addToCart(productCode, productName, price, quantity = 1) {
    const existingItem = cart.find(item => item.productCode === productCode);
    if (existingItem) {
        existingItem.quantity += quantity;
    } else {
        cart.push({
            productCode: productCode,
            productName: productName,
            price: price,
            quantity: quantity
        });
    }
    updateCartDisplay();
    showSuccess(`${productName} added to cart`);
}

function removeFromCart(productCode) {
    cart = cart.filter(item => item.productCode !== productCode);
    updateCartDisplay();
}

function updateCartDisplay() {
    const cartCount = cart.reduce((total, item) => total + item.quantity, 0);
    $('.cart-count').text(cartCount);

    // Update cart items if cart page is displayed
    if ($('#cart-items').length) {
        displayCartItems();
    }
}

function displayCartItems() {
    const cartItemsContainer = $('#cart-items');
    const cartTotalContainer = $('#cart-total');

    if (cart.length === 0) {
        cartItemsContainer.html('<p class="text-muted">Your cart is empty</p>');
        cartTotalContainer.html('');
        return;
    }

    let html = '';
    let total = 0;

    cart.forEach(item => {
        const itemTotal = item.price * item.quantity;
        total += itemTotal;
        html += `
            <div class="cart-item d-flex justify-content-between align-items-center">
                <div>
                    <h6 class="mb-0">${item.productName}</h6>
                    <small class="text-muted">$${item.price.toFixed(2)} x ${item.quantity}</small>
                </div>
                <div class="d-flex align-items-center">
                    <span class="me-3">$${itemTotal.toFixed(2)}</span>
                    <button class="btn btn-sm btn-outline-danger" onclick="removeFromCart('${item.productCode}')">
                        Remove
                    </button>
                </div>
            </div>
        `;
    });

    cartItemsContainer.html(html);
    cartTotalContainer.html(`<h4 class="cart-total">Total: $${total.toFixed(2)}</h4>`);
}

// Load cart from localStorage on page load
$(document).ready(function() {
    const savedCart = localStorage.getItem('syos-cart');
    if (savedCart) {
        cart = JSON.parse(savedCart);
        updateCartDisplay();
    }

    // Save cart to localStorage when it changes
    $(window).on('beforeunload', function() {
        localStorage.setItem('syos-cart', JSON.stringify(cart));
    });
});