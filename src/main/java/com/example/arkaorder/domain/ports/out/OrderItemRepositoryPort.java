package com.example.arkaorder.domain.ports.out;

import com.example.arkaorder.domain.model.OrderItem;

import java.util.List;
import java.util.Optional;

public interface OrderItemRepositoryPort {

    List<OrderItem> findAll();

    Optional<OrderItem> findById(Long id);

    OrderItem save(OrderItem orderItem);

    OrderItem update(Long id, OrderItem orderItem);

    void deleteById(Long id);
}
