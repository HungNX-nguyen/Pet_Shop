package com.mypetlove.g5project.service.impl;

import com.mypetlove.g5project.entity.Order;
import com.mypetlove.g5project.repository.OrderRepository;
import com.mypetlove.g5project.service.OrderManagementService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderManagementServiceImpl implements OrderManagementService {

    private final OrderRepository orderRepository;

    @Override
    public Page<Order> getOrders(String keyword, String status, int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);
        return orderRepository.searchOrders(keyword, status, pageable);
    }

    @Override
    public Order getOrderDetail(Integer id) {
        return orderRepository.findDetailById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng với id: " + id));
    }
}