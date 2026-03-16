package com.mypetlove.g5project.service;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Component
public class VNPayUtils {

    public String hmacSHA512(String key, String data) {
        try {
            Mac hmac = Mac.getInstance("HmacSHA512");
            SecretKeySpec secretKey = new SecretKeySpec(
                    key.getBytes(StandardCharsets.UTF_8), "HmacSHA512");
            hmac.init(secretKey);
            byte[] bytes = hmac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException("HMAC-SHA512 error", e);
        }
    }

    public String getIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

    /**
     * Sort params theo alphabet, trả về:
     * [0] = hashData (để ký HMAC)
     * [1] = queryString (để append URL)
     */
    public String[] buildSortedParams(Map<String, String> params) {
        List<String> keys = new ArrayList<>(params.keySet());
        Collections.sort(keys);

        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();

        for (Iterator<String> it = keys.iterator(); it.hasNext(); ) {
            String key = it.next();
            String value = params.get(key);
            if (value != null && !value.isEmpty()) {
                String encodedValue = URLEncoder.encode(value, StandardCharsets.US_ASCII);
                String encodedKey   = URLEncoder.encode(key,   StandardCharsets.US_ASCII);
                hashData.append(key).append('=').append(encodedValue);
                query.append(encodedKey).append('=').append(encodedValue);
                if (it.hasNext()) {
                    hashData.append('&');
                    query.append('&');
                }
            }
        }
        return new String[]{hashData.toString(), query.toString()};
    }
}