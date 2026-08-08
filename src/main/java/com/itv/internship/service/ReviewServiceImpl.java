package com.itv.internship.service;

import com.itv.internship.dto.*;
import com.itv.internship.entity.Product;
import com.itv.internship.entity.Review;
import com.itv.internship.entity.User;
import com.itv.internship.exception.*;
import com.itv.internship.repository.OrderItemRepository;
import com.itv.internship.repository.ProductRepository;
import com.itv.internship.repository.ReviewRepository;
import com.itv.internship.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final OrderItemRepository orderItemRepository;

    @Override
    @Transactional
    public ReviewResponse addReview(ReviewRequest request) {
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Product not found with id: " + request.getProductId()));

        User customer = userRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Customer not found with id: " + request.getCustomerId()));

        boolean purchased = orderItemRepository.existsByOrder_Customer_UserIdAndProduct_ProductId(
                request.getCustomerId(), request.getProductId());
        if (!purchased) {
            throw new ProductNotPurchasedException(
                    "You can only review products you have purchased");
        }

        if (reviewRepository.existsByCustomer_UserIdAndProduct_ProductId(
                request.getCustomerId(), request.getProductId())) {
            throw new DuplicateReviewException(
                    "You have already reviewed this product. You can edit your existing review instead.");
        }

        Review review = new Review();
        review.setProduct(product);
        review.setCustomer(customer);
        review.setRating(request.getRating());
        review.setReviewText(request.getReviewText());
        review.setStatus(false); // requires admin approval before showing publicly

        Review saved = reviewRepository.save(review);
        return toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReviewResponse> getApprovedReviewsForProduct(Long productId) {
        return reviewRepository.findByProduct_ProductIdAndStatusTrue(productId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReviewResponse> getAllReviewsForProduct(Long productId) {
        return reviewRepository.findByProduct_ProductId(productId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReviewResponse> getAllReviews() {
        return reviewRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ProductRatingSummary getProductRatingSummary(Long productId) {
        List<Review> approvedReviews = reviewRepository.findByProduct_ProductIdAndStatusTrue(productId);
        double average = approvedReviews.stream()
                .mapToInt(Review::getRating)
                .average()
                .orElse(0.0);
        return new ProductRatingSummary(productId, Math.round(average * 10) / 10.0, approvedReviews.size());
    }

    @Override
    @Transactional
    public ReviewResponse updateReview(Long reviewId, Long customerId, ReviewUpdateRequest request) {
        Review review = findReviewOrThrow(reviewId);

        if (!review.getCustomer().getUserId().equals(customerId)) {
            throw new UnauthorizedReviewActionException("You can only edit your own reviews");
        }

        review.setRating(request.getRating());
        review.setReviewText(request.getReviewText());
        review.setStatus(false); // re-moderation after edit

        Review updated = reviewRepository.save(review);
        return toResponse(updated);
    }

    @Override
    @Transactional
    public void deleteReviewByCustomer(Long reviewId, Long customerId) {
        Review review = findReviewOrThrow(reviewId);

        if (!review.getCustomer().getUserId().equals(customerId)) {
            throw new UnauthorizedReviewActionException("You can only delete your own reviews");
        }

        reviewRepository.delete(review);
    }

    @Override
    @Transactional
    public ReviewResponse approveReview(Long reviewId) {
        Review review = findReviewOrThrow(reviewId);
        review.setStatus(true);
        return toResponse(reviewRepository.save(review));
    }

    @Override
    @Transactional
    public ReviewResponse rejectReview(Long reviewId) {
        Review review = findReviewOrThrow(reviewId);
        review.setStatus(false);
        return toResponse(reviewRepository.save(review));
    }

    @Override
    @Transactional
    public void deleteReviewByAdmin(Long reviewId) {
        Review review = findReviewOrThrow(reviewId);
        reviewRepository.delete(review);
    }

    private Review findReviewOrThrow(Long reviewId) {
        return reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found with id: " + reviewId));
    }

    private ReviewResponse toResponse(Review review) {
        return new ReviewResponse(
                review.getReviewId(),
                review.getProduct().getProductId(),
                review.getProduct().getProductName(),
                review.getCustomer().getUserId(),
                review.getCustomer().getFullName(),
                review.getRating(),
                review.getReviewText(),
                review.getStatus(),
                review.getCreatedAt(),
                review.getUpdatedAt()
        );
    }
}