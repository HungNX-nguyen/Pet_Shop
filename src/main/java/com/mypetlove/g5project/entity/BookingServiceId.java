package com.mypetlove.g5project.entity;

import jakarta.persistence.Embeddable;
import lombok.*;
import java.io.Serializable;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingServiceId implements Serializable {
    private Integer bookingId;
    private Integer serviceId;
}