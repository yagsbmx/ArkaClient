package com.example.arkaorder.infraestructure.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class OrderRequestDto {
    @NotNull
    private Long clientId;

    @Valid
    @NotEmpty
    private List<OrderItemRequestDto> items;
}


