package com.mypetlove.g5project.dto.BookingDto;

import lombok.*;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingCreateDto {
    private Integer serviceId;
    private LocalDate bookingDate;
    private String timeSlot;
    private String note;

    // Pet info
    private String petName;
    private String petBreed;
    private Integer petAge;
    private Double petWeight;

    // Contact info
    private String contactName;
    private String contactPhone;
    private String contactEmail;
}
