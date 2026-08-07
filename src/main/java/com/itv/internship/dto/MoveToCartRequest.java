package com.itv.internship.dto;

import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MoveToCartRequest {

    @Positive(message = "Quantity must be greater than 0")
    private Integer quantity = 1;
}