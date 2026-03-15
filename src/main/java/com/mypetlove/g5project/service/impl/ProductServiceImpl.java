package com.mypetlove.g5project.service.impl;

import com.mypetlove.g5project.entity.Product;
import com.mypetlove.g5project.repository.ProductRepository;
import com.mypetlove.g5project.service.ProductService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import com.mypetlove.g5project.dto.ProductRequest;
import com.mypetlove.g5project.entity.Account;
import com.mypetlove.g5project.entity.Category;
import com.mypetlove.g5project.entity.Product;
import com.mypetlove.g5project.repository.AccountRepository;
import com.mypetlove.g5project.repository.CategoryRepository;
import com.mypetlove.g5project.repository.ProductRepository;
import com.mypetlove.g5project.service.ProductService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Transactional
@Service
@Slf4j
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final AccountRepository accountRepository;

    @Override
    public Page<Product> getAllProducts(String keyword, Integer categoryId, Boolean isActive, String sort, int page, int size) {
        Sort sortOption = buildSort(sort);
        Pageable pageable = PageRequest.of(page, size, sortOption);

        Specification<Product> spec = (root, query, cb) -> cb.conjunction();

        if (StringUtils.hasText(keyword)) {
            spec = spec.and((root, query, cb) ->
                    cb.like(cb.lower(root.get("name")), "%" + keyword.toLowerCase() + "%"));
        }

        if (categoryId != null) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.get("category").get("id"), categoryId));
        }

        if (isActive != null) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.get("isActive"), isActive));
        }

        return productRepository.findAll(spec, pageable);
    }

    @Override
    public Product getById(Integer id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy sản phẩm với id=" + id));
    }

    @Override
    public Product create(ProductRequest request, String username) {
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy danh mục"));

        Account account = accountRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy tài khoản tạo"));

        Product entity = Product.builder()
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .stockQuantity(request.getStockQuantity())
                .imageUrl(request.getImageUrl())
                .isActive(request.getIsActive())
                .category(category)
                .creator(account)
                .build();

        return productRepository.save(entity);
    }

    @Override
    public Product update(Integer id, ProductRequest request) {
        Product entity = getById(id);

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy danh mục"));

        entity.setName(request.getName());
        entity.setDescription(request.getDescription());
        entity.setPrice(request.getPrice());
        entity.setStockQuantity(request.getStockQuantity());
        entity.setImageUrl(request.getImageUrl());
        entity.setIsActive(request.getIsActive());
        entity.setCategory(category);

        return productRepository.save(entity);
    }

    @Override
    public void toggleStatus(Integer id) {
        Product entity = getById(id);
        entity.setIsActive(!Boolean.TRUE.equals(entity.getIsActive()));
        productRepository.save(entity);
    }

    private Sort buildSort(String sort) {
        if (!StringUtils.hasText(sort)) {
            return Sort.by(Sort.Direction.DESC, "createdAt");
        }

        return switch (sort) {
            case "name-asc" -> Sort.by(Sort.Direction.ASC, "name");
            case "name-desc" -> Sort.by(Sort.Direction.DESC, "name");
            case "price-asc" -> Sort.by(Sort.Direction.ASC, "price");
            case "price-desc" -> Sort.by(Sort.Direction.DESC, "price");
            case "stock-asc" -> Sort.by(Sort.Direction.ASC, "stockQuantity");
            case "stock-desc" -> Sort.by(Sort.Direction.DESC, "stockQuantity");
            case "oldest" -> Sort.by(Sort.Direction.ASC, "createdAt");
            default -> Sort.by(Sort.Direction.DESC, "createdAt");
        };
    }

    /**
     * Lấy danh sách sản phẩm bán chạy cho Homepage
     * @return
     */
    @Override
    public List<Product> getBestSellers() {
        return productRepository.findTop8ByIsActiveTrueOrderByIdDesc();
    }

    /**
     * Lấy sản phẩm active
     * @return
     */
    @Override
    public List<Product> getAllActiveProducts() {
        return productRepository.findByIsActiveTrue();
    }

    /**
     * Tìm kiếm theo tên
     * @param keyword: tên sp
     * @return
     */
    @Override
    public List<Product> searchProducts(String keyword) {
        return productRepository.findByNameContainingIgnoreCaseAndIsActiveTrue(keyword);
    }


}
