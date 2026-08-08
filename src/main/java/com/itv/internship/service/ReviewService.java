package com.itv.internship.service;

import com.itv.internship.dto.*;

import java.util.List;

public interface ReviewService {
    ReviewResponse addReview(ReviewRequest request);
    List<ReviewResponse> getApprovedReviewsForProduct(Long productId);
    List<ReviewResponse> getAllReviewsForProduct(Long productId);
    List<ReviewResponse> getAllReviews();
    ProductRatingSummary getProductRatingSummary(Long productId);
    ReviewResponse updateReview(Long reviewId, Long customerId, ReviewUpdateRequest request);
    void deleteReviewByCustomer(Long reviewId, Long customerId);
    ReviewResponse approveReview(Long reviewId);
    ReviewResponse rejectReview(Long reviewId);
    void deleteReviewByAdmin(Long reviewId);
}