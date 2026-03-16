package com.mypetlove.g5project.service;

import com.mypetlove.g5project.dto.DtoRequest.CheckoutRequestDto;
import com.mypetlove.g5project.dto.OrderDetailDto;
import com.mypetlove.g5project.entity.Order;
import org.springframework.data.domain.Page;

public interface OrderService {
    Order checkout(CheckoutRequestDto dto, String username);
    OrderDetailDto getOrderDetail(Integer orderId, String username);
    void confirmCheckout(Integer orderId, String shippingAddress, String username);
    Page<Order> getOrderHistory(String username, int page, int size);
    Page<Order> getOrderHistoryByStatus(String username, String status, int page, int size);
    void cancelOrder(Integer orderId, String username);
}
