package com.mypetlove.g5project.repository;

import com.mypetlove.g5project.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Integer> {

    List<Booking> findByCustomer_AccountId(Integer customerAccountId);

    // Kiểm tra slot đã bị đặt chưa
    @Query("""
        SELECT b.timeSlot FROM Booking b
        WHERE b.bookingDate = :date
        AND b.status != 'CANCELLED'
    """)
    List<String> findBookedSlotsByDate(@Param("date") LocalDate date);
}