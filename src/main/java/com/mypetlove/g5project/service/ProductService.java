package com.mypetlove.g5project.service;

import com.mypetlove.g5project.entity.Product;

import java.util.List;

public interface ProductService {
    public List<Product> getBestSellers();
    public List<Product> getAllActiveProducts();
    public List<Product> searchProducts(String keyword);
}
