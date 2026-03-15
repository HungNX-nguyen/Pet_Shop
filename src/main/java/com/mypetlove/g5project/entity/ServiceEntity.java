package com.mypetlove.g5project.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Slf4j
@Getter
@Setter
@Entity
@Builder
@Table(name = "Services")
public class ServiceEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable = false)
    private Account createdBy;

    @Column(length = 255)
    private String name;

    @Column(name = "category", length = 50, nullable = false)
    private String category;

    @Lob
    private String description;

    @Column(precision = 10, scale = 2)
    private BigDecimal price;

    private Integer duration;

    @Column(name = "is_active")
    private Boolean isActive;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}