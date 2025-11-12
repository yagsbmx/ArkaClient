package com.example.arkaorder.infraestructure.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import java.util.List;

@Data
public class OrderRequestDto {

    @NotNull(message = "clientId es obligatorio")
    @Positive(message = "clientId debe ser > 0")
    private Long clientId;

    @Valid
    @NotEmpty(message = "La orden debe tener al menos 1 ítem")
    private List<OrderItemRequestDto> items;
}

