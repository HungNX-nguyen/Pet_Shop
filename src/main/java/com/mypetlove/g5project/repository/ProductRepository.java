package com.mypetlove.g5project.repository;

import com.mypetlove.g5project.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer>, JpaSpecificationExecutor<Product> {

    List<Product> findByIsActiveTrue();

    List<Product> findTop8ByIsActiveTrueOrderByIdDesc();

    List<Product> findByCategoryIdAndIsActiveTrue(Integer categoryId);

    List<Product> findByNameContainingIgnoreCaseAndIsActiveTrue(String name);

    Page<Product> findByNameContainingIgnoreCase(String keyword, Pageable pageable);
    // Paginated queries
    Page<Product> findByIsActiveTrue(Pageable pageable);

    Page<Product> findByNameContainingIgnoreCaseAndIsActiveTrue(String name, Pageable pageable);

    Page<Product> findByCategoryIdAndIsActiveTrue(Integer categoryId, Pageable pageable);

    Page<Product> findByCategoryIdAndNameContainingIgnoreCaseAndIsActiveTrue(Integer categoryId, String name, Pageable pageable);
}
