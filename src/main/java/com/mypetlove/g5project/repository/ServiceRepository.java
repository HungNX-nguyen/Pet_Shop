package com.mypetlove.g5project.repository;

import com.mypetlove.g5project.entity.Service;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServiceRepository extends JpaRepository<Service, Integer> {
}
