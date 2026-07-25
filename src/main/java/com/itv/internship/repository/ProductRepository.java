package com.itv.internship.repository;

import com.itv.internship.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
    long countByCategory_CategoryId(Long categoryId);
}