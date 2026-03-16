package com.mypetlove.g5project.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

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
@Table(name = "Accounts")
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "account_id")
    private Integer accountId;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "created_at")
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @Column(name = "is_active")
    private Boolean isActive;

    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AccountRole> accountRoles = new ArrayList<>();

    @OneToMany(mappedBy = "owner")
    private List<Pet> pets;

    @OneToMany(mappedBy = "customer")
    private List<Order> orders;

    @OneToMany(mappedBy = "customer")
    private List<Booking> bookings;

    @OneToMany(mappedBy = "creator")
    private List<Product> products;

    @OneToMany(mappedBy = "creator")
    private List<Service> services;


    @OneToMany(mappedBy = "customer")
    private List<Feedback> feedbacks;


}
