package com.example.arkaorder.domain.exceptions;

public class InsufficientStockException extends RuntimeException {
    public InsufficientStockException(Long productId, int requested) {
        super("Insufficient stock for product id: " + productId + " (requested: " + requested + ")");
    }

    public InsufficientStockException(String message) {
        super(message);
    }

    public InsufficientStockException(String message, Throwable cause) {
        super(message, cause);
    }
}
