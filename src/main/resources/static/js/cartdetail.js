var SHIPPING_FEE = 5.99;
    var TAX_RATE = 0.05;

    function formatVND(amount) {
    return amount.toLocaleString('vi-VN') + ' VNĐ';
}

    function recalculateTotal() {
    var checkboxes = document.querySelectorAll('.cart-item-checkbox:checked');
    var subtotal = 0;

    checkboxes.forEach(function(cb) {
    subtotal += parseFloat(cb.getAttribute('data-price')) || 0;
});

    var shipping = subtotal > 0 ? SHIPPING_FEE : 0;
    var tax = subtotal * TAX_RATE;
    var total = subtotal + shipping + tax;

    document.getElementById('summary-subtotal').textContent = formatVND(subtotal);
    document.getElementById('summary-shipping').textContent = formatVND(shipping);
    document.getElementById('summary-tax').textContent = formatVND(tax);
    document.getElementById('summary-total').textContent = formatVND(total);

    // Enable/disable checkout button
    var checkoutBtn = document.getElementById('checkout-btn');
    var warning = document.getElementById('checkout-warning');
    if (checkboxes.length > 0) {
    checkoutBtn.disabled = false;
    warning.classList.add('hidden');
} else {
    checkoutBtn.disabled = true;
    warning.classList.remove('hidden');
}

    // Sync select-all checkbox
    var allCheckboxes = document.querySelectorAll('.cart-item-checkbox');
    var selectAll = document.getElementById('select-all');
    if (selectAll) {
    selectAll.checked = checkboxes.length === allCheckboxes.length;
    selectAll.indeterminate = checkboxes.length > 0 && checkboxes.length < allCheckboxes.length;
}
}

    function toggleSelectAll(checked) {
    document.querySelectorAll('.cart-item-checkbox').forEach(function(cb) {
        cb.checked = checked;
    });
    recalculateTotal();
}

    function proceedCheckout() {
    var selectedIds = [];
    document.querySelectorAll('.cart-item-checkbox:checked').forEach(function(cb) {
    selectedIds.push(parseInt(cb.value));
});

    if (selectedIds.length === 0) {
    document.getElementById('checkout-warning').classList.remove('hidden');
    return;
}

    var paymentMethod = document.querySelector('input[name="payment-method"]:checked').value;

    var checkoutBtn = document.getElementById('checkout-btn');
    checkoutBtn.disabled = true;
    checkoutBtn.innerHTML = '<span class="material-symbols-outlined animate-spin">progress_activity</span> Đang xử lý...';

    fetch(CONTEXT_PATH + '/api/orders/checkout', {
    method: 'POST',
    headers: buildHeaders(true),
    body: JSON.stringify({
    productIds: selectedIds,
    paymentMethod: paymentMethod
})
})
    .then(function(res) { return res.json(); })
    .then(function(data) {
    if (data.status === 'success') {
    showToast('Đặt hàng thành công! Mã đơn: ' + data.orderCode, 'success');
    setTimeout(function() {
    window.location.href = CONTEXT_PATH + '/orders/' + data.orderId + '/checkout';
}, 1500);
} else {
    showToast(data.message || 'Lỗi khi đặt hàng', 'error');
    checkoutBtn.disabled = false;
    checkoutBtn.innerHTML = 'Proceed to Checkout <span class="material-symbols-outlined">shopping_cart_checkout</span>';
}
})
    .catch(function() {
    showToast('Lỗi kết nối', 'error');
    checkoutBtn.disabled = false;
    checkoutBtn.innerHTML = 'Proceed to Checkout <span class="material-symbols-outlined">shopping_cart_checkout</span>';
});
}
