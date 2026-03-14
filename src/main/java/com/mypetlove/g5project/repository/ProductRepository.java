package com.mypetlove.g5project.repository;

import com.mypetlove.g5project.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {

    List<Product> findByIsActiveTrue();

    List<Product> findTop8ByIsActiveTrueOrderByIdDesc();

    List<Product> findByCategoryIdAndIsActiveTrue(Integer categoryId);

    List<Product> findByNameContainingIgnoreCaseAndIsActiveTrue(String name);
}

