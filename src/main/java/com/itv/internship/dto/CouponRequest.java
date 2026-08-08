package com.itv.internship.dto;

import com.itv.internship.entity.DiscountType;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class CouponRequest {

    @NotBlank(message = "Coupon code is required")
    @Size(max = 50, message = "Coupon code must be at most 50 characters")
    private String couponCode;

    @NotNull(message = "Discount type is required")
    private DiscountType discountType;

    @NotNull(message = "Discount value is required")
    @Positive(message = "Discount value must be greater than 0")
    private BigDecimal discountValue;

    @NotNull(message = "Valid-from date is required")
    private LocalDateTime validFrom;

    @NotNull(message = "Valid-to date is required")
    private LocalDateTime validTo;

    @Positive(message = "Usage limit must be greater than 0")
    private Integer usageLimit;

    @PositiveOrZero(message = "Minimum order amount cannot be negative")
    private BigDecimal minOrderAmount;

    private Long applicableCategoryId;

    private Long applicableProductId;
}