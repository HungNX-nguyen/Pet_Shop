package com.mypetlove.g5project.service.impl;

import com.mypetlove.g5project.dto.DtoRespone.ProductCardResponse;
import com.mypetlove.g5project.dto.DtoRespone.ProductDetailResponse;
import com.mypetlove.g5project.entity.Product;
import com.mypetlove.g5project.repository.ProductRepository;
import com.mypetlove.g5project.service.ProductService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Transactional
@Service
@Slf4j
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;

    // ===== Entity methods (giữ nguyên) =====

    @Override
    public List<Product> getBestSellers() {
        return productRepository.findTop8ByIsActiveTrueOrderByIdDesc();
    }

    @Override
    public List<Product> getAllActiveProducts() {
        return productRepository.findByIsActiveTrue();
    }

    @Override
    public List<Product> searchProducts(String keyword) {
        return productRepository.findByNameContainingIgnoreCaseAndIsActiveTrue(keyword);
    }

    @Override
    public Page<Product> getActiveProducts(Pageable pageable) {
        return productRepository.findByIsActiveTrue(pageable);
    }

    @Override
    public Page<Product> searchProducts(String keyword, Pageable pageable) {
        return productRepository.findByNameContainingIgnoreCaseAndIsActiveTrue(keyword, pageable);
    }

    @Override
    public Page<Product> getProductsByCategory(Integer categoryId, Pageable pageable) {
        return productRepository.findByCategoryIdAndIsActiveTrue(categoryId, pageable);
    }

    @Override
    public Page<Product> searchProductsByCategory(Integer categoryId, String keyword, Pageable pageable) {
        return productRepository.findByCategoryIdAndNameContainingIgnoreCaseAndIsActiveTrue(categoryId, keyword, pageable);
    }

    // ===== DTO methods (for views) =====

    @Override
    public List<ProductCardResponse> getBestSellerDtos() {
        return productRepository.findTop8ByIsActiveTrueOrderByIdDesc()
                .stream()
                .map(this::toDtoRespone)
                .collect(Collectors.toList());
    }

    @Override
    public Page<ProductCardResponse> getActiveProductDtos(Pageable pageable) {
        return productRepository.findByIsActiveTrue(pageable).map(this::toDtoRespone);
    }

    @Override
    public Page<ProductCardResponse> searchProductDtos(String keyword, Pageable pageable) {
        return productRepository.findByNameContainingIgnoreCaseAndIsActiveTrue(keyword, pageable).map(this::toDtoRespone);
    }

    @Override
    public Page<ProductCardResponse> getProductDtosByCategory(Integer categoryId, Pageable pageable) {
        return productRepository.findByCategoryIdAndIsActiveTrue(categoryId, pageable).map(this::toDtoRespone);
    }

    @Override
    public Page<ProductCardResponse> searchProductDtosByCategory(Integer categoryId, String keyword, Pageable pageable) {
        return productRepository.findByCategoryIdAndNameContainingIgnoreCaseAndIsActiveTrue(categoryId, keyword, pageable).map(this::toDtoRespone);
    }

    // ===== Product Detail =====

    @Override
    public ProductDetailResponse getProductDetailById(Integer id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
        return toDetailDto(product);
    }

    @Override
    public List<ProductCardResponse> getRelatedProducts(Integer categoryId, Integer excludeProductId, int limit) {
        Page<Product> page = productRepository.findByCategoryIdAndIsActiveTrue(categoryId, PageRequest.of(0, limit + 1));
        return page.getContent().stream()
                .filter(p -> !p.getId().equals(excludeProductId))
                .limit(limit)
                .map(this::toDtoRespone)
                .collect(Collectors.toList());
    }

    // ===== Helpers =====

    private ProductCardResponse toDtoRespone(Product product) {
        return ProductCardResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .imageUrl(product.getImageUrl())
                .categoryName(product.getCategory() != null ? product.getCategory().getName() : null)
                .build();
    }

    private ProductDetailResponse toDetailDto(Product product) {
        return ProductDetailResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .imageUrl(product.getImageUrl())
                .stockQuantity(product.getStockQuantity())
                .categoryName(product.getCategory() != null ? product.getCategory().getName() : null)
                .categoryId(product.getCategory() != null ? product.getCategory().getId() : null)
                .build();
    }
}
