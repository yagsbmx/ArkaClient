package com.example.arkaorder.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItem {

    private Long id;

    private Long productId;

    private Integer quantity;

    private BigDecimal unitPrice; 

    private BigDecimal totalPrice;

    private String reservationId;

}
