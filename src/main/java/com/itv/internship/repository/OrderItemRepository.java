package com.itv.internship.repository;

import com.itv.internship.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    boolean existsByOrder_Customer_UserIdAndProduct_ProductId(Long customerId, Long productId);
}