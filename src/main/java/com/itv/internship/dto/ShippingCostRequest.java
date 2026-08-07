package com.itv.internship.dto;

import com.itv.internship.entity.DeliveryZone;
import com.itv.internship.entity.ShippingMethod;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ShippingCostRequest {

    @NotNull(message = "Weight is required")
    @Positive(message = "Weight must be greater than 0")
    private Double weightKg;

    @NotNull(message = "Delivery zone is required")
    private DeliveryZone deliveryZone;

    @NotNull(message = "Shipping method is required")
    private ShippingMethod shippingMethod;
}