package com.example.arkaorder.domain.ports.out;

import java.util.List;

public interface CartPort {

    record CartItem(Long productId, String name, Integer quantity, Double price) {}
    record CartData(Long id, Long userId, String status, List<CartItem> items, Double totalPrice) {}

    CartData getCartByUserId(Long userId);
}
