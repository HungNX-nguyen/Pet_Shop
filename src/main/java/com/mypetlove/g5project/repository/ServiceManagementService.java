package com.mypetlove.g5project.repository;

import com.mypetlove.g5project.dto.ServiceRequest;
import com.mypetlove.g5project.entity.Service;
import java.util.List;
public interface ServiceManagementService
{
    List<Service> getAll();
    Service getById(Integer id);
    Service create(ServiceRequest request);
    Service update(Integer id, ServiceRequest request);
    void toggleStatus(Integer id);
}
