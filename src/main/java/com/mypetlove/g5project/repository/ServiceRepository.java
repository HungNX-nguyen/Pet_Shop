package com.mypetlove.g5project.repository;

import com.mypetlove.g5project.entity.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ServiceRepository extends JpaRepository<Service, Integer> {

    long countByIsActiveTrue();

    Page<Service> findByNameContainingIgnoreCase(String keyword, Pageable pageable);

    Page<Service> findByCategoryIgnoreCaseAndNameContainingIgnoreCase(
            String category, String keyword, Pageable pageable
    );
    List<Service> findByIsActiveTrue();
}