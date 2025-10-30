package com.example.arkaorder.infraestructure.adapter.out.cartclient;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class CartFeignResponseDto {
    private Long id;
    private Long userId;
    private List<Long> productIds;
    private BigDecimal totalPrice;
    private String status;
}
