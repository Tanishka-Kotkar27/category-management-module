package com.itv.internship.service;

import com.itv.internship.dto.PaymentRequest;
import com.itv.internship.dto.PaymentResponse;
import com.itv.internship.entity.*;
import com.itv.internship.exception.PaymentAlreadyProcessedException;
import com.itv.internship.exception.RefundNotAllowedException;
import com.itv.internship.exception.ResourceNotFoundException;
import com.itv.internship.repository.OrderRepository;
import com.itv.internship.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;

    @Override
    @Transactional
    public PaymentResponse processPayment(PaymentRequest request) {
        Order order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Order not found with id: " + request.getOrderId()));

        boolean alreadyPaid = paymentRepository.findByOrder_OrderId(order.getOrderId())
                .stream()
                .anyMatch(p -> p.getPaymentStatus() == PaymentStatus.PAID);

        if (alreadyPaid) {
            throw new PaymentAlreadyProcessedException(
                    "Order #" + order.getOrderId() + " has already been paid");
        }

        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setAmount(request.getAmount());
        payment.setPaymentMethod(request.getPaymentMethod());

        // --- Simulated payment gateway call ---
        // In a real integration this would call Stripe/PayPal's API here.
        boolean gatewaySuccess = simulateGatewayCharge();

        if (gatewaySuccess) {
            payment.setPaymentStatus(PaymentStatus.PAID);
            payment.setTransactionReference("TXN-" + UUID.randomUUID().toString().substring(0, 12).toUpperCase());
        } else {
            payment.setPaymentStatus(PaymentStatus.FAILED);
        }

        Payment saved = paymentRepository.save(payment);
        return toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentResponse> getAllPayments() {
        return paymentRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentResponse getPaymentById(Long id) {
        Payment payment = findPaymentOrThrow(id);
        return toResponse(payment);
    }

    @Override
    @Transactional
    public PaymentResponse refundPayment(Long id) {
        Payment payment = findPaymentOrThrow(id);

        if (payment.getPaymentStatus() != PaymentStatus.PAID) {
            throw new RefundNotAllowedException(
                    "Only paid transactions can be refunded. Current status: " + payment.getPaymentStatus());
        }

        Order order = payment.getOrder();
        if (order.getOrderStatus() != OrderStatus.CANCELLED) {
            throw new RefundNotAllowedException(
                    "Refunds can only be issued for cancelled orders. Order #" + order.getOrderId()
                            + " is currently " + order.getOrderStatus());
        }

        payment.setPaymentStatus(PaymentStatus.REFUNDED);
        Payment updated = paymentRepository.save(payment);
        return toResponse(updated);
    }

    /**
     * Simulated gateway call. Always succeeds here since there's no real
     * Stripe/PayPal integration wired up - swap this out for a real API
     * call if credentials are added later.
     */
    private boolean simulateGatewayCharge() {
        return true;
    }

    private Payment findPaymentOrThrow(Long id) {
        return paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found with id: " + id));
    }

    private PaymentResponse toResponse(Payment payment) {
        return new PaymentResponse(
                payment.getPaymentId(),
                payment.getOrder().getOrderId(),
                payment.getOrder().getCustomer().getFullName(),
                payment.getAmount(),
                payment.getPaymentMethod(),
                payment.getPaymentStatus(),
                payment.getTransactionReference(),
                payment.getCreatedAt(),
                payment.getUpdatedAt()
        );
    }
}