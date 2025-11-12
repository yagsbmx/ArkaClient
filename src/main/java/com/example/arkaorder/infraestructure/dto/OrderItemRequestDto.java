package com.example.arkaorder.infraestructure.dto;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderItemRequestDto {

    @NotNull(message = "productId es obligatorio")
    @Positive(message = "productId debe ser > 0")
    private Long productId;

    @NotNull(message = "quantity es obligatorio")
    @Min(value = 1, message = "quantity debe ser >= 1")
    private Integer quantity;

    @Digits(integer = 12, fraction = 2, message = "unitPrice debe tener máx. 2 decimales")
    @Positive(message = "unitPrice (si se envía) debe ser > 0")
    private BigDecimal unitPrice;
}


