package com.example.arkaorder.domain.ports.in;

import com.example.arkaorder.domain.model.OrderItem;

import java.util.List;
import java.util.Optional;

public interface OrderItemUseCase {

    OrderItem create(OrderItem orderItem);

    Optional<OrderItem> getById(Long id);

    List<OrderItem> list();

    OrderItem update(Long id, OrderItem orderItem);

    void delete(Long id);
}
