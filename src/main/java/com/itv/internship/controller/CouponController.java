package com.itv.internship.controller;

import com.itv.internship.dto.*;
import com.itv.internship.service.CouponService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/coupons")
@RequiredArgsConstructor
public class CouponController {

    private final CouponService couponService;

    @PostMapping
    public ResponseEntity<CouponResponse> createCoupon(@Valid @RequestBody CouponRequest request) {
        CouponResponse response = couponService.createCoupon(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // Dashboard: all coupons (active and expired)
    @GetMapping
    public ResponseEntity<List<CouponResponse>> getAllCoupons() {
        return ResponseEntity.ok(couponService.getAllCoupons());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CouponResponse> getCouponById(@PathVariable Long id) {
        return ResponseEntity.ok(couponService.getCouponById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CouponResponse> updateCoupon(
            @PathVariable Long id,
            @Valid @RequestBody CouponRequest request) {
        return ResponseEntity.ok(couponService.updateCoupon(id, request));
    }

    // Soft delete (deactivate)
    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<Map<String, String>> deactivateCoupon(@PathVariable Long id) {
        couponService.deactivateCoupon(id);
        return ResponseEntity.ok(Map.of("message", "Coupon deactivated successfully"));
    }

    @PatchMapping("/{id}/activate")
    public ResponseEntity<CouponResponse> activateCoupon(@PathVariable Long id) {
        return ResponseEntity.ok(couponService.activateCoupon(id));
    }

    // Hard delete
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteCoupon(@PathVariable Long id) {
        couponService.deleteCoupon(id);
        return ResponseEntity.ok(Map.of("message", "Coupon deleted permanently"));
    }

    // Apply coupon at checkout
    @PostMapping("/apply")
    public ResponseEntity<ApplyCouponResponse> applyCoupon(@Valid @RequestBody ApplyCouponRequest request) {
        return ResponseEntity.ok(couponService.applyCoupon(request));
    }
}