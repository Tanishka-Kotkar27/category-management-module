package com.itv.internship.repository;

import com.itv.internship.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {
    List<Cart> findByCustomer_UserId(Long customerId);
    Optional<Cart> findByCustomer_UserIdAndProduct_ProductId(Long customerId, Long productId);
}