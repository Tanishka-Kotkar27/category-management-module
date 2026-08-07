package com.itv.internship.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ShippingCreateRequest {

    @NotNull(message = "Order is required")
    private Long orderId;

    @Size(max = 100, message = "Courier service must be at most 100 characters")
    private String courierService;

    @Size(max = 100, message = "Tracking number must be at most 100 characters")
    private String trackingNumber;

    @NotNull(message = "Shipping cost is required")
    @PositiveOrZero(message = "Shipping cost cannot be negative")
    private BigDecimal shippingCost;
}