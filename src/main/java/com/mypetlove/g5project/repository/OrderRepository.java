package com.mypetlove.g5project.repository;

import com.mypetlove.g5project.entity.Account;
import com.mypetlove.g5project.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {
    Page<Order> findByCustomer(Account customer, Pageable pageable);
    Page<Order> findByCustomerAndStatus(Account customer, Order.OrderStatus status, Pageable pageable);
    Page<Order> findByCustomerAndStatusIn(Account customer, List<Order.OrderStatus> statuses, Pageable pageable);
}
