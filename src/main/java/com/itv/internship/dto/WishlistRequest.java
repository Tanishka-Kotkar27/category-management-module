package com.itv.internship.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WishlistRequest {

    @NotNull(message = "Customer is required")
    private Long customerId;

    @NotNull(message = "Product is required")
    private Long productId;
}