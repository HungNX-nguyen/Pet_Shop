function confirmOrder(orderId) {
    console.log('=== confirmOrder called ===');
    console.log('orderId parameter:', orderId, 'type:', typeof orderId);
    
    // ✅ Fallback: đọc thẳng từ DOM nếu tham số bị lỗi
    if (!orderId || isNaN(parseInt(orderId))) {
        console.log('orderId invalid, trying fallback from button dataset');
        var btn = document.getElementById('confirm-btn');
        if (btn) {
            console.log('Button dataset.orderId:', btn.dataset.orderId);
            orderId = btn ? parseInt(btn.dataset.orderId) : null;
        }
    }
    orderId = parseInt(orderId);

    console.log('Final orderId:', orderId);

    if (!orderId || isNaN(orderId)) {
        showToast('Lỗi: Không tìm thấy mã đơn hàng', 'error');
        console.error('orderId is null or NaN');
        return;
    }

    var fullName      = document.getElementById('fullName').value.trim();
    var streetAddress = document.getElementById('streetAddress').value.trim();
    var city          = document.getElementById('city').value.trim();
    var phone         = document.getElementById('phone').value.trim();

    if (!fullName || !streetAddress || !city || !phone) {
        showToast('Vui lòng điền đầy đủ thông tin giao hàng', 'error');
        return;
    }

    var btn = document.getElementById('confirm-btn');
    btn.disabled = true;
    btn.innerHTML = '<span class="material-symbols-outlined animate-spin">progress_activity</span> Đang xử lý...';

    var paymentMethodEl = document.getElementById('payment-method-data');
    var paymentMethod   = paymentMethodEl ? paymentMethodEl.dataset.method : 'COD';

    console.log('orderId:', orderId);
    console.log('paymentMethod:', paymentMethod);
    console.log('CONTEXT_PATH:', CONTEXT_PATH);

    if (paymentMethod === 'ONLINE') {
        var form = document.createElement('form');
        form.method = 'POST';
        form.action = CONTEXT_PATH + '/payment/vnpay/create/' + orderId;

        console.log('Form action:', form.action);

        var fields = {
            fullName:      fullName,
            streetAddress: streetAddress,
            city:          city,
            phone:         phone
        };

        Object.keys(fields).forEach(function(name) {
            var input   = document.createElement('input');
            input.type  = 'hidden';
            input.name  = name;
            input.value = fields[name];
            form.appendChild(input);
        });

        document.body.appendChild(form);
        form.submit();

    } else {
        var params = new URLSearchParams();
        params.append('fullName',      fullName);
        params.append('streetAddress', streetAddress);
        params.append('city',          city);
        params.append('phone',         phone);

        var csrf    = getCsrfToken();
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
}