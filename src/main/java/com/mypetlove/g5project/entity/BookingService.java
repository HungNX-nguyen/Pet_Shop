package com.mypetlove.g5project.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

@AllArgsConstructor
@NoArgsConstructor
@Slf4j
@Getter
@Setter
@Entity
@Builder
@Table(name = "bookingservices")
public class BookingService {
    @EmbeddedId
    private BookingServiceId id;

    @ManyToOne
    @MapsId("bookingId")
    @JoinColumn(name = "bookingId")
    private Booking booking;

    @ManyToOne
    @MapsId("serviceId")
    @JoinColumn(name = "serviceId")
    private Service service;
}
