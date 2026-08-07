package com.itv.internship.service;

import com.itv.internship.dto.*;

import java.util.List;

public interface ShippingService {
    ShippingCostResponse calculateCost(ShippingCostRequest request);
    ShippingResponse createShipping(ShippingCreateRequest request);
    List<ShippingResponse> getAllShipping();
    ShippingResponse getShippingByOrderId(Long orderId);
    ShippingResponse trackByTrackingNumber(String trackingNumber);
    ShippingResponse updateShipping(Long shippingId, ShippingUpdateRequest request);
}