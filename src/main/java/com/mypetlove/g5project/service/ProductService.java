package com.mypetlove.g5project.service;

import com.mypetlove.g5project.dto.DtoRespone.ProductCardResponse;
import com.mypetlove.g5project.dto.DtoRespone.ProductDetailResponse;
import com.mypetlove.g5project.entity.Product;
import org.springframework.data.domain.Page;
import com.mypetlove.g5project.dto.ProductRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ProductService {
    public List<Product> getBestSellers();
    public List<Product> getAllActiveProducts();
    public List<Product> searchProducts(String keyword);
    Page<Product> getAllProducts(
            String keyword,
            Integer categoryId,
            Boolean isActive,
            String sort,
            int page,
            int size
    );

    Product getById(Integer id);

    Product create(ProductRequest request, String username);

    Product update(Integer id, ProductRequest request);

    void toggleStatus(Integer id);

    // Paginated methods
    Page<Product> getActiveProducts(Pageable pageable);

    Page<Product> searchProducts(String keyword, Pageable pageable);

    Page<Product> getProductsByCategory(Integer categoryId, Pageable pageable);

    Page<Product> searchProductsByCategory(Integer categoryId, String keyword, Pageable pageable);

    // DTO methods (for views)
    List<ProductCardResponse> getBestSellerDtos();

    Page<ProductCardResponse> getActiveProductDtos(Pageable pageable);

    Page<ProductCardResponse> searchProductDtos(String keyword, Pageable pageable);

    Page<ProductCardResponse> getProductDtosByCategory(Integer categoryId, Pageable pageable);

    Page<ProductCardResponse> searchProductDtosByCategory(Integer categoryId, String keyword, Pageable pageable);

    // Product detail
    ProductDetailResponse getProductDetailById(Integer id);

    List<ProductCardResponse> getRelatedProducts(Integer categoryId, Integer excludeProductId, int limit);
}
