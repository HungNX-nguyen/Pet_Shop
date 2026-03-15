package com.mypetlove.g5project.service;

import com.mypetlove.g5project.entity.Product;
import org.springframework.data.domain.Page;
import com.mypetlove.g5project.dto.ProductRequest;
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
}
