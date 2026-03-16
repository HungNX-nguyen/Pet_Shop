package com.mypetlove.g5project.service.impl;

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
import com.mypetlove.g5project.dto.ProductRequest;
import com.mypetlove.g5project.entity.Account;
import com.mypetlove.g5project.entity.Category;
import com.mypetlove.g5project.repository.AccountRepository;
import com.mypetlove.g5project.repository.CategoryRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;
import org.springframework.data.domain.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.stream.Collectors;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;
import com.mypetlove.g5project.dto.DtoRespone.ProductCardResponse;
import com.mypetlove.g5project.dto.DtoRespone.ProductDetailResponse;
@Transactional
@Service
@Slf4j
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final AccountRepository accountRepository;

    private final String uploadDir = "uploads/products";

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

        String savedImageUrl = saveImage(request.getImageFile());

        Product entity = Product.builder()
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice() != null ? request.getPrice() : BigDecimal.ZERO)
                .stockQuantity(request.getStockQuantity() != null ? request.getStockQuantity() : 0)
                .isActive(request.getIsActive() != null ? request.getIsActive() : true)
                .imageUrl(savedImageUrl)
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

        String savedImageUrl = saveImage(request.getImageFile());

        entity.setName(request.getName());
        entity.setDescription(request.getDescription());
        entity.setPrice(request.getPrice());
        entity.setStockQuantity(request.getStockQuantity());
        entity.setIsActive(request.getIsActive());
        entity.setCategory(category);

        if (StringUtils.hasText(savedImageUrl)) {
            entity.setImageUrl(savedImageUrl);
        }

        return productRepository.save(entity);
    }

    @Override
    public void toggleStatus(Integer id) {
        Product entity = getById(id);
        entity.setIsActive(!Boolean.TRUE.equals(entity.getIsActive()));
        productRepository.save(entity);
    }

    // ===== Entity methods (giữ nguyên) =====

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

    private String saveImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return null;
        }

        try {
            Files.createDirectories(Paths.get(uploadDir));

            String originalFilename = file.getOriginalFilename();
            String extension = "";

            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }

            String newFileName = UUID.randomUUID() + extension;
            Path filePath = Paths.get(uploadDir, newFileName);

            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            return "/uploads/products/" + newFileName;
        } catch (IOException e) {
            throw new RuntimeException("Không thể lưu ảnh sản phẩm", e);
        }
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
