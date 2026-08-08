package com.itv.internship.dto;

import com.itv.internship.entity.DiscountType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class CouponResponse {
    private Long couponId;
    private String couponCode;
    private DiscountType discountType;
    private BigDecimal discountValue;
    private LocalDateTime validFrom;
    private LocalDateTime validTo;
    private Integer usageLimit;
    private Integer timesUsed;
    private BigDecimal minOrderAmount;
    private Long applicableCategoryId;
    private String applicableCategoryName;
    private Long applicableProductId;
    private String applicableProductName;
    private Boolean status;
    private String computedState; // ACTIVE, EXPIRED, UPCOMING, EXHAUSTED, INACTIVE
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}