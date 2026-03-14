package com.mypetlove.g5project.service;

import com.mypetlove.g5project.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ProductService {
    List<Product> getBestSellers();

    List<Product> getAllActiveProducts();

    List<Product> searchProducts(String keyword);

    // Paginated methods
    Page<Product> getActiveProducts(Pageable pageable);

    Page<Product> searchProducts(String keyword, Pageable pageable);

    Page<Product> getProductsByCategory(Integer categoryId, Pageable pageable);

    Page<Product> searchProductsByCategory(Integer categoryId, String keyword, Pageable pageable);
}
