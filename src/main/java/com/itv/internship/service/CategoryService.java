package com.itv.internship.service;

import com.itv.internship.dto.CategoryRequest;
import com.itv.internship.dto.CategoryResponse;

import java.util.List;

public interface CategoryService {
    CategoryResponse createCategory(CategoryRequest request);
    List<CategoryResponse> getAllCategories();
    CategoryResponse getCategoryById(Long id);
    CategoryResponse updateCategory(Long id, CategoryRequest request);
    void deactivateCategory(Long id, boolean force);
    CategoryResponse activateCategory(Long id);
}