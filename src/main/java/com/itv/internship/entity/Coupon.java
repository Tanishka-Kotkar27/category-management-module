package com.itv.internship.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "coupons")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Coupon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "coupon_id")
    private Long couponId;

    @NotBlank(message = "Coupon code is required")
    @Size(max = 50, message = "Coupon code must be at most 50 characters")
    @Column(name = "coupon_code", nullable = false, unique = true, length = 50)
    private String couponCode;

    @Enumerated(EnumType.STRING)
    @Column(name = "discount_type", nullable = false, length = 50)
    private DiscountType discountType;

    @NotNull(message = "Discount value is required")
    @Positive(message = "Discount value must be greater than 0")
    @Column(name = "discount_value", nullable = false, precision = 10, scale = 2)
    private BigDecimal discountValue;

    @NotNull(message = "Valid-from date is required")
    @Column(name = "valid_from", nullable = false)
    private LocalDateTime validFrom;

    @NotNull(message = "Valid-to date is required")
    @Column(name = "valid_to", nullable = false)
    private LocalDateTime validTo;

    @Positive(message = "Usage limit must be greater than 0")
    @Column(name = "usage_limit")
    private Integer usageLimit;

    @Column(name = "times_used", nullable = false)
    private Integer timesUsed = 0;

    @PositiveOrZero(message = "Minimum order amount cannot be negative")
    @Column(name = "min_order_amount", precision = 10, scale = 2)
    private BigDecimal minOrderAmount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "applicable_category_id")
    private Category applicableCategory;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "applicable_product_id")
    private Product applicableProduct;

    @Column(name = "status", nullable = false)
    private Boolean status = true;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (this.status == null) this.status = true;
        if (this.timesUsed == null) this.timesUsed = 0;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}