package com.itv.internship.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ProductRatingSummary {
    private Long productId;
    private double averageRating;
    private long totalReviews;
}