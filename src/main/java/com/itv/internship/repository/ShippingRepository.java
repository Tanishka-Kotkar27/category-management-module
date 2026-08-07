package com.itv.internship.repository;

import com.itv.internship.entity.Shipping;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ShippingRepository extends JpaRepository<Shipping, Long> {
    Optional<Shipping> findByOrder_OrderId(Long orderId);
    Optional<Shipping> findByTrackingNumber(String trackingNumber);
}