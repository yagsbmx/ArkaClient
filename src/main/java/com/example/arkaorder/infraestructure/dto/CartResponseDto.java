package com.example.arkaorder.infraestructure.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartResponseDto {
    private Long id;
    private Long userId;
    private String status;
    private Double totalPrice;
    private List<CartItemDto> items;
}