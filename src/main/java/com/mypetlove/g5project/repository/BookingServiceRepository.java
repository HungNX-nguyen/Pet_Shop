package com.mypetlove.g5project.repository;


import com.mypetlove.g5project.entity.BookingService;
import com.mypetlove.g5project.entity.BookingServiceId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookingServiceRepository extends JpaRepository<BookingService, BookingServiceId> {
}
