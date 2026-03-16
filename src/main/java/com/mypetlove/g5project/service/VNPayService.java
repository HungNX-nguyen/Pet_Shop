package com.mypetlove.g5project.service;

import com.mypetlove.g5project.config.VNPayConfig;
import com.mypetlove.g5project.entity.Order;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class VNPayService {

    private final VNPayConfig vnPayConfig;
    private final VNPayUtils vnPayUtils;

    private static final DateTimeFormatter FMT =
            DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    public String createPaymentUrl(Order order, HttpServletRequest request) {
        LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh"));

        // VNPay yêu cầu amount * 100
        long amount = order.getTotalAmount()
                .multiply(BigDecimal.valueOf(100))
                .longValue();

        Map<String, String> params = new HashMap<>();
        params.put("vnp_Version",    vnPayConfig.getApiVersion());
        params.put("vnp_Command",    "pay");
        params.put("vnp_TmnCode",    vnPayConfig.getTmnCode());
        params.put("vnp_Amount",     String.valueOf(amount));
        params.put("vnp_CurrCode",   "VND");
        params.put("vnp_TxnRef",     order.getId().toString());
        params.put("vnp_OrderInfo",  "Thanh toan don hang " + order.getOrderCode());
        params.put("vnp_OrderType",  "other");
        params.put("vnp_Locale",     "vn");
        params.put("vnp_ReturnUrl",  vnPayConfig.getReturnUrl());
        params.put("vnp_IpAddr",     vnPayUtils.getIpAddress(request));
        params.put("vnp_CreateDate", now.format(FMT));
        params.put("vnp_ExpireDate", now.plusMinutes(15).format(FMT));

        String[] built       = vnPayUtils.buildSortedParams(params);
        String hashData      = built[0];
        String queryString   = built[1];
        String secureHash    = vnPayUtils.hmacSHA512(vnPayConfig.getHashSecret(), hashData);

        return vnPayConfig.getPaymentUrl() + "?" + queryString
                + "&vnp_SecureHash=" + secureHash;
    }

    public boolean verifyReturn(Map<String, String> params) {
        String receivedHash = params.get("vnp_SecureHash");
        if (receivedHash == null) return false;

        Map<String, String> filtered = new HashMap<>(params);
        filtered.remove("vnp_SecureHash");
        filtered.remove("vnp_SecureHashType");

        String[] built        = vnPayUtils.buildSortedParams(filtered);
        String computedHash   = vnPayUtils.hmacSHA512(vnPayConfig.getHashSecret(), built[0]);
        return computedHash.equalsIgnoreCase(receivedHash);
    }
}