package com.mypetlove.g5project.service.impl;

import com.mypetlove.g5project.dto.ServiceRequest;
import com.mypetlove.g5project.entity.Account;
import com.mypetlove.g5project.entity.Service;
import com.mypetlove.g5project.repository.AccountRepository;
import com.mypetlove.g5project.repository.ServiceRepository;
import com.mypetlove.g5project.service.ServiceManagementService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

@org.springframework.stereotype.Service
@RequiredArgsConstructor
public class ServiceManagementServiceImpl implements ServiceManagementService {

    private final ServiceRepository serviceRepository;
    private final AccountRepository accountRepository;

    @Override
    public Page<Service> getPage(String keyword, String category, String sort, int page, int size) {
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
    public Service getById(Integer id) {
        return serviceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Service not found"));
    }

    @Override
    public Service create(ServiceRequest request, String username) {
        Account account;

        if (username != null && !"anonymousUser".equals(username)) {
            account = accountRepository.findByUsername(username)
                    .orElseGet(() -> accountRepository.findFirstByOrderByAccountIdAsc()
                            .orElseThrow(() -> new RuntimeException("No account found")));
        } else {
            account = accountRepository.findFirstByOrderByAccountIdAsc()
                    .orElseThrow(() -> new RuntimeException("No account found"));
        }

        Service service = new Service();
        service.setName(request.getName());
        service.setCategory(request.getCategory());
        service.setDescription(request.getDescription());
        service.setPrice(request.getPrice());
        service.setDuration(request.getDuration());
        service.setCreator(account);
        service.setCreatedAt(LocalDateTime.now());
        service.setUpdatedAt(LocalDateTime.now());
        service.setIsActive(true); // create luôn active

        return serviceRepository.save(service);
    }

    @Override
    public Service update(Integer id, ServiceRequest request) {
        Service service = getById(id);

        service.setName(request.getName());
        service.setCategory(request.getCategory());
        service.setDescription(request.getDescription());
        service.setPrice(request.getPrice());
        service.setDuration(request.getDuration());
        service.setUpdatedAt(LocalDateTime.now());

        // KHÔNG sửa isActive ở đây nữa
        return serviceRepository.save(service);
    }

    @Override
    public void toggleStatus(Integer id) {
        Service service = getById(id);
        service.setIsActive(!Boolean.TRUE.equals(service.getIsActive()));
        service.setUpdatedAt(LocalDateTime.now());
        serviceRepository.save(service);
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
        List<Service> services = serviceRepository.findAll();

        if (services.isEmpty()) return BigDecimal.ZERO;

        BigDecimal total = services.stream()
                .map(Service::getPrice)
                .filter(p -> p != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        long count = services.stream().filter(s -> s.getPrice() != null).count();
        if (count == 0) return BigDecimal.ZERO;

        return total.divide(BigDecimal.valueOf(count), 2, RoundingMode.HALF_UP);
    }
}