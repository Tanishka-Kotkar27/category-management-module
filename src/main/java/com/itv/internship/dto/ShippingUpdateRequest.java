package com.itv.internship.dto;

import com.itv.internship.entity.ShippingStatus;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ShippingUpdateRequest {

    @Size(max = 100, message = "Courier service must be at most 100 characters")
    private String courierService;

    @Size(max = 100, message = "Tracking number must be at most 100 characters")
    private String trackingNumber;

    private ShippingStatus shippingStatus;
}