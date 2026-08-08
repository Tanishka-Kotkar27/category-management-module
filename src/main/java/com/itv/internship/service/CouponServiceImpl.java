package com.itv.internship.service;

import com.itv.internship.dto.*;
import com.itv.internship.entity.Category;
import com.itv.internship.entity.Coupon;
import com.itv.internship.entity.DiscountType;
import com.itv.internship.entity.Product;
import com.itv.internship.exception.DuplicateCouponCodeException;
import com.itv.internship.exception.InvalidCouponException;
import com.itv.internship.exception.ResourceNotFoundException;
import com.itv.internship.repository.CategoryRepository;
import com.itv.internship.repository.CouponRepository;
import com.itv.internship.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class CouponServiceImpl implements CouponService {

    private final CouponRepository couponRepository;
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    @Override
    @Transactional
    public CouponResponse createCoupon(CouponRequest request) {
        if (couponRepository.existsByCouponCodeIgnoreCase(request.getCouponCode())) {
            throw new DuplicateCouponCodeException(
                    "Coupon code '" + request.getCouponCode() + "' already exists");
        }
        if (request.getValidTo().isBefore(request.getValidFrom())) {
            throw new InvalidCouponException("Valid-to date must be after valid-from date");
        }

        Coupon coupon = new Coupon();
        applyRequestToEntity(coupon, request);
        coupon.setStatus(true);
        coupon.setTimesUsed(0);

        Coupon saved = couponRepository.save(coupon);
        return toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CouponResponse> getAllCoupons() {
        return couponRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public CouponResponse getCouponById(Long id) {
        return toResponse(findCouponOrThrow(id));
    }

    @Override
    @Transactional
    public CouponResponse updateCoupon(Long id, CouponRequest request) {
        Coupon coupon = findCouponOrThrow(id);

        if (!coupon.getCouponCode().equalsIgnoreCase(request.getCouponCode())
                && couponRepository.existsByCouponCodeIgnoreCase(request.getCouponCode())) {
            throw new DuplicateCouponCodeException(
                    "Coupon code '" + request.getCouponCode() + "' already exists");
        }
        if (request.getValidTo().isBefore(request.getValidFrom())) {
            throw new InvalidCouponException("Valid-to date must be after valid-from date");
        }

        applyRequestToEntity(coupon, request);

        Coupon updated = couponRepository.save(coupon);
        return toResponse(updated);
    }

    @Override
    @Transactional
    public void deactivateCoupon(Long id) {
        Coupon coupon = findCouponOrThrow(id);
        coupon.setStatus(false);
        couponRepository.save(coupon);
    }

    @Override
    @Transactional
    public CouponResponse activateCoupon(Long id) {
        Coupon coupon = findCouponOrThrow(id);
        coupon.setStatus(true);
        return toResponse(couponRepository.save(coupon));
    }

    @Override
    @Transactional
    public void deleteCoupon(Long id) {
        Coupon coupon = findCouponOrThrow(id);
        couponRepository.delete(coupon);
    }

    @Override
    @Transactional
    public ApplyCouponResponse applyCoupon(ApplyCouponRequest request) {
        Coupon coupon = couponRepository.findByCouponCodeIgnoreCase(request.getCouponCode())
                .orElseThrow(() -> new InvalidCouponException(
                        "Coupon code '" + request.getCouponCode() + "' does not exist"));

        LocalDateTime now = LocalDateTime.now();

        if (!coupon.getStatus()) {
            throw new InvalidCouponException("This coupon is no longer active");
        }
        if (now.isBefore(coupon.getValidFrom())) {
            throw new InvalidCouponException("This coupon is not valid yet");
        }
        if (now.isAfter(coupon.getValidTo())) {
            throw new InvalidCouponException("This coupon has expired");
        }
        if (coupon.getUsageLimit() != null && coupon.getTimesUsed() >= coupon.getUsageLimit()) {
            throw new InvalidCouponException("This coupon has reached its usage limit");
        }
        if (coupon.getMinOrderAmount() != null
                && request.getOrderTotal().compareTo(coupon.getMinOrderAmount()) < 0) {
            throw new InvalidCouponException(
                    "This coupon requires a minimum order total of $" + coupon.getMinOrderAmount());
        }
        if (coupon.getApplicableCategory() != null
                && !Objects.equals(coupon.getApplicableCategory().getCategoryId(), request.getCategoryId())) {
            throw new InvalidCouponException(
                    "This coupon only applies to items in the '"
                            + coupon.getApplicableCategory().getCategoryName() + "' category");
        }
        if (coupon.getApplicableProduct() != null
                && !Objects.equals(coupon.getApplicableProduct().getProductId(), request.getProductId())) {
            throw new InvalidCouponException(
                    "This coupon only applies to '" + coupon.getApplicableProduct().getProductName() + "'");
        }

        BigDecimal discountAmount = coupon.getDiscountType() == DiscountType.PERCENTAGE
                ? request.getOrderTotal()
                        .multiply(coupon.getDiscountValue())
                        .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP)
                : coupon.getDiscountValue();

        // never let discount exceed the order total
        if (discountAmount.compareTo(request.getOrderTotal()) > 0) {
            discountAmount = request.getOrderTotal();
        }

        BigDecimal finalTotal = request.getOrderTotal().subtract(discountAmount);

        coupon.setTimesUsed(coupon.getTimesUsed() + 1);
        couponRepository.save(coupon);

        return new ApplyCouponResponse(
                coupon.getCouponCode(),
                request.getOrderTotal(),
                discountAmount,
                finalTotal
        );
    }

    private void applyRequestToEntity(Coupon coupon, CouponRequest request) {
        coupon.setCouponCode(request.getCouponCode());
        coupon.setDiscountType(request.getDiscountType());
        coupon.setDiscountValue(request.getDiscountValue());
        coupon.setValidFrom(request.getValidFrom());
        coupon.setValidTo(request.getValidTo());
        coupon.setUsageLimit(request.getUsageLimit());
        coupon.setMinOrderAmount(request.getMinOrderAmount());

        if (request.getApplicableCategoryId() != null) {
            Category category = categoryRepository.findById(request.getApplicableCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Category not found with id: " + request.getApplicableCategoryId()));
            coupon.setApplicableCategory(category);
        } else {
            coupon.setApplicableCategory(null);
        }

        if (request.getApplicableProductId() != null) {
            Product product = productRepository.findById(request.getApplicableProductId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Product not found with id: " + request.getApplicableProductId()));
            coupon.setApplicableProduct(product);
        } else {
            coupon.setApplicableProduct(null);
        }
    }

    private Coupon findCouponOrThrow(Long id) {
        return couponRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Coupon not found with id: " + id));
    }

    private String computeState(Coupon coupon) {
        if (!coupon.getStatus()) return "INACTIVE";
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(coupon.getValidFrom())) return "UPCOMING";
        if (now.isAfter(coupon.getValidTo())) return "EXPIRED";
        if (coupon.getUsageLimit() != null && coupon.getTimesUsed() >= coupon.getUsageLimit()) return "EXHAUSTED";
        return "ACTIVE";
    }

    private CouponResponse toResponse(Coupon coupon) {
        return new CouponResponse(
                coupon.getCouponId(),
                coupon.getCouponCode(),
                coupon.getDiscountType(),
                coupon.getDiscountValue(),
                coupon.getValidFrom(),
                coupon.getValidTo(),
                coupon.getUsageLimit(),
                coupon.getTimesUsed(),
                coupon.getMinOrderAmount(),
                coupon.getApplicableCategory() != null ? coupon.getApplicableCategory().getCategoryId() : null,
                coupon.getApplicableCategory() != null ? coupon.getApplicableCategory().getCategoryName() : null,
                coupon.getApplicableProduct() != null ? coupon.getApplicableProduct().getProductId() : null,
                coupon.getApplicableProduct() != null ? coupon.getApplicableProduct().getProductName() : null,
                coupon.getStatus(),
                computeState(coupon),
                coupon.getCreatedAt(),
                coupon.getUpdatedAt()
        );
    }
}