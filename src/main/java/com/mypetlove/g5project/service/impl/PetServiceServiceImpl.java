package com.mypetlove.g5project.service.impl;

import com.mypetlove.g5project.dto.ServiceDto.ServiceDto;
import com.mypetlove.g5project.entity.Service;
import com.mypetlove.g5project.repository.ServiceRepository;
import com.mypetlove.g5project.service.PetServiceService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.stream.Collectors;

@Transactional
@Slf4j
@RequiredArgsConstructor
@org.springframework.stereotype.Service
public class PetServiceServiceImpl implements PetServiceService {

    private final ServiceRepository repository;

    @Override
    public List<ServiceDto> getAllServices() {
        return repository.findAll()
                .stream()
                .map(this::convertToDto)
                .toList();
    }

    @Override
    public List<ServiceDto> getAllActiveServices() {
        return repository.findByIsActiveTrue() // ← chỉ active (cho user)
                .stream()
                .map(this::convertToDto)
                .toList();
    }

    @Override
    public ServiceDto getServiceById(Integer id) {
        Service service = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Service not found: " + id));
        return convertToDto(service);
    }

    private ServiceDto convertToDto(Service service){

        ServiceDto dto = new ServiceDto();

        dto.setId(service.getId());
        dto.setName(service.getName());
        dto.setDescription(service.getDescription());
        dto.setPrice(service.getPrice());
        dto.setDuration(service.getDuration());

        return dto;
    }
}