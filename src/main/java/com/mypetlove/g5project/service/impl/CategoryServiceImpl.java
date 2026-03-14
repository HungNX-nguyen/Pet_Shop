package com.mypetlove.g5project.service.impl;

import com.mypetlove.g5project.entity.Category;
import com.mypetlove.g5project.repository.CategoryRepository;
import com.mypetlove.g5project.service.CategoryService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;

    @Override
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }
}
