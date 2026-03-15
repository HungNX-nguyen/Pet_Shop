package com.mypetlove.g5project.repository;

import com.mypetlove.g5project.entity.ServiceEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServiceRepository extends JpaRepository<ServiceEntity, Integer> {

    long countByIsActiveTrue();

    Page<ServiceEntity> findByNameContainingIgnoreCase(String keyword, Pageable pageable);

    Page<ServiceEntity> findByCategoryIgnoreCaseAndNameContainingIgnoreCase(
            String category, String keyword, Pageable pageable
    );
}