package com.example.arkaorder.domain.model;

import com.example.arkaorder.domain.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {

    private Long id;

    private Long clientId;

    private OrderStatus status;

    private List<OrderItem> items;

    private BigDecimal totalAmount;
    
    private LocalDateTime orderDate;
}
