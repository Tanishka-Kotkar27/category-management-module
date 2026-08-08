package com.itv.internship.repository;

import com.itv.internship.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByProduct_ProductIdAndStatusTrue(Long productId);
    List<Review> findByProduct_ProductId(Long productId);
    List<Review> findByCustomer_UserId(Long customerId);
    Optional<Review> findByCustomer_UserIdAndProduct_ProductId(Long customerId, Long productId);
    boolean existsByCustomer_UserIdAndProduct_ProductId(Long customerId, Long productId);
}