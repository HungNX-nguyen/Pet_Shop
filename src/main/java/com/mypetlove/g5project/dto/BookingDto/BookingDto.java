package com.mypetlove.g5project.dto.BookingDto;

import com.mypetlove.g5project.dto.ServiceDto.ServiceDto;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingDto {
    private Integer id;
    private String bookingCode;
    private LocalDate bookingDate;
    private String timeSlot;
    private String status;
    private BigDecimal totalPrice;
    private String note;
    private LocalDateTime createdAt;

    private String customerName;
    private String customerPhone;

    // Pet info
    private String petName;
    private String petBreed;
    private Integer petAge;
    private Double petWeight;

    private List<ServiceDto> services;
}