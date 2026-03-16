function confirmOrder(orderId) {
    var fullName = document.getElementById('fullName').value.trim();
    var streetAddress = document.getElementById('streetAddress').value.trim();
    var city = document.getElementById('city').value.trim();
    var phone = document.getElementById('phone').value.trim();

    // Validate
    if (!fullName || !streetAddress || !city || !phone) {
        showToast('Vui lòng điền đầy đủ thông tin giao hàng', 'error');
        return;
    }

    var btn = document.getElementById('confirm-btn');
    btn.disabled = true;
    btn.innerHTML = '<span class="material-symbols-outlined animate-spin">progress_activity</span> Đang xử lý...';

    var params = new URLSearchParams();
    params.append('fullName', fullName);
    params.append('streetAddress', streetAddress);
    params.append('city', city);
    params.append('phone', phone);

    var csrf = getCsrfToken();
    var headers = { 'Content-Type': 'application/x-www-form-urlencoded' };
    if (csrf) headers[csrf.header] = csrf.token;

    fetch(CONTEXT_PATH + '/orders/' + orderId + '/confirm', {
        method: 'POST',
        headers: headers,
        body: params.toString()
    })
        .then(function(res) { return res.json(); })
        .then(function(data) {
            if (data.status === 'success') {
                showToast('Đặt hàng thành công!', 'success');
                setTimeout(function() {
                    window.location.href = CONTEXT_PATH + '/orders/' + orderId;
                }, 1500);
            } else {
                showToast(data.message || 'Lỗi xác nhận đơn hàng', 'error');
                btn.disabled = false;
                btn.innerHTML = '<span>Xác nhận đơn hàng</span><span class="material-symbols-outlined">shopping_bag</span>';
            }
        })
        .catch(function() {
            showToast('Lỗi kết nối', 'error');
            btn.disabled = false;
            btn.innerHTML = '<span>Xác nhận đơn hàng</span><span class="material-symbols-outlined">shopping_bag</span>';
        });
}