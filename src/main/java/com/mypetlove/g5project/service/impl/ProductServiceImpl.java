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

@Transactional
@Service
@Slf4j
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;

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
