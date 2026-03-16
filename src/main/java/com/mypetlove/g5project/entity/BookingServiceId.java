package com.mypetlove.g5project.entity;

import jakarta.persistence.Embeddable;

import java.io.Serializable;

@Embeddable
public class BookingServiceId implements Serializable {

    private Integer bookingId;
    private Integer serviceId;
}
