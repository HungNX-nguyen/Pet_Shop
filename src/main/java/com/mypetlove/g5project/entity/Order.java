package com.mypetlove.g5project.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Slf4j
@Getter
@Setter
@Entity
@Builder
@Table(name = "Orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "order_code", length = 50)
    private String orderCode;

    @Column(name = "total_amount", precision = 10, scale = 2)
    private BigDecimal totalAmount;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

//    @Column(name = "status", length = 50)
//    private String status;

    @Column(name = "shipping_address")
    private String shippingAddress;

    @Column(name = "created_at")
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @ManyToOne
    @JoinColumn(name = "customerId")
    private Account customer;

    // CASCADE HERE
    @OneToMany(mappedBy = "order",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private List<OrderItem> orderItems = new ArrayList<>();

    @OneToOne(mappedBy = "order")
    private PaymentHistory paymentHistory;

    public enum OrderStatus {
        WAITING_PAYMENT,
        PROCESSING,
        CONFIRMED,
        SHIPPING,
        DELIVERED,
        CANCELLED,
        PAID
    }
}
