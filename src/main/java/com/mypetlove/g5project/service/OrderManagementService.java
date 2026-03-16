package com.mypetlove.g5project.service;

import com.mypetlove.g5project.entity.Order;
import org.springframework.data.domain.Page;

public interface OrderManagementService {
    Page<Order> getOrders(String keyword, String status, int page, int size, String sortBy, String sortDir);
    Order getOrderDetail(Integer id);
}