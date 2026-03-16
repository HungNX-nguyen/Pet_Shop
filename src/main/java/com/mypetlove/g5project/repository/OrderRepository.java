package com.mypetlove.g5project.repository;

import com.mypetlove.g5project.entity.Account;
import com.mypetlove.g5project.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {
    Page<Order> findByCustomer(Account customer, Pageable pageable);

    Page<Order> findByCustomerAndStatus(Account customer, Order.OrderStatus status, Pageable pageable);

    Page<Order> findByCustomerAndStatusIn(Account customer, List<Order.OrderStatus> statuses, Pageable pageable);

    @Query("""
            SELECT o
            FROM Order o
            LEFT JOIN o.customer c
            WHERE
                (:keyword IS NULL OR :keyword = '' OR
                 LOWER(o.orderCode) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
                 LOWER(c.fullName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
                 LOWER(c.username) LIKE LOWER(CONCAT('%', :keyword, '%')))
            AND
                (:status IS NULL OR :status = '' OR LOWER(o.status) = LOWER(:status))
            """)
    Page<Order> searchOrders(String keyword, String status, Pageable pageable);

    @EntityGraph(attributePaths = {"customer", "orderItems", "orderItems.product", "paymentHistory"})
    @Query("SELECT o FROM Order o WHERE o.id = :id")
    Optional<Order> findDetailById(Integer id);
}