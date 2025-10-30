package com.example.arkaorder.domain.ports.out;

import java.math.BigDecimal;

public interface ProductPort {
    boolean isAvailable(Long productId, int quantity);
    String reserve(Long productId, int quantity, long ttlSeconds);
    void commitReservation(String reservationId);
    void releaseReservation(String reservationId);
    void decrementStock(Long productId, int quantity);
    void incrementStock(Long productId, int quantity);
    BigDecimal getPrice(Long productId);
}

