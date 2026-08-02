package com.itv.internship.repository;

import com.itv.internship.entity.Order;
import com.itv.internship.entity.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByOrderStatus(OrderStatus orderStatus);
    long countByCustomer_UserId(Long userId);
}