package com.itv.internship.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class OrderRequest {

    @NotBlank(message = "Customer name is required")
    @Size(max = 150, message = "Customer name must be at most 150 characters")
    private String customerName;

    @Size(max = 150, message = "Email must be at most 150 characters")
    private String customerEmail;

    @Size(max = 300, message = "Shipping address must be at most 300 characters")
    private String shippingAddress;

    @NotEmpty(message = "An order must contain at least one item")
    @Valid
    private List<OrderItemRequest> items;
}