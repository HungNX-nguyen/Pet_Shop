package com.mypetlove.g5project.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "Bookings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)  // sửa: customerId -> customer_id
    private Account customer;

    @ManyToOne(fetch = FetchType.LAZY)                   // thêm: quan hệ với Pet
    @JoinColumn(name = "pet_id")
    private Pet pet;

    @Column(name = "booking_code")
    private String bookingCode;

    @Column(name = "booking_date")
    private LocalDate bookingDate;

    @Column(name = "time_slot")
    private String timeSlot;

    private String status;

    @Column(name = "total_price", precision = 10, scale = 2)
    private BigDecimal totalPrice;

    @Column(columnDefinition = "TEXT")
    private String note;

    @Column(name = "created_at", updatable = false)
    @CreationTimestamp                                   // sửa: tự động set khi tạo
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "booking",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private List<BookingService> bookingServices = new ArrayList<>();
}