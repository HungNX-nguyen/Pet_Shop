package com.mypetlove.g5project.service;

import com.mypetlove.g5project.dto.ServiceRequest;
import com.mypetlove.g5project.entity.ServiceEntity;
import org.springframework.data.domain.Page;

import java.math.BigDecimal;

public interface ServiceManagementService {
    Page<ServiceEntity> getPage(String keyword, String category, String sort, int page, int size);

    ServiceEntity getById(Integer id);

    ServiceEntity create(ServiceRequest request, String username);

    ServiceEntity update(Integer id, ServiceRequest request);

    void toggleStatus(Integer id);

    long countAll();
    long countActive();
    long countInactive();
    BigDecimal averagePrice();
}