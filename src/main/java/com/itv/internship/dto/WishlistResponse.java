package com.itv.internship.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class WishlistResponse {
    private Long wishlistId;
    private Long customerId;
    private String customerName;
    private Long productId;
    private String productName;
    private BigDecimal price;
    private boolean inStock;
    private Integer availableQuantity;
    private LocalDateTime createdAt;
}