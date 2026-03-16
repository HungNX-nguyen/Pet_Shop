package com.mypetlove.g5project.service;

import com.mypetlove.g5project.dto.ServiceRequest;
import com.mypetlove.g5project.entity.Service;
import org.springframework.data.domain.Page;

import java.math.BigDecimal;

public interface ServiceManagementService {
    Page<Service> getPage(String keyword, String category, String sort, int page, int size);

    Service getById(Integer id);

    Service create(ServiceRequest request, String username);

    Service update(Integer id, ServiceRequest request);

    void toggleStatus(Integer id);

    long countAll();
    long countActive();
    long countInactive();
    BigDecimal averagePrice();
}