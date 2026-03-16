package com.mypetlove.g5project.service;

import com.mypetlove.g5project.dto.ServiceDto.ServiceDto;
import com.mypetlove.g5project.entity.Service;

import java.util.List;

public interface PetServiceService {
    List<ServiceDto> getAllServices();

    ServiceDto getServiceById(Integer id);
}
