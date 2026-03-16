package com.mypetlove.g5project.controller;

import com.mypetlove.g5project.dto.BookingDto.BookingCreateDto;
import com.mypetlove.g5project.dto.BookingDto.BookingDto;
import com.mypetlove.g5project.dto.BookingDto.TimeSlotDto;
import com.mypetlove.g5project.entity.Account;
import com.mypetlove.g5project.entity.Service;
import com.mypetlove.g5project.repository.AccountRepository;
import com.mypetlove.g5project.repository.ServiceRepository;
import com.mypetlove.g5project.service.IBookingService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
public class BookingController {

    private final IBookingService bookingService;
    private final ServiceRepository serviceRepository;
    private final AccountRepository accountRepository;

    // ------------------------------------------------
    // GET /services/{id}/booking
    // ------------------------------------------------
    @GetMapping("/services/{id}/booking")
    public String bookingForm(@PathVariable Integer id,
                              @RequestParam(required = false) String date,
                              @AuthenticationPrincipal UserDetails userDetails,
                              Model model) {

        Service service = serviceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Service not found"));

        LocalDate selectedDate = (date != null && !date.isBlank())
                ? LocalDate.parse(date)
                : LocalDate.now().plusDays(1);

        List<TimeSlotDto> slots =
                bookingService.getAvailableSlots(selectedDate);

        // Pre-fill contact từ account đang login
        Account account = accountRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Account not found"));

        BookingCreateDto dto = BookingCreateDto.builder()
                .contactName(account.getFullName())
                .contactPhone(account.getPhoneNumber())
                .contactEmail(account.getEmail())
                .build();

        model.addAttribute("service", service);
        model.addAttribute("bookingCreateDto", dto);
        model.addAttribute("timeSlots", slots);
        model.addAttribute("selectedDate", selectedDate);

        return "service-booking/booking-form";
    }

    // ------------------------------------------------
    // POST /services/{id}/booking
    // ------------------------------------------------
    @PostMapping("/services/{id}/booking")
    public String submitBooking(@PathVariable Integer id,
                                @ModelAttribute BookingCreateDto dto,
                                @AuthenticationPrincipal UserDetails userDetails,
                                RedirectAttributes redirectAttrs) {
        try {
            dto.setServiceId(id);
            BookingDto booking = bookingService.createBooking(userDetails.getUsername(), dto);

            // Dùng addFlashAttribute để truyền qua redirect
            redirectAttrs.addFlashAttribute("booking", booking);
            return "redirect:/booking/success";

        } catch (Exception e) {
            log.error("Booking failed: {}", e.getMessage(), e);
            redirectAttrs.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/services/" + id + "/booking";
        }
    }

    @GetMapping("/booking/success")
    public String bookingSuccess(Model model) {
        // Nếu không có booking trong flash → redirect về services
        if (!model.containsAttribute("booking")) {
            return "redirect:/petlover/services";
        }
        return "service-booking/booking-success";
    }
    // ------------------------------------------------
    // GET /bookings
    // ------------------------------------------------
    @GetMapping("/petlover/bookings")
    public String myBookings(@AuthenticationPrincipal UserDetails userDetails,
                             Model model) {

        List<BookingDto> bookings =
                bookingService.getBookingsByUsername(
                        userDetails.getUsername());

        model.addAttribute("bookings", bookings);

        return "service-booking/my-bookings";
    }

    // ------------------------------------------------
    // POST /bookings/{id}/cancel
    // ------------------------------------------------
    @PostMapping("/petlover/bookings/{id}/cancel")
    public String cancelBooking(@PathVariable Integer id,
                                @AuthenticationPrincipal UserDetails userDetails,
                                RedirectAttributes redirectAttrs) {
        try {
            bookingService.cancelBooking(id, userDetails.getUsername());
            redirectAttrs.addFlashAttribute("successMessage", "Booking cancelled successfully.");
        } catch (Exception e) {
            redirectAttrs.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/petlover/bookings";
    }



}