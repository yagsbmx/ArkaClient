package com.example.arkaorder.domain.ports.out;

import com.example.arkaorder.domain.enums.OrderStatus;
import com.example.arkaorder.domain.model.Order;

import java.util.List;
import java.util.Optional;

public interface OrderRepositoryPort {

    List<Order> findAll();

    Optional<Order> findById(Long id);

    Order save(Order order);

    Order update(Long id, Order order);

    void deleteById(Long id);

    List<Order> findByStatus(OrderStatus status);
}
