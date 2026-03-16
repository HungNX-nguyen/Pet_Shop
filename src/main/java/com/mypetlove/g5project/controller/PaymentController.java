package com.mypetlove.g5project.controller;

import com.mypetlove.g5project.entity.Order;
import com.mypetlove.g5project.entity.OrderItem;
import com.mypetlove.g5project.entity.PaymentHistory;
import com.mypetlove.g5project.entity.Product;
import com.mypetlove.g5project.repository.OrderRepository;
import com.mypetlove.g5project.repository.PaymentHistoryRepository;
import com.mypetlove.g5project.repository.ProductRepository;
import com.mypetlove.g5project.service.VNPayService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

@Controller
@RequestMapping("/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final VNPayService vnPayService;
    private final OrderRepository orderRepository;
    private final PaymentHistoryRepository paymentHistoryRepository;
    private final ProductRepository productRepository;

    /**
     * User confirm đơn ONLINE → lưu địa chỉ → redirect sang VNPay
     */
    @PostMapping("/vnpay/create/{orderId}")
    public String createVNPayPayment(
            @PathVariable Integer orderId,
            @RequestParam String fullName,
            @RequestParam String streetAddress,
            @RequestParam String city,
            @RequestParam String phone,
            Authentication authentication,
            HttpServletRequest request) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (!order.getCustomer().getUsername().equals(authentication.getName())) {
            return "redirect:/orders";
        }

        // Lưu địa chỉ giao hàng
        String shippingAddress = fullName + " | " + streetAddress + ", " + city + " | " + phone;
        order.setShippingAddress(shippingAddress);
        orderRepository.save(order);

        // Tạo URL và redirect
        String paymentUrl = vnPayService.createPaymentUrl(order, request);
        return "redirect:" + paymentUrl;
    }

    /**
     * VNPay callback sau khi thanh toán xong
     */
    @GetMapping("/vnpay-return")
    public String vnpayReturn(
            @RequestParam Map<String, String> params,
            Model model) {

        boolean isValid     = vnPayService.verifyReturn(params);
        String responseCode = params.get("vnp_ResponseCode");
        Integer orderId     = Integer.parseInt(params.get("vnp_TxnRef"));

        Order order = orderRepository.findById(orderId).orElse(null);
        if (order == null) return "redirect:/orders";

        if (isValid && "00".equals(responseCode)) {
            // ✅ Thanh toán thành công
            order.setStatus(Order.OrderStatus.PAID);
            orderRepository.save(order);

            PaymentHistory ph = order.getPaymentHistory();
            if (ph != null) {
                ph.setPaymentStatus(true);
                ph.setPaymentDate(LocalDateTime.now());
                paymentHistoryRepository.save(ph);
            }

            model.addAttribute("status",        "success");
            model.addAttribute("orderId",        orderId);
            model.addAttribute("orderCode",      order.getOrderCode());
            model.addAttribute("amount",         order.getTotalAmount().longValue());
            model.addAttribute("transactionNo",  params.get("vnp_TransactionNo"));

        } else {
            // ❌ Thanh toán thất bại hoặc bị hủy
            
            // ✅ Trả lại stock quantity cho các sản phẩm
            for (OrderItem orderItem : order.getOrderItems()) {
                Product product = orderItem.getProduct();
                int returnQuantity = orderItem.getQuantity();
                
                // Cộng lại số lượng đã trừ khi tạo order
                product.setStockQuantity(product.getStockQuantity() + returnQuantity);
                productRepository.save(product);
            }
            
            order.setStatus(Order.OrderStatus.CANCELLED);
            orderRepository.save(order);

            model.addAttribute("status",       "failed");
            model.addAttribute("orderId",       orderId);
            model.addAttribute("orderCode",     order.getOrderCode());
            model.addAttribute("responseCode",  responseCode);
        }

        return "order/result";
    }
}