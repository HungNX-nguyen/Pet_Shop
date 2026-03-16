package com.mypetlove.g5project.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Slf4j
@Getter
@Setter
@Entity
@Builder
@Table(name = "Services")
public class Service {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;

    private String description;

    private BigDecimal price;

    private Integer duration;

    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "createdBy")
    private Account creator;

    @OneToMany(mappedBy = "service")
    private List<BookingService> bookingServices;
}
