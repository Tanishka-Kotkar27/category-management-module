package com.itv.internship.exception;

public class DuplicateCouponCodeException extends RuntimeException {
    public DuplicateCouponCodeException(String message) {
        super(message);
    }
}