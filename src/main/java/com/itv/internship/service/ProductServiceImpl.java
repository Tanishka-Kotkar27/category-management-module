package com.itv.internship.service;

import com.itv.internship.dto.ProductRequest;
import com.itv.internship.dto.ProductResponse;
import com.itv.internship.entity.Category;
import com.itv.internship.entity.Product;
import com.itv.internship.exception.DuplicateSkuException;
import com.itv.internship.exception.ResourceNotFoundException;
import com.itv.internship.repository.CategoryRepository;
import com.itv.internship.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    @Override
    @Transactional
    public ProductResponse createProduct(ProductRequest request) {
        if (productRepository.existsBySkuIgnoreCase(request.getSku())) {
            throw new DuplicateSkuException("A product with SKU '" + request.getSku() + "' already exists");
        }

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Category not found with id: " + request.getCategoryId()));

        Product product = new Product();
        applyRequestToEntity(product, request, category);
        product.setStatus(true);

        Product saved = productRepository.save(product);
        return toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponse> getAllProducts() {
        return productRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ProductResponse getProductById(Long id) {
        Product product = findProductOrThrow(id);
        return toResponse(product);
    }

    @Override
    @Transactional
    public ProductResponse updateProduct(Long id, ProductRequest request) {
        Product product = findProductOrThrow(id);

        if (!product.getSku().equalsIgnoreCase(request.getSku())
                && productRepository.existsBySkuIgnoreCase(request.getSku())) {
            throw new DuplicateSkuException("A product with SKU '" + request.getSku() + "' already exists");
        }

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Category not found with id: " + request.getCategoryId()));

        applyRequestToEntity(product, request, category);

        Product updated = productRepository.save(product);
        return toResponse(updated);
    }

    @Override
    @Transactional
    public void deactivateProduct(Long id) {
        Product product = findProductOrThrow(id);
        product.setStatus(false);
        productRepository.save(product);
    }

    @Override
    @Transactional
    public ProductResponse activateProduct(Long id) {
        Product product = findProductOrThrow(id);
        product.setStatus(true);
        return toResponse(productRepository.save(product));
    }

    private void applyRequestToEntity(Product product, ProductRequest request, Category category) {
        product.setProductName(request.getProductName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setSku(request.getSku());
        product.setCategory(category);
        product.setInventoryCount(request.getInventoryCount());
    }

    private Product findProductOrThrow(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
    }

    private ProductResponse toResponse(Product product) {
        return new ProductResponse(
                product.getProductId(),
                product.getProductName(),
                product.getDescription(),
                product.getPrice(),
                product.getSku(),
                product.getCategory() != null ? product.getCategory().getCategoryId() : null,
                product.getCategory() != null ? product.getCategory().getCategoryName() : null,
                product.getInventoryCount(),
                product.getCreatedAt(),
                product.getUpdatedAt(),
                product.getStatus()
        );
    }
}