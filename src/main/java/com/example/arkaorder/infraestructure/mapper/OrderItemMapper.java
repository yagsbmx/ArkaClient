package com.example.arkaorder.infraestructure.mapper;

import com.example.arkaorder.domain.model.OrderItem;
import com.example.arkaorder.infraestructure.adapter.persistence.entity.OrderItemEntity;
import com.example.arkaorder.infraestructure.dto.OrderItemRequestDto;
import com.example.arkaorder.infraestructure.dto.OrderItemResponseDto;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
public class OrderItemMapper {

    public OrderItem toDomain(OrderItemEntity entity) {
        if (entity == null) return null;
        return OrderItem.builder()
                .id(entity.getId())
                .productId(entity.getProductId())
                .quantity(entity.getQuantity())
                .unitPrice(entity.getUnitPrice())
                .totalPrice(entity.getTotalPrice())
                .build();
    }

    public List<OrderItem> toDomain(List<OrderItemEntity> listEntities) {
        if (listEntities == null || listEntities.isEmpty()) return Collections.emptyList();
        List<OrderItem> items = new ArrayList<>(listEntities.size());
        for (OrderItemEntity entity : listEntities) {
            items.add(toDomain(entity));
        }
        return items;
    }

    public OrderItemEntity toEntity(OrderItem item) {
        if (item == null) return null;
        BigDecimal unitPrice = item.getUnitPrice();
        Integer quantity = item.getQuantity();
        BigDecimal total = item.getTotalPrice();
        if (total == null && unitPrice != null && quantity != null) {
            total = unitPrice.multiply(BigDecimal.valueOf(quantity));
        }

        return OrderItemEntity.builder()
                .id(item.getId())
                .productId(item.getProductId())
                .quantity(quantity)
                .unitPrice(unitPrice)
                .totalPrice(total)
                .build();
    }

    public List<OrderItemEntity> toEntity(List<OrderItem> listItems) {
        if (listItems == null || listItems.isEmpty()) return Collections.emptyList();
        List<OrderItemEntity> entities = new ArrayList<>(listItems.size());
        for (OrderItem item : listItems) {
            entities.add(toEntity(item));
        }
        return entities;
    }

    public OrderItemResponseDto toResponseDto(OrderItem item) {
        if (item == null) return null;
        return new OrderItemResponseDto(
                item.getId(),
                item.getProductId(),
                item.getQuantity(),
                item.getUnitPrice(),
                item.getTotalPrice()
        );
    }

    public List<OrderItemResponseDto> toResponseDto(List<OrderItem> listItems) {
        if (listItems == null || listItems.isEmpty()) return Collections.emptyList();
        List<OrderItemResponseDto> dtos = new ArrayList<>(listItems.size());
        for (OrderItem item : listItems) {
            dtos.add(toResponseDto(item));
        }
        return dtos;
    }

    public OrderItem requestToDomain(OrderItemRequestDto dto) {
        if (dto == null) return null;
        BigDecimal unitPrice = dto.getUnitPrice(); 
        Integer quantity = dto.getQuantity();
        BigDecimal total = (unitPrice != null && quantity != null)
                ? unitPrice.multiply(BigDecimal.valueOf(quantity))
                : null;

        return OrderItem.builder()
                .productId(dto.getProductId())
                .quantity(quantity)
                .unitPrice(unitPrice)
                .totalPrice(total)
                .build();
    }

    public List<OrderItem> requestToDomain(List<OrderItemRequestDto> listDtos) {
        if (listDtos == null || listDtos.isEmpty()) return Collections.emptyList();
        List<OrderItem> items = new ArrayList<>(listDtos.size());
        for (OrderItemRequestDto dto : listDtos) {
            items.add(requestToDomain(dto));
        }
        return items;
    }
}
