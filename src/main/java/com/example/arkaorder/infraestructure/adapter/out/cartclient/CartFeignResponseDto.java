package com.example.arkaorder.infraestructure.adapter.out.cartclient;

import java.math.BigDecimal;
import java.util.List;

public record CartFeignResponseDto(
        Long id,
        Long userId,
        String status,
        List<Item> items,
        BigDecimal totalPrice
) {
    public record Item(
            Long id,
            Long productId,
            String name,
            Integer quantity,
            BigDecimal price,
            Long cartId
    ) {}
}
