package com.itv.internship.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class ReviewResponse {
    private Long reviewId;
    private Long productId;
    private String productName;
    private Long customerId;
    private String customerName;
    private Integer rating;
    private String reviewText;
    private Boolean status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}