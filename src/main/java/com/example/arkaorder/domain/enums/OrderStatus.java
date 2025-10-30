package com.example.arkaorder.domain.enums;

public enum OrderStatus {
    PENDING,
    CONFIRMED,
    DISPATCHED,
    SHIPPED,
    DELIVERED,
    EXPIRED,
    RESERVED,
    CANCELLED;

   
    public static OrderStatus fromString(String value) {
        return OrderStatus.valueOf(value.toUpperCase());
    }
}
