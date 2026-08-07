package com.itv.internship.repository;

import com.itv.internship.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByOrder_OrderId(Long orderId);
    Optional<Payment> findFirstByOrder_OrderIdOrderByPaymentIdDesc(Long orderId);
}