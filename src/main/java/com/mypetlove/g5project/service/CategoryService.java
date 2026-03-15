package com.mypetlove.g5project.service;

import com.mypetlove.g5project.dto.DtoRespone.CategoryResponse;
import com.mypetlove.g5project.entity.Category;

import java.util.List;

public interface CategoryService {
    List<Category> getAllCategories();

    // DTO method (for views)
    List<CategoryResponse> getAllCategoryDtos();
}
