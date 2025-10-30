package com.example.arkaorder.infraestructure.mapper;

import com.example.arkaorder.domain.model.Order;
import com.example.arkaorder.infraestructure.adapter.persistence.entity.OrderEntity;
import com.example.arkaorder.infraestructure.adapter.persistence.entity.OrderItemEntity;
import com.example.arkaorder.infraestructure.dto.OrderRequestDto;
import com.example.arkaorder.infraestructure.dto.OrderResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
@RequiredArgsConstructor
public class OrderMapper {

    private final OrderItemMapper orderItemMapper;

    public OrderEntity toEntity(Order order) {
        if (order == null) return null;

        OrderEntity entity = OrderEntity.builder()
                .id(order.getId())
                .clientId(order.getClientId())
                .status(order.getStatus())
                .totalAmount(order.getTotalAmount())
                .orderDate(order.getOrderDate())
                .build();

        List<OrderItemEntity> itemEntities =
                orderItemMapper.toEntity(order.getItems());
        if (itemEntities != null) {
            itemEntities.forEach(it -> it.setOrder(entity));
        }
        entity.setItems(itemEntities);

        return entity;
    }

    public Order toDomain(OrderEntity entity) {
        if (entity == null) return null;

        return Order.builder()
                .id(entity.getId())
                .clientId(entity.getClientId())
                .items(safeDomainItems(entity))
                .status(entity.getStatus())
                .totalAmount(entity.getTotalAmount())
                .orderDate(entity.getOrderDate())
                .build();
    }

    private List<com.example.arkaorder.domain.model.OrderItem> safeDomainItems(OrderEntity entity) {
        var items = entity.getItems();
        return (items == null || items.isEmpty())
                ? Collections.emptyList()
                : orderItemMapper.toDomain(items);
    }

    public Order requestToDomain(OrderRequestDto dto) {
        if (dto == null) return null;

        var items = (dto.getItems() == null || dto.getItems().isEmpty())
                ? Collections.<com.example.arkaorder.domain.model.OrderItem>emptyList()
                : orderItemMapper.requestToDomain(dto.getItems());

        return Order.builder()
                .clientId(dto.getClientId())
                .items(items)
                .build();
    }

    public OrderResponseDto toResponseDto(Order order) {
        if (order == null) return null;

        return OrderResponseDto.builder()
                .id(order.getId())
                .clientId(order.getClientId())
                .status(order.getStatus() != null ? order.getStatus().name() : null)
                .totalAmount(order.getTotalAmount())
                .createdAt(order.getOrderDate()) 
                .build();
    }
}
