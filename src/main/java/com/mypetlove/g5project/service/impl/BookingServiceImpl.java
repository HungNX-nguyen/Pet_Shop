package com.mypetlove.g5project.service.impl;

import com.mypetlove.g5project.dto.BookingDto.BookingCreateDto;
import com.mypetlove.g5project.dto.BookingDto.BookingDto;
import com.mypetlove.g5project.dto.BookingDto.TimeSlotDto;
import com.mypetlove.g5project.dto.ServiceDto.ServiceDto;
import com.mypetlove.g5project.entity.*;
import com.mypetlove.g5project.entity.Service;
import com.mypetlove.g5project.repository.*;
import com.mypetlove.g5project.service.IBookingService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@org.springframework.stereotype.Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class BookingServiceImpl implements IBookingService {

    private final BookingRepository bookingRepository;
    private final BookingServiceRepository bookingServiceRepository;
    private final ServiceRepository serviceRepository;
    private final AccountRepository accountRepository;

    // Danh sách slot cố định trong ngày
    private static final List<String> ALL_SLOTS = List.of(
            "09:00", "10:30", "13:00", "14:30", "16:00", "17:30"
    );

    // ------------------------------------------------------------------ //
    //  CREATE
    // ------------------------------------------------------------------ //
    @Override
    public BookingDto createBooking(String username, BookingCreateDto dto) {
        Account customer = accountRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Account not found: " + username));

        Service service = serviceRepository.findById(dto.getServiceId())
                .orElseThrow(() -> new RuntimeException("Service not found: " + dto.getServiceId()));

        // Kiểm tra slot còn trống
        List<String> bookedSlots = bookingRepository
                .findBookedSlotsByDate(dto.getBookingDate());
        if (bookedSlots.contains(dto.getTimeSlot())) {
            throw new RuntimeException("Time slot " + dto.getTimeSlot() + " is already booked.");
        }

        // Gộp pet info + note thành một string
        String fullNote = buildNote(dto);

        String code = "PS-" + UUID.randomUUID().toString().substring(0, 5).toUpperCase();

        Booking booking = Booking.builder()
                .bookingCode(code)
                .bookingDate(dto.getBookingDate())
                .timeSlot(dto.getTimeSlot())
                .status("PENDING")
                .totalPrice(service.getPrice())
                .note(fullNote)
                .createdAt(LocalDateTime.now())
                .customer(customer)
                .build();

        Booking saved = bookingRepository.save(booking);

        BookingService bs = BookingService.builder()
                .id(new BookingServiceId(saved.getId(), service.getId()))
                .booking(saved)
                .service(service)
                .build();
        bookingServiceRepository.save(bs);

        log.info("Booking created [{}] for user [{}]", code, username);
        return toDto(saved, List.of(service));
    }

    // Helper: gộp pet info
    private String buildNote(BookingCreateDto dto) {
        StringBuilder sb = new StringBuilder();
        if (dto.getPetName()  != null && !dto.getPetName().isBlank())
            sb.append("Pet: ").append(dto.getPetName());
        if (dto.getPetBreed() != null && !dto.getPetBreed().isBlank())
            sb.append(" (").append(dto.getPetBreed()).append(")");
        if (dto.getPetAge()   != null)
            sb.append(", Age: ").append(dto.getPetAge()).append("y");
        if (dto.getPetWeight()!= null)
            sb.append(", Weight: ").append(dto.getPetWeight()).append("kg");
        if (dto.getNote()     != null && !dto.getNote().isBlank()) {
            if (sb.length() > 0) sb.append(" | ");
            sb.append(dto.getNote());
        }
        return sb.toString();
    }



    // ------------------------------------------------------------------ //
    //  READ
    // ------------------------------------------------------------------ //
    @Override
    public BookingDto getBookingById(Integer id) {
        Booking booking = findById(id);
        List<Service> services = extractServices(booking);
        return toDto(booking, services);
    }

    @Override
    public List<BookingDto> getBookingsByUsername(String username) {
        Account account = accountRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Account not found: " + username));

        return bookingRepository.findByCustomer_AccountId(account.getAccountId())
                .stream()
                .map(b -> {
                    List<Service> services = extractServices(b);
                    return toDto(b, services);
                })
                .toList();
    }

    // ------------------------------------------------------------------ //
    //  CANCEL
    // ------------------------------------------------------------------ //
    @Override
    public BookingDto cancelBooking(Integer id, String username) {
        Booking booking = findById(id);

        log.info("Cancel - owner: [{}], requester: [{}]",
                booking.getCustomer().getUsername(), username);

        if (!booking.getCustomer().getUsername().equals(username)) {
            throw new RuntimeException("Unauthorized to cancel this booking.");
        }

        // Extract services TRƯỚC khi save (còn trong session)
        List<Service> services = extractServices(booking);

        booking.setStatus("CANCELLED");
        bookingRepository.save(booking);

        return toDto(booking, services);
    }

    // ------------------------------------------------------------------ //
    //  TIME SLOTS
    // ------------------------------------------------------------------ //
    @Override
    public List<TimeSlotDto> getAvailableSlots(LocalDate date) {
        List<String> booked = bookingRepository.findBookedSlotsByDate(date);
        return ALL_SLOTS.stream().map(slot -> TimeSlotDto.builder()
                .value(slot)
                .label(formatSlotLabel(slot))
                .available(!booked.contains(slot))
                .build()
        ).toList();
    }

    // ------------------------------------------------------------------ //
    //  HELPERS
    // ------------------------------------------------------------------ //
    private Booking findById(Integer id) {
        return bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found: " + id));
    }

    private List<Service> extractServices(Booking booking) {
        return booking.getBookingServices().stream()
                .map(BookingService::getService)
                .toList();
    }

    private String formatSlotLabel(String slot) {
        // "09:00" -> "09:00 AM" | "13:00" -> "01:00 PM"
        String[] parts = slot.split(":");
        int hour = Integer.parseInt(parts[0]);
        String suffix = hour < 12 ? "AM" : "PM";
        int displayHour = hour > 12 ? hour - 12 : (hour == 0 ? 12 : hour);
        return String.format("%02d:%s %s", displayHour, parts[1], suffix);
    }

    private BookingDto toDto(Booking booking, List<Service> services) {
        return BookingDto.builder()
                .id(booking.getId())
                .bookingCode(booking.getBookingCode())
                .bookingDate(booking.getBookingDate())
                .timeSlot(booking.getTimeSlot())
                .status(booking.getStatus())
                .totalPrice(booking.getTotalPrice())
                .note(booking.getNote())
                .createdAt(booking.getCreatedAt())
                .services(services.stream().map(this::toServiceDto).toList())
                .build();
    }

    private ServiceDto toServiceDto(Service s) {
        return ServiceDto.builder()
                .id(s.getId())
                .name(s.getName())
                .description(s.getDescription())
                .price(s.getPrice())
                .duration(s.getDuration())
                .build();
    }
}