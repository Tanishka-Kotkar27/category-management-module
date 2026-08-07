package com.itv.internship.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
public class ShippingCostResponse {
    private BigDecimal shippingCost;
    private String breakdown;
}