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
}
