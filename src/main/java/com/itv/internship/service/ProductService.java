package com.itv.internship.service;

import com.itv.internship.dto.ProductRequest;
import com.itv.internship.dto.ProductResponse;

import java.util.List;

public interface ProductService {
    ProductResponse createProduct(ProductRequest request);
    List<ProductResponse> getAllProducts();
    ProductResponse getProductById(Long id);
    ProductResponse updateProduct(Long id, ProductRequest request);
    void deactivateProduct(Long id);
    ProductResponse activateProduct(Long id);
}