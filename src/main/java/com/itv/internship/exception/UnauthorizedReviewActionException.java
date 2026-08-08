package com.itv.internship.exception;

public class UnauthorizedReviewActionException extends RuntimeException {
    public UnauthorizedReviewActionException(String message) {
        super(message);
    }
}