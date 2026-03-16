package com.mypetlove.g5project.dto.BookingDto;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TimeSlotDto {
    private String value;   // "09:00"
    private String label;   // "09:00 AM"
    private boolean available;
}