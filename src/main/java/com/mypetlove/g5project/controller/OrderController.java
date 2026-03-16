package com.mypetlove.g5project.controller;

import com.mypetlove.g5project.dto.DtoRequest.CheckoutRequestDto;
import com.mypetlove.g5project.dto.OrderDetailDto;
import com.mypetlove.g5project.entity.Order;
import com.mypetlove.g5project.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Map;

@Controller
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/api/orders/checkout")
    @ResponseBody
    public ResponseEntity<?> checkout(
            @RequestBody CheckoutRequestDto dto,
            Authentication authentication) {

        if (authentication == null || !authentication.isAuthenticated()
                || "anonymousUser".equals(authentication.getName())) {
            return ResponseEntity.status(401)
                    .body(Map.of("status", "error", "message", "Vui long dang nhap de thanh toan"));
        }

        if (dto.getProductIds() == null || dto.getProductIds().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("status", "error", "message", "Vui long chon it nhat mot san pham"));
        }

        try {
            Order order = orderService.checkout(dto, authentication.getName());
            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "message", "Dat hang thanh cong",
                    "orderId", order.getId(),
                    "orderCode", order.getOrderCode()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("status", "error", "message", e.getMessage()));
        }
    }

    @GetMapping("/orders/{orderId}/checkout")
    public String checkoutPage(
            @PathVariable Integer orderId,
            Authentication authentication,
            Model model) {
        try {
            OrderDetailDto orderDetail = orderService.getOrderDetail(orderId, authentication.getName());
            model.addAttribute("order", orderDetail);
            return "order/checkout";
        } catch (Exception e) {
            return "redirect:/cart";
        }
    }

    @PostMapping("/orders/{orderId}/confirm")
    @ResponseBody
    public ResponseEntity<?> confirmCheckout(
            @PathVariable Integer orderId,
            @RequestParam String fullName,
            @RequestParam String streetAddress,
            @RequestParam String city,
            @RequestParam String phone,
            Authentication authentication) {
        try {
            String shippingAddress = fullName + " | " + streetAddress + ", " + city + " | " + phone;
            orderService.confirmCheckout(orderId, shippingAddress, authentication.getName());
            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "message", "Xac nhan don hang thanh cong",
                    "orderId", orderId
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("status", "error", "message", e.getMessage()));
        }
    }

    @GetMapping("/orders")
    public String orderHistory(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "ALL") String status,
            Authentication authentication,
            Model model) {

        if (authentication == null || !authentication.isAuthenticated()
                || "anonymousUser".equals(authentication.getName())) {
            return "redirect:/login";
        }

        try {
            var orderPage = orderService.getOrderHistoryByStatus(authentication.getName(), status, page, size);
            model.addAttribute("orders", orderPage.getContent());
            model.addAttribute("currentPage", orderPage.getNumber());
            model.addAttribute("totalPages", orderPage.getTotalPages());
            model.addAttribute("totalItems", orderPage.getTotalElements());
            model.addAttribute("currentStatus", status.toUpperCase());
            return "order/orderhistory";
        } catch (Exception e) {
            model.addAttribute("orders", java.util.Collections.emptyList());
            model.addAttribute("currentPage", 0);
            model.addAttribute("totalPages", 0);
            model.addAttribute("totalItems", 0);
            model.addAttribute("currentStatus", status.toUpperCase());
            return "order/orderhistory";
        }
    }

    @PostMapping("/orders/{orderId}/cancel")
    public String cancelOrder(
            @PathVariable Integer orderId,
            @RequestParam(defaultValue = "ALL") String status,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        if (authentication == null || !authentication.isAuthenticated()
                || "anonymousUser".equals(authentication.getName())) {
            return "redirect:/login";
        }

        try {
            orderService.cancelOrder(orderId, authentication.getName());
            redirectAttributes.addFlashAttribute("successMessage", "Đơn hàng đã được hủy thành công.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/orders?status=" + status;
    }

    @GetMapping("/orders/{orderId}")
    public String orderDetail(
            @PathVariable Integer orderId,
            Authentication authentication,
            Model model) {
        try {
            OrderDetailDto orderDetail = orderService.getOrderDetail(orderId, authentication.getName());
            model.addAttribute("order", orderDetail);
            return "order/orderdetail";
        } catch (Exception e) {
            return "redirect:/orders";
        }
    }
}