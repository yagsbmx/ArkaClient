package com.example.arkaorder.domain.enums;

public enum OrderStatus {
    CONFIRMED,
    DISPATCHED,
    SHIPPED,
    DELIVERED,
    EXPIRED,
    RESERVED,
    CANCELLED,
    PENDING_PAYMENT;

   
    public static OrderStatus fromString(String value) {
        return OrderStatus.valueOf(value.toUpperCase());
    }
}
