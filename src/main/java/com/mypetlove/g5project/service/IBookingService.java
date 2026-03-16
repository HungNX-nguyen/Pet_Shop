package com.mypetlove.g5project.service;

import com.mypetlove.g5project.dto.BookingDto.BookingCreateDto;
import com.mypetlove.g5project.dto.BookingDto.BookingDto;
import com.mypetlove.g5project.dto.BookingDto.TimeSlotDto;

import java.time.LocalDate;
import java.util.List;

public interface IBookingService {
    BookingDto createBooking(String username, BookingCreateDto dto);
    BookingDto getBookingById(Integer id);
    List<BookingDto> getBookingsByUsername(String username);
    BookingDto cancelBooking(Integer id, String username);
    List<TimeSlotDto> getAvailableSlots(LocalDate date);
}