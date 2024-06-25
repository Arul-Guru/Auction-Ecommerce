package com.auction.ecommerce.exception;

public class DuplicateCategoryException extends RuntimeException {
    // Constructor that accepts a message
    public DuplicateCategoryException(String message) {
        super(message);
    }
}
