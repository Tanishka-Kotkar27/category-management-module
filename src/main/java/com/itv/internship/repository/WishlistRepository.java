package com.itv.internship.repository;

import com.itv.internship.entity.Wishlist;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WishlistRepository extends JpaRepository<Wishlist, Long> {
    List<Wishlist> findByCustomer_UserId(Long customerId);
    Optional<Wishlist> findByCustomer_UserIdAndProduct_ProductId(Long customerId, Long productId);
    boolean existsByCustomer_UserIdAndProduct_ProductId(Long customerId, Long productId);
}