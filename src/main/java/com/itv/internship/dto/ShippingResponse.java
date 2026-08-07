package com.itv.internship.dto;

import com.itv.internship.entity.ShippingStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class ShippingResponse {
    private Long shippingId;
    private Long orderId;
    private String customerName;
    private String shippingAddress;
    private String courierService;
    private String trackingNumber;
    private ShippingStatus shippingStatus;
    private BigDecimal shippingCost;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}