package com.itv.internship.service;

import com.itv.internship.dto.*;

import java.util.List;

public interface CouponService {
    CouponResponse createCoupon(CouponRequest request);
    List<CouponResponse> getAllCoupons();
    CouponResponse getCouponById(Long id);
    CouponResponse updateCoupon(Long id, CouponRequest request);
    void deactivateCoupon(Long id);
    CouponResponse activateCoupon(Long id);
    void deleteCoupon(Long id);
    ApplyCouponResponse applyCoupon(ApplyCouponRequest request);
}