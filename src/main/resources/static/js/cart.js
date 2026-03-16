// ===== Context Path (lấy động từ meta tag, không hardcode) =====
var CONTEXT_PATH = document.querySelector('meta[name="context-path"]')?.content?.replace(/\/$/, '') || '';

// ===== Auth Helpers =====

function isAuthenticated() {
    return document.getElementById('auth-status') !== null;
}

// ===== Cookie Helpers =====

function getCartFromCookie() {
    var match = document.cookie.match(/(?:^|;\s*)pet_cart=([^;]*)/);
    if (!match) return [];
    try {
        return JSON.parse(decodeURIComponent(match[1]));
    } catch (e) {
        return [];
    }
}

function saveCartToCookie(cart) {
    var json = JSON.stringify(cart);
    document.cookie = 'pet_cart=' + encodeURIComponent(json) + ';path=/;max-age=2592000;SameSite=Lax';
}

// ===== CSRF Helper =====

function getCsrfToken() {
    var meta = document.querySelector('meta[name="_csrf"]');
    var header = document.querySelector('meta[name="_csrf_header"]');
    if (meta && header) {
        return { header: header.content, token: meta.content };
    }
    return null;
}

function buildHeaders(isJson) {
    var headers = {};
    if (isJson) headers['Content-Type'] = 'application/json';
    var csrf = getCsrfToken();
    if (csrf) headers[csrf.header] = csrf.token;
    return headers;
}

// ===== Badge =====

function updateCartBadge() {
    var badge = document.getElementById('cart-badge');
    if (!badge) return;

    if (isAuthenticated()) {
        fetch(CONTEXT_PATH + '/api/cart/count', {
            method: 'GET',
            headers: buildHeaders(false)
        })
            .then(function(res) { return res.json(); })
            .then(function(data) {
                var total = data.totalItems || 0;
                badge.textContent = total;
                if (total > 0) {
                    badge.classList.remove('hidden');
                } else {
                    badge.classList.add('hidden');
                }
            })
            .catch(function() {});
    } else {
        var cart = getCartFromCookie();
        var total = 0;
        for (var i = 0; i < cart.length; i++) {
            total += (cart[i].quantity || 0);
        }
        badge.textContent = total;
        if (total > 0) {
            badge.classList.remove('hidden');
        } else {
            badge.classList.add('hidden');
        }
    }
}

// ===== Toast Notification =====

function showToast(message, type) {
    type = type || 'success';
    var toastContainer = document.getElementById('toast-container');
    if (!toastContainer) {
        toastContainer = document.createElement('div');
        toastContainer.id = 'toast-container';
        toastContainer.className = 'fixed bottom-4 right-4 z-50 flex flex-col gap-2';
        document.body.appendChild(toastContainer);
    }

    var toast = document.createElement('div');
    var bgColor = type === 'success' ? 'bg-green-500' : 'bg-red-500';
    toast.className = bgColor + ' text-white px-6 py-3 rounded-lg shadow-lg transform transition-all duration-300 translate-y-10 opacity-0 flex items-center gap-2';

    var icon = type === 'success' ? 'check_circle' : 'error';
    toast.innerHTML = '<span class="material-symbols-outlined">' + icon + '</span>' +
        '<span class="font-semibold">' + message + '</span>';

    toastContainer.appendChild(toast);

    requestAnimationFrame(function() {
        toast.classList.remove('translate-y-10', 'opacity-0');
        toast.classList.add('translate-y-0', 'opacity-100');
    });

    setTimeout(function() {
        toast.classList.remove('translate-y-0', 'opacity-100');
        toast.classList.add('translate-y-10', 'opacity-0');
        setTimeout(function() { toast.remove(); }, 300);
    }, 3000);
}

// ===== Cart Operations =====

function addToCart(productId, quantity) {
    quantity = parseInt(quantity) || 1;
    productId = parseInt(productId);

    if (!isAuthenticated()) {
        var cart = getCartFromCookie();
        var found = false;
        for (var i = 0; i < cart.length; i++) {
            if (cart[i].productId === productId) {
                cart[i].quantity += quantity;
                found = true;
                break;
            }
        }
        if (!found) {
            cart.push({ productId: productId, quantity: quantity });
        }
        saveCartToCookie(cart);
        updateCartBadge();
        showToast('Đã thêm vào giỏ hàng', 'success');
        // ✅ Không gọi API thừa với guest nữa
    } else {
        fetch(CONTEXT_PATH + '/api/cart/add', {
            method: 'POST',
            headers: buildHeaders(true),
            body: JSON.stringify({ productId: productId, quantity: quantity })
        })
            .then(function(res) { return res.json(); })
            .then(function(data) {
                if (data.status === 'success') {
                    showToast('Đã thêm vào giỏ hàng', 'success');
                    updateCartBadge();
                } else {
                    showToast(data.message || 'Lỗi khi thêm vào giỏ hàng', 'error');
                }
            })
            .catch(function() {
                showToast('Lỗi kết nối', 'error');
            });
    }
}

function updateCartItem(productId, newQuantity) {
    productId = parseInt(productId);
    newQuantity = parseInt(newQuantity);

    if (newQuantity <= 0) {
        removeCartItem(productId);
        return;
    }

    if (!isAuthenticated()) {
        var cart = getCartFromCookie();
        for (var i = 0; i < cart.length; i++) {
            if (cart[i].productId === productId) {
                cart[i].quantity = newQuantity;
                break;
            }
        }
        saveCartToCookie(cart);
        updateCartBadge();
        fetch(CONTEXT_PATH + '/api/cart/update-product?productId=' + productId + '&quantity=' + newQuantity, {
            method: 'PUT',
            headers: buildHeaders(false)
        }).finally(function() {
            window.location.reload();
        });
    } else {
        fetch(CONTEXT_PATH + '/api/cart/update-product?productId=' + productId + '&quantity=' + newQuantity, {
            method: 'PUT',
            headers: buildHeaders(false)
        }).finally(function() {
            window.location.reload();
        });
    }
}

function removeCartItem(productId) {
    productId = parseInt(productId);

    if (!confirm('Bạn có chắc chắn muốn xóa sản phẩm này khỏi giỏ hàng?')) {
        return;
    }

    if (!isAuthenticated()) {
        var cart = getCartFromCookie();
        cart = cart.filter(function(item) { return item.productId !== productId; });
        saveCartToCookie(cart);
        updateCartBadge();

        fetch(CONTEXT_PATH + '/api/cart/remove-product/' + productId, {
            method: 'DELETE',
            headers: buildHeaders(false)
        }).finally(function() {
            window.location.reload();
        });
    } else {
        fetch(CONTEXT_PATH + '/api/cart/remove-product/' + productId, {
            method: 'DELETE',
            headers: buildHeaders(false)
        }).finally(function() {
            window.location.reload();
        });
    }
}

// ===== Init: update badge on every page load =====

document.addEventListener('DOMContentLoaded', function() {
    updateCartBadge();
});