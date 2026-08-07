package com.itv.internship.service;

import com.itv.internship.dto.ShippingCostRequest;
import com.itv.internship.dto.ShippingCostResponse;
import com.itv.internship.entity.DeliveryZone;
import com.itv.internship.entity.ShippingMethod;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
public class ShippingCostCalculator {

    private static final BigDecimal BASE_RATE = new BigDecimal("5.00");
    private static final BigDecimal PER_KG_RATE = new BigDecimal("1.50");

    public ShippingCostResponse calculate(ShippingCostRequest request) {
        BigDecimal weightCost = PER_KG_RATE.multiply(BigDecimal.valueOf(request.getWeightKg()));

        BigDecimal zoneMultiplier = zoneMultiplier(request.getDeliveryZone());
        BigDecimal methodMultiplier = methodMultiplier(request.getShippingMethod());

        BigDecimal cost = BASE_RATE.add(weightCost)
                .multiply(zoneMultiplier)
                .multiply(methodMultiplier)
                .setScale(2, RoundingMode.HALF_UP);

        String breakdown = String.format(
                "Base $%.2f + (%.2fkg × $%.2f/kg) = $%.2f, × %s zone (%.1fx), × %s method (%.1fx)",
                BASE_RATE, request.getWeightKg(), PER_KG_RATE, BASE_RATE.add(weightCost),
                request.getDeliveryZone(), zoneMultiplier,
                request.getShippingMethod(), methodMultiplier
        );

        return new ShippingCostResponse(cost, breakdown);
    }

    private BigDecimal zoneMultiplier(DeliveryZone zone) {
        return switch (zone) {
            case LOCAL -> new BigDecimal("1.0");
            case REGIONAL -> new BigDecimal("1.5");
            case NATIONAL -> new BigDecimal("2.0");
        };
    }

    private BigDecimal methodMultiplier(ShippingMethod method) {
        return switch (method) {
            case STANDARD -> new BigDecimal("1.0");
            case EXPRESS -> new BigDecimal("1.75");
            case OVERNIGHT -> new BigDecimal("2.5");
        };
    }
}