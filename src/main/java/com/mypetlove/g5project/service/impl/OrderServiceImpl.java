package com.mypetlove.g5project.service.impl;

import com.mypetlove.g5project.dto.DtoRequest.CheckoutRequestDto;
import com.mypetlove.g5project.dto.OrderDetailDto;
import com.mypetlove.g5project.entity.*;
import com.mypetlove.g5project.repository.*;
import com.mypetlove.g5project.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final AccountRepository accountRepository;
    private final ProductRepository productRepository;
    private final PaymentHistoryRepository paymentHistoryRepository;

    private static final BigDecimal SHIPPING_FEE = new BigDecimal("5.99");
    private static final BigDecimal TAX_RATE = new BigDecimal("0.05");

    @Override
    @Transactional
    public Order checkout(CheckoutRequestDto dto, String username) {
        Account account = accountRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Cart cart = cartRepository.findByCustomer_Username(username)
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        List<CartItem> selectedItems = cart.getCartItems().stream()
                .filter(item -> dto.getProductIds().contains(item.getProduct().getId()))
                .collect(Collectors.toList());

        if (selectedItems.isEmpty()) {
            throw new RuntimeException("Khong co san pham nao duoc chon");
        }

        for (CartItem cartItem : selectedItems) {
            Product product = cartItem.getProduct();
            if (product.getStockQuantity() < cartItem.getQuantity()) {
                throw new RuntimeException("San pham '" + product.getName()
                        + "' khong du so luong (con " + product.getStockQuantity() + ")");
            }
        }

        BigDecimal subtotal = selectedItems.stream()
                .map(item -> item.getProduct().getPrice()
                        .multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal tax = subtotal.multiply(TAX_RATE);
        BigDecimal total = subtotal.add(SHIPPING_FEE).add(tax);

        Order order = Order.builder()
                .orderCode("ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                .customer(account)
                .status(Order.OrderStatus.WAITING_PAYMENT)   // ✅ enum
                .totalAmount(total)
                .build();
        orderRepository.save(order);

        for (CartItem cartItem : selectedItems) {
            Product product = cartItem.getProduct();
            int orderedQty = cartItem.getQuantity();

            BigDecimal unitPrice = product.getPrice();
            BigDecimal subTotal = unitPrice.multiply(BigDecimal.valueOf(orderedQty));

            OrderItem orderItem = OrderItem.builder()
                    .order(order)
                    .product(product)
                    .quantity(orderedQty)
                    .unitPrice(unitPrice)
                    .subTotal(subTotal)
                    .build();
            orderItemRepository.save(orderItem);

            product.setStockQuantity(product.getStockQuantity() - orderedQty);
            productRepository.save(product);
        }

        PaymentHistory paymentHistory = PaymentHistory.builder()
                .order(order)
                .amount(total)
                .paymentMethod(dto.getPaymentMethod())
                .paymentStatus(false)
                .paymentDate(null)
                .build();
        paymentHistoryRepository.save(paymentHistory);

        List<Integer> selectedItemIds = selectedItems.stream()
                .map(CartItem::getId)
                .collect(Collectors.toList());
        cartItemRepository.flush();
        cartItemRepository.deleteByIds(selectedItemIds);

        return order;
    }

    @Override
    @Transactional(readOnly = true)
    public OrderDetailDto getOrderDetail(Integer orderId, String username) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (!order.getCustomer().getUsername().equals(username)) {
            throw new RuntimeException("Unauthorized");
        }

        List<OrderDetailDto.OrderItemDto> items = order.getOrderItems().stream()
                .map(item -> OrderDetailDto.OrderItemDto.builder()
                        .productId(item.getProduct().getId())
                        .productName(item.getProduct().getName())
                        .imageUrl(item.getProduct().getImageUrl())
                        .quantity(item.getQuantity())
                        .unitPrice(item.getUnitPrice())
                        .subTotal(item.getSubTotal())
                        .build())
                .collect(Collectors.toList());

        BigDecimal subtotal = items.stream()
                .map(OrderDetailDto.OrderItemDto::getSubTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal tax = subtotal.multiply(TAX_RATE);

        String paymentMethod = "COD";
        Boolean paymentStatus = false;
        if (order.getPaymentHistory() != null) {
            paymentMethod = order.getPaymentHistory().getPaymentMethod();
            paymentStatus = order.getPaymentHistory().getPaymentStatus();
        }

        return OrderDetailDto.builder()
                .orderId(order.getId())
                .orderCode(order.getOrderCode())
                .status(order.getStatus().name())            // ✅ enum → String cho DTO
                .shippingAddress(order.getShippingAddress())
                .paymentMethod(paymentMethod)
                .paymentStatus(paymentStatus)
                .subtotal(subtotal)
                .shipping(SHIPPING_FEE)
                .tax(tax)
                .total(order.getTotalAmount())
                .items(items)
                .build();
    }

    @Override
    @Transactional
    public void confirmCheckout(Integer orderId, String shippingAddress, String username) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (!order.getCustomer().getUsername().equals(username)) {
            throw new RuntimeException("Unauthorized");
        }

        if (order.getStatus() != Order.OrderStatus.WAITING_PAYMENT) {   // ✅ enum
            throw new RuntimeException("Order khong o trang thai cho thanh toan");
        }

        order.setShippingAddress(shippingAddress);

        if (order.getPaymentHistory() != null
                && "COD".equalsIgnoreCase(order.getPaymentHistory().getPaymentMethod())) {
            order.setStatus(Order.OrderStatus.PROCESSING);   // ✅ enum
        } else {
            order.setStatus(Order.OrderStatus.CONFIRMED);    // ✅ enum
        }

        orderRepository.save(order);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Order> getOrderHistory(String username, int page, int size) {
        Account account = accountRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
        return orderRepository.findByCustomer(account, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Order> getOrderHistoryByStatus(String username, String status, int page, int size) {
        Account account = accountRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));

        if (status == null || status.isBlank() || "ALL".equalsIgnoreCase(status)) {
            return orderRepository.findByCustomer(account, pageable);
        }

        // ✅ Map String tab → List<Order.OrderStatus> enum
        List<Order.OrderStatus> dbStatuses = switch (status.toUpperCase()) {
            case "PENDING"    -> List.of(Order.OrderStatus.WAITING_PAYMENT);
            case "PAID"       -> List.of(Order.OrderStatus.PAID);
            case "PROCESSING" -> List.of(Order.OrderStatus.PROCESSING, Order.OrderStatus.CONFIRMED);
            case "SHIPPING"   -> List.of(Order.OrderStatus.SHIPPING);
            case "SUCCESS"    -> List.of(Order.OrderStatus.DELIVERED);
            case "CANCEL"     -> List.of(Order.OrderStatus.CANCELLED);
            default           -> List.of(Order.OrderStatus.WAITING_PAYMENT);
        };

        return orderRepository.findByCustomerAndStatusIn(account, dbStatuses, pageable);
    }

    @Override
    @Transactional
    public void cancelOrder(Integer orderId, String username) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (!order.getCustomer().getUsername().equals(username)) {
            throw new RuntimeException("Unauthorized");
        }

        // ✅ So sánh enum
        if (order.getStatus() != Order.OrderStatus.WAITING_PAYMENT
                && order.getStatus() != Order.OrderStatus.PROCESSING) {
            throw new RuntimeException("Không thể hủy đơn hàng ở trạng thái hiện tại");
        }

        // ✅ Trả lại stock quantity cho các sản phẩm
        for (OrderItem orderItem : order.getOrderItems()) {
            Product product = orderItem.getProduct();
            int returnQuantity = orderItem.getQuantity();
            
            // Cộng lại số lượng đã trừ khi tạo order
            product.setStockQuantity(product.getStockQuantity() + returnQuantity);
            productRepository.save(product);
        }

        order.setStatus(Order.OrderStatus.CANCELLED);        // ✅ enum
        orderRepository.save(order);
    }
}