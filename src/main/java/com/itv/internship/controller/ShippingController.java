package com.itv.internship.controller;

import com.itv.internship.dto.*;
import com.itv.internship.service.ShippingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/shipping")
@RequiredArgsConstructor
public class ShippingController {

    private final ShippingService shippingService;

    // Calculate shipping cost (no persistence, just returns the estimate)
    @PostMapping("/calculate")
    public ResponseEntity<ShippingCostResponse> calculateCost(@Valid @RequestBody ShippingCostRequest request) {
        return ResponseEntity.ok(shippingService.calculateCost(request));
    }

    // Create shipping record for an order (once it's shipped)
    @PostMapping
    public ResponseEntity<ShippingResponse> createShipping(@Valid @RequestBody ShippingCreateRequest request) {
        ShippingResponse response = shippingService.createShipping(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // Admin dashboard: all orders with shipping details
    @GetMapping
    public ResponseEntity<List<ShippingResponse>> getAllShipping() {
        return ResponseEntity.ok(shippingService.getAllShipping());
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<ShippingResponse> getByOrderId(@PathVariable Long orderId) {
        return ResponseEntity.ok(shippingService.getShippingByOrderId(orderId));
    }

    // Customer tracking by tracking number
    @GetMapping("/track/{trackingNumber}")
    public ResponseEntity<ShippingResponse> track(@PathVariable String trackingNumber) {
        return ResponseEntity.ok(shippingService.trackByTrackingNumber(trackingNumber));
    }

    // Update tracking info / status
    @PatchMapping("/{shippingId}")
    public ResponseEntity<ShippingResponse> updateShipping(
            @PathVariable Long shippingId,
            @Valid @RequestBody ShippingUpdateRequest request) {
        return ResponseEntity.ok(shippingService.updateShipping(shippingId, request));
    }
}