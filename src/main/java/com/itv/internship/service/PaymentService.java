package com.itv.internship.service;

import com.itv.internship.dto.PaymentRequest;
import com.itv.internship.dto.PaymentResponse;

import java.util.List;

public interface PaymentService {
    PaymentResponse processPayment(PaymentRequest request);
    List<PaymentResponse> getAllPayments();
    PaymentResponse getPaymentById(Long id);
    PaymentResponse refundPayment(Long id);
}