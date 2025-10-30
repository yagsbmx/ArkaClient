package com.example.arkaorder.domain.ports.in;

import com.example.arkaorder.domain.enums.OrderStatus;
import com.example.arkaorder.domain.model.Order;

import java.util.List;
import java.util.Optional;

public interface OrderUseCase {

    Order createOrder(Order order);

    Optional<Order> getOrderById(Long id);

    List<Order> list();

    Order updateOrder(Long id, Order order);

    void deleteOrder(Long id);

    List<Order> findByStatus(OrderStatus status);

    Order updateStatus(Long id, OrderStatus status);

}


