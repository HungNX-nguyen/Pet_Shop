package com.mypetlove.g5project.dto.BookingDto;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TimeSlotDto {
    private String value;
    private String label;
    private boolean available;
}