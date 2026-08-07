package com.itv.internship.exception;

public class ShippingAlreadyExistsException extends RuntimeException {
    public ShippingAlreadyExistsException(String message) {
        super(message);
    }
}