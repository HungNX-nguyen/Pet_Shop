package com.mypetlove.g5project.service.impl;

import com.mypetlove.g5project.dto.ServiceRequest;
import com.mypetlove.g5project.entity.Account;
import com.mypetlove.g5project.entity.ServiceEntity;
import com.mypetlove.g5project.repository.AccountRepository;
import com.mypetlove.g5project.repository.ServiceRepository;
import com.mypetlove.g5project.service.ServiceManagementService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ServiceManagementServiceImpl implements ServiceManagementService {

    private final ServiceRepository serviceRepository;
    private final AccountRepository accountRepository;

    @Override
    public Page<ServiceEntity> getPage(String keyword, String category, String sort, int page, int size) {
        Sort sortObj = switch (sort == null ? "newest" : sort) {
            case "price" -> Sort.by("price").ascending();
            case "duration" -> Sort.by("duration").ascending();
            default -> Sort.by("createdAt").descending();
        };

        Pageable pageable = PageRequest.of(page, size, sortObj);

        String safeKeyword = keyword == null ? "" : keyword.trim();
        String safeCategory = category == null ? "" : category.trim();

        if (!safeCategory.isBlank() && !"All".equalsIgnoreCase(safeCategory)) {
            return serviceRepository.findByCategoryIgnoreCaseAndNameContainingIgnoreCase(
                    safeCategory, safeKeyword, pageable
            );
        }

        return serviceRepository.findByNameContainingIgnoreCase(safeKeyword, pageable);
    }

    @Override
    public ServiceEntity getById(Integer id) {
        return serviceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Service not found"));
    }

    @Override
    public ServiceEntity create(ServiceRequest request, String username) {
        Account account;

        if (username != null && !"anonymousUser".equals(username)) {
            account = accountRepository.findByUsername(username)
                    .orElseGet(() -> accountRepository.findFirstByOrderByAccountIDAsc()
                            .orElseThrow(() -> new RuntimeException("No account found")));
        } else {
            account = accountRepository.findFirstByOrderByAccountIDAsc()
                    .orElseThrow(() -> new RuntimeException("No account found"));
        }

        ServiceEntity serviceEntity = new ServiceEntity();
        serviceEntity.setName(request.getName());
        serviceEntity.setCategory(request.getCategory());
        serviceEntity.setDescription(request.getDescription());
        serviceEntity.setPrice(request.getPrice());
        serviceEntity.setDuration(request.getDuration());
        serviceEntity.setCreatedBy(account);
        serviceEntity.setCreatedAt(LocalDateTime.now());
        serviceEntity.setUpdatedAt(LocalDateTime.now());
        serviceEntity.setIsActive(true); // create luôn active

        return serviceRepository.save(serviceEntity);
    }

    @Override
    public ServiceEntity update(Integer id, ServiceRequest request) {
        ServiceEntity serviceEntity = getById(id);

        serviceEntity.setName(request.getName());
        serviceEntity.setCategory(request.getCategory());
        serviceEntity.setDescription(request.getDescription());
        serviceEntity.setPrice(request.getPrice());
        serviceEntity.setDuration(request.getDuration());
        serviceEntity.setUpdatedAt(LocalDateTime.now());

        // KHÔNG sửa isActive ở đây nữa
        return serviceRepository.save(serviceEntity);
    }

    @Override
    public void toggleStatus(Integer id) {
        ServiceEntity serviceEntity = getById(id);
        serviceEntity.setIsActive(!Boolean.TRUE.equals(serviceEntity.getIsActive()));
        serviceEntity.setUpdatedAt(LocalDateTime.now());
        serviceRepository.save(serviceEntity);
    }

    @Override
    public long countAll() {
        return serviceRepository.count();
    }

    @Override
    public long countActive() {
        return serviceRepository.countByIsActiveTrue();
    }

    @Override
    public long countInactive() {
        return countAll() - countActive();
    }

    @Override
    public BigDecimal averagePrice() {
        List<ServiceEntity> services = serviceRepository.findAll();

        if (services.isEmpty()) return BigDecimal.ZERO;

        BigDecimal total = services.stream()
                .map(ServiceEntity::getPrice)
                .filter(p -> p != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        long count = services.stream().filter(s -> s.getPrice() != null).count();
        if (count == 0) return BigDecimal.ZERO;

        return total.divide(BigDecimal.valueOf(count), 2, RoundingMode.HALF_UP);
    }
}