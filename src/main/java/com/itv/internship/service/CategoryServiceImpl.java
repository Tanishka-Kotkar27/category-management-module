package com.itv.internship.service;

import com.itv.internship.dto.CategoryRequest;
import com.itv.internship.dto.CategoryResponse;
import com.itv.internship.entity.Category;
import com.itv.internship.exception.CategoryHasProductsException;
import com.itv.internship.exception.DuplicateCategoryException;
import com.itv.internship.exception.ResourceNotFoundException;
import com.itv.internship.repository.CategoryRepository;
import com.itv.internship.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    @Override
    @Transactional
    public CategoryResponse createCategory(CategoryRequest request) {
        if (categoryRepository.existsByCategoryNameIgnoreCase(request.getCategoryName())) {
            throw new DuplicateCategoryException(
                    "A category named '" + request.getCategoryName() + "' already exists");
        }
        Category category = new Category();
        category.setCategoryName(request.getCategoryName());
        category.setDescription(request.getDescription());
        category.setStatus(true);

        Category saved = categoryRepository.save(category);
        return toResponse(saved);
    }

    @Override
    public List<CategoryResponse> getAllCategories() {
        return categoryRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public CategoryResponse getCategoryById(Long id) {
        Category category = findCategoryOrThrow(id);
        return toResponse(category);
    }

    @Override
    @Transactional
    public CategoryResponse updateCategory(Long id, CategoryRequest request) {
        Category category = findCategoryOrThrow(id);

        if (!category.getCategoryName().equalsIgnoreCase(request.getCategoryName())
                && categoryRepository.existsByCategoryNameIgnoreCase(request.getCategoryName())) {
            throw new DuplicateCategoryException(
                    "A category named '" + request.getCategoryName() + "' already exists");
        }

        category.setCategoryName(request.getCategoryName());
        category.setDescription(request.getDescription());

        Category updated = categoryRepository.save(category);
        return toResponse(updated);
    }

    @Override
    @Transactional
    public void deactivateCategory(Long id, boolean force) {
        Category category = findCategoryOrThrow(id);
        long productCount = productRepository.countByCategory_CategoryId(id);

        if (productCount > 0 && !force) {
            throw new CategoryHasProductsException(
                    "This category has " + productCount + " product(s) assigned to it. " +
                    "Please reassign these products to another category before deactivating, " +
                    "or confirm to deactivate anyway.");
        }

        category.setStatus(false);
        categoryRepository.save(category);
    }

    @Override
    @Transactional
    public CategoryResponse activateCategory(Long id) {
        Category category = findCategoryOrThrow(id);
        category.setStatus(true);
        return toResponse(categoryRepository.save(category));
    }

    private Category findCategoryOrThrow(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));
    }

    private CategoryResponse toResponse(Category category) {
        long productCount = productRepository.countByCategory_CategoryId(category.getCategoryId());
        return new CategoryResponse(
                category.getCategoryId(),
                category.getCategoryName(),
                category.getDescription(),
                category.getCreatedAt(),
                category.getUpdatedAt(),
                category.getStatus(),
                productCount
        );
    }
}