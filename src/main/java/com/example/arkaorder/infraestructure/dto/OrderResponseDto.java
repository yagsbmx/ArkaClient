package com.example.arkaorder.infraestructure.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class OrderResponseDto {
    private Long id;
    private Long clientId;
    private String status;
    private BigDecimal totalAmount;
    private LocalDateTime createdAt;
}


