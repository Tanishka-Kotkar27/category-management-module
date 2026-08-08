package com.itv.internship.controller;

import com.itv.internship.dto.*;
import com.itv.internship.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    // Customer adds a review (must have purchased the product)
    @PostMapping
    public ResponseEntity<ReviewResponse> addReview(@Valid @RequestBody ReviewRequest request) {
        ReviewResponse response = reviewService.addReview(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // Product page: only approved reviews
    @GetMapping("/product/{productId}")
    public ResponseEntity<List<ReviewResponse>> getApprovedReviewsForProduct(@PathVariable Long productId) {
        return ResponseEntity.ok(reviewService.getApprovedReviewsForProduct(productId));
    }

    // Admin: all reviews for a product (including unapproved)
    @GetMapping("/product/{productId}/all")
    public ResponseEntity<List<ReviewResponse>> getAllReviewsForProduct(@PathVariable Long productId) {
        return ResponseEntity.ok(reviewService.getAllReviewsForProduct(productId));
    }

    // Average rating + count for a product
    @GetMapping("/product/{productId}/summary")
    public ResponseEntity<ProductRatingSummary> getRatingSummary(@PathVariable Long productId) {
        return ResponseEntity.ok(reviewService.getProductRatingSummary(productId));
    }

    // Admin moderation dashboard: all reviews across all products
    @GetMapping
    public ResponseEntity<List<ReviewResponse>> getAllReviews() {
        return ResponseEntity.ok(reviewService.getAllReviews());
    }

    // Customer updates their own review (?customerId=1 identifies the requester)
    @PutMapping("/{reviewId}")
    public ResponseEntity<ReviewResponse> updateReview(
            @PathVariable Long reviewId,
            @RequestParam Long customerId,
            @Valid @RequestBody ReviewUpdateRequest request) {
        return ResponseEntity.ok(reviewService.updateReview(reviewId, customerId, request));
    }

    // Customer deletes their own review
    @DeleteMapping("/{reviewId}")
    public ResponseEntity<Map<String, String>> deleteOwnReview(
            @PathVariable Long reviewId,
            @RequestParam Long customerId) {
        reviewService.deleteReviewByCustomer(reviewId, customerId);
        return ResponseEntity.ok(Map.of("message", "Review deleted successfully"));
    }

    // Admin approves a review
    @PatchMapping("/{reviewId}/approve")
    public ResponseEntity<ReviewResponse> approveReview(@PathVariable Long reviewId) {
        return ResponseEntity.ok(reviewService.approveReview(reviewId));
    }

    // Admin rejects a review
    @PatchMapping("/{reviewId}/reject")
    public ResponseEntity<ReviewResponse> rejectReview(@PathVariable Long reviewId) {
        return ResponseEntity.ok(reviewService.rejectReview(reviewId));
    }

    // Admin deletes any inappropriate review
    @DeleteMapping("/{reviewId}/admin")
    public ResponseEntity<Map<String, String>> deleteReviewByAdmin(@PathVariable Long reviewId) {
        reviewService.deleteReviewByAdmin(reviewId);
        return ResponseEntity.ok(Map.of("message", "Review deleted by admin"));
    }
}