package com.example.arkaorder.application.service;

import com.example.arkaorder.domain.enums.OrderStatus;
import com.example.arkaorder.domain.model.Order;
import com.example.arkaorder.domain.model.OrderItem;
import com.example.arkaorder.domain.ports.in.OrderUseCase;
import com.example.arkaorder.domain.ports.out.OrderRepositoryPort;
import com.example.arkaorder.domain.ports.out.ProductPort;
import com.example.arkaorder.domain.ports.out.UserPort;
import com.example.arkaorder.domain.ports.out.CartPort;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderService implements OrderUseCase {

    private final OrderRepositoryPort orders;
    private final ProductPort products;
    private final UserPort users;
    private final CartPort carts;

    @Override
    @Transactional
    public Order createOrder(final Order order) {
        if (order.getClientId() == null || !users.existsAndActive(order.getClientId())) {
            throw new IllegalArgumentException("User not found or inactive: " + order.getClientId());
        }

        computeTotals(order);
        order.setStatus(OrderStatus.PENDING);
        order.setOrderDate(LocalDateTime.now());

        for (OrderItem it : order.getItems()) {
            if (!products.isAvailable(it.getProductId(), it.getQuantity())) {
                throw new IllegalArgumentException("Insufficient stock for product " + it.getProductId());
            }
        }

        var createdOrder = orders.save(order);

        try {
            carts.completeCartByUserId(order.getClientId());
        } catch (Exception ex) {
            System.err.println(" No se pudo completar el carrito del usuario " + order.getClientId() + ": " + ex.getMessage());
        }

        return createdOrder;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Order> getOrderById(Long id) {
        return orders.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Order> list() {
        return orders.findAll();
    }

    @Override
    @Transactional
    public Order updateOrder(Long id, Order incoming) {
        Order current = orders.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Order not found: " + id));

        if (current.getStatus() != OrderStatus.PENDING) {
            throw new IllegalStateException("Only PENDING orders can be edited");
        }

        if (incoming.getItems() != null && !incoming.getItems().isEmpty()) {
            current.setItems(incoming.getItems());
            computeTotals(current);
        }

        return orders.update(id, current);
    }

    @Override
    @Transactional
    public void deleteOrder(Long id) {
        orders.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Order> findByStatus(OrderStatus status) {
        return orders.findByStatus(status);
    }

    private void computeTotals(Order order) {
        if (order.getItems() == null || order.getItems().isEmpty()) {
            throw new IllegalArgumentException("Order must contain at least one item");
        }

        BigDecimal total = BigDecimal.ZERO;
        for (OrderItem it : order.getItems()) {
            if (it.getQuantity() == null || it.getQuantity() <= 0) {
                throw new IllegalArgumentException("Quantity must be >= 1 for product " + it.getProductId());
            }
            if (it.getUnitPrice() == null) {
                it.setUnitPrice(products.getPrice(it.getProductId()));
            }
            BigDecimal line = it.getUnitPrice().multiply(BigDecimal.valueOf(it.getQuantity()));
            it.setTotalPrice(line);
            total = total.add(line);
        }
        order.setTotalAmount(total);
    }

    @Override
    @Transactional
    public Order updateStatus(Long id, OrderStatus newStatus) {
        Order current = orders.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Order not found: " + id));

        OrderStatus old = current.getStatus();
        if (old == newStatus) return current;
        if (old == OrderStatus.CANCELLED) throw new IllegalArgumentException("Cancelled orders cannot change status");

        if (old == OrderStatus.PENDING && newStatus == OrderStatus.CANCELLED) {
            current.setStatus(OrderStatus.CANCELLED);
            return orders.update(id, current);
        }

        if (old == OrderStatus.PENDING && newStatus == OrderStatus.CONFIRMED) {
            for (OrderItem it : current.getItems()) {
                products.decrementStock(it.getProductId(), it.getQuantity());
            }
            current.setStatus(OrderStatus.CONFIRMED);
            return orders.update(id, current);
        }

        if (old == OrderStatus.CONFIRMED && newStatus == OrderStatus.CANCELLED) {
            for (OrderItem it : current.getItems()) {
                products.incrementStock(it.getProductId(), it.getQuantity());
            }
            current.setStatus(OrderStatus.CANCELLED);
            return orders.update(id, current);
        }

        if (old == OrderStatus.CONFIRMED && newStatus == OrderStatus.SHIPPED) {
            current.setStatus(OrderStatus.SHIPPED);
            return orders.update(id, current);
        }

        if ((old == OrderStatus.CONFIRMED || old == OrderStatus.SHIPPED) && newStatus == OrderStatus.DELIVERED) {
            current.setStatus(OrderStatus.DELIVERED);
            return orders.update(id, current);
        }

        throw new IllegalStateException("Invalid status transition: " + old + " -> " + newStatus);
    }
}
