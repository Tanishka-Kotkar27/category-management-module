package com.itv.internship.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ApplyCouponRequest {

    @NotBlank(message = "Coupon code is required")
    private String couponCode;

    @NotNull(message = "Order total is required")
    @Positive(message = "Order total must be greater than 0")
    private BigDecimal orderTotal;

    private Long categoryId;

    private Long productId;
}