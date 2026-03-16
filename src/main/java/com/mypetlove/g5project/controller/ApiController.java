package com.mypetlove.g5project.controller;

import com.mypetlove.g5project.dto.BookingDto.TimeSlotDto;
import com.mypetlove.g5project.service.IBookingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController          // ← @RestController, KHÔNG phải @Controller
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class ApiController {

    private final IBookingService bookingService;

    @GetMapping("/slots")
    public ResponseEntity<?> getSlots(@RequestParam String date) {
        log.info("=====> /api/slots HIT, date={}", date);
        try {
            List<TimeSlotDto> slots = bookingService.getAvailableSlots(LocalDate.parse(date));
            log.info("=====> slots count: {}", slots.size());
            return ResponseEntity.ok(slots);
        } catch (Exception e) {
            log.error("=====> Error: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }
}
