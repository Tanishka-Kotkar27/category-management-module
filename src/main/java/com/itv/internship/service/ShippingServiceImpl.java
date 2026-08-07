package com.itv.internship.service;

import com.itv.internship.dto.*;
import com.itv.internship.entity.Order;
import com.itv.internship.entity.Shipping;
import com.itv.internship.exception.ResourceNotFoundException;
import com.itv.internship.exception.ShippingAlreadyExistsException;
import com.itv.internship.repository.OrderRepository;
import com.itv.internship.repository.ShippingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ShippingServiceImpl implements ShippingService {

    private final ShippingRepository shippingRepository;
    private final OrderRepository orderRepository;
    private final ShippingCostCalculator costCalculator;

    @Override
    public ShippingCostResponse calculateCost(ShippingCostRequest request) {
        return costCalculator.calculate(request);
    }

    @Override
    @Transactional
    public ShippingResponse createShipping(ShippingCreateRequest request) {
        Order order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Order not found with id: " + request.getOrderId()));

        if (shippingRepository.findByOrder_OrderId(request.getOrderId()).isPresent()) {
            throw new ShippingAlreadyExistsException(
                    "A shipping record already exists for Order #" + request.getOrderId());
        }

        Shipping shipping = new Shipping();
        shipping.setOrder(order);
        shipping.setCourierService(request.getCourierService());
        shipping.setTrackingNumber(request.getTrackingNumber());
        shipping.setShippingCost(request.getShippingCost());

        Shipping saved = shippingRepository.save(shipping);
        return toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ShippingResponse> getAllShipping() {
        return shippingRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ShippingResponse getShippingByOrderId(Long orderId) {
        Shipping shipping = shippingRepository.findByOrder_OrderId(orderId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No shipping record found for Order #" + orderId));
        return toResponse(shipping);
    }

    @Override
    @Transactional(readOnly = true)
    public ShippingResponse trackByTrackingNumber(String trackingNumber) {
        Shipping shipping = shippingRepository.findByTrackingNumber(trackingNumber)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No shipment found with tracking number: " + trackingNumber));
        return toResponse(shipping);
    }

    @Override
    @Transactional
    public ShippingResponse updateShipping(Long shippingId, ShippingUpdateRequest request) {
        Shipping shipping = shippingRepository.findById(shippingId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Shipping record not found with id: " + shippingId));

        if (request.getCourierService() != null) {
            shipping.setCourierService(request.getCourierService());
        }
        if (request.getTrackingNumber() != null) {
            shipping.setTrackingNumber(request.getTrackingNumber());
        }
        if (request.getShippingStatus() != null) {
            shipping.setShippingStatus(request.getShippingStatus());
        }

        Shipping updated = shippingRepository.save(shipping);
        return toResponse(updated);
    }

    private ShippingResponse toResponse(Shipping shipping) {
        Order order = shipping.getOrder();
        return new ShippingResponse(
                shipping.getShippingId(),
                order.getOrderId(),
                order.getCustomer().getFullName(),
                order.getShippingAddress(),
                shipping.getCourierService(),
                shipping.getTrackingNumber(),
                shipping.getShippingStatus(),
                shipping.getShippingCost(),
                shipping.getCreatedAt(),
                shipping.getUpdatedAt()
        );
    }
}