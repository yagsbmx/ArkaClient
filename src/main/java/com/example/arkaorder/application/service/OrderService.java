package com.example.arkaorder.application.service;

import com.example.arkaorder.domain.enums.OrderStatus;
import com.example.arkaorder.domain.model.Order;
import com.example.arkaorder.domain.model.OrderItem;
import com.example.arkaorder.domain.ports.in.OrderUseCase;
import com.example.arkaorder.domain.ports.out.CartPort;
import com.example.arkaorder.domain.ports.out.NotificationPort;
import com.example.arkaorder.domain.ports.out.OrderRepositoryPort;
import com.example.arkaorder.domain.ports.out.ProductPort;
import com.example.arkaorder.domain.ports.out.UserPort;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

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
    private final OrderHistoryRecorder history;
    private final CartPort carts;
    private final ApplicationEventPublisher eventPublisher;
    private final NotificationPort notificationPort;

    @Override
    @Transactional
    public Order createOrderFromCart(Long userId) {
        if (userId == null || userId <= 0)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "userId es obligatorio y debe ser > 0");

        var cart = carts.getCartByUserId(userId);
        if (cart == null || cart.items() == null || cart.items().isEmpty())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El carrito está vacío");

        var items = new java.util.ArrayList<OrderItem>();
        BigDecimal total = BigDecimal.ZERO;

        for (var it : cart.items()) {
            if (it == null || it.quantity() == null || it.quantity() < 1)
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cantidad inválida en el carrito");
            if (!products.isAvailable(it.productId(), it.quantity()))
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Stock insuficiente para producto " + it.productId());
            BigDecimal unit = products.getPrice(it.productId());
            items.add(OrderItem.builder()
                    .productId(it.productId())
                    .quantity(it.quantity())
                    .unitPrice(unit)
                    .build());
            total = total.add(unit.multiply(BigDecimal.valueOf(it.quantity())));
        }

        var order = Order.builder()
                .clientId(userId)
                .items(items)
                .status(OrderStatus.PENDING_PAYMENT)
                .totalAmount(total)
                .orderDate(LocalDateTime.now())
                .build();

        return orders.save(order);
    }


    @Override
    @Transactional
    public Order createOrder(Order order) {
        if (order == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El cuerpo de la orden es obligatorio");
        if (order.getClientId() == null || order.getClientId() <= 0)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "clientId es obligatorio y debe ser > 0");
        if (!users.existsAndActive(order.getClientId()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Usuario no existe o está inactivo: " + order.getClientId());
        validateItems(order.getItems());
        computeTotals(order);
        order.setStatus(OrderStatus.PENDING_PAYMENT);
        order.setOrderDate(LocalDateTime.now());
        for (OrderItem it : order.getItems())
            if (!products.isAvailable(it.getProductId(), it.getQuantity()))
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Stock insuficiente para producto " + it.getProductId());
        return orders.save(order);
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
        if (current.getStatus() != OrderStatus.PENDING_PAYMENT)
            throw new IllegalStateException("Only PENDING_PAYMENT orders can be edited");
        if (incoming == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El cuerpo de la orden es obligatorio");
        if (incoming.getClientId() != null && incoming.getClientId() <= 0)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "clientId (si viene) debe ser > 0");
        if (incoming.getItems() != null && !incoming.getItems().isEmpty()) {
            validateItems(incoming.getItems());
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

    @Override
    @Transactional
    public Order updateStatus(Long id, OrderStatus newStatus) {
        Order current = orders.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Order not found: " + id));
        OrderStatus old = current.getStatus();
        if (old == newStatus) return current;
        if (old == OrderStatus.CANCELLED) throw new IllegalArgumentException("Cancelled orders cannot change status");
        if (old == OrderStatus.PENDING_PAYMENT && newStatus == OrderStatus.CANCELLED) {
            current.setStatus(OrderStatus.CANCELLED);
            return updateAndNotify(id, current, old, newStatus);
        }
        if (old == OrderStatus.PENDING_PAYMENT && newStatus == OrderStatus.CONFIRMED) {
            for (OrderItem it : current.getItems())
                products.decrementStock(it.getProductId(), it.getQuantity());
            current.setStatus(OrderStatus.CONFIRMED);
            return updateAndNotify(id, current, old, newStatus);
        }
        if (old == OrderStatus.CONFIRMED && newStatus == OrderStatus.CANCELLED) {
            for (OrderItem it : current.getItems())
                products.incrementStock(it.getProductId(), it.getQuantity());
            current.setStatus(OrderStatus.CANCELLED);
            return updateAndNotify(id, current, old, newStatus);
        }
        if (old == OrderStatus.CONFIRMED && newStatus == OrderStatus.SHIPPED) {
            current.setStatus(OrderStatus.SHIPPED);
            return updateAndNotify(id, current, old, newStatus);
        }
        if (old == OrderStatus.SHIPPED && newStatus == OrderStatus.DELIVERED) {
            current.setStatus(OrderStatus.DELIVERED);
            return updateAndNotify(id, current, old, newStatus);
        }
        throw new IllegalStateException("Invalid status transition: " + old + " -> " + newStatus);
    }

    private Order updateAndNotify(Long id, Order current, OrderStatus old, OrderStatus newStatus) {
        Order updated = orders.update(id, current);
        publishStatusChangeEvent(updated, old, newStatus);
        try {
            String toEmail = users.getEmailById(updated.getClientId());
            notificationPort.notifyOrderStatusChange(updated, old, newStatus, toEmail);
        } catch (Exception ignored) {}
        try { history.record(updated.getId(), "UPDATE", "Orden actualizada"); } catch (Exception ignored) {}
        return updated;
    }

    private void publishStatusChangeEvent(Order order, OrderStatus oldStatus, OrderStatus newStatus) {
        try {
            eventPublisher.publishEvent(new OrderStatusChangedEvent(
                    order.getId(),
                    order.getClientId(),
                    oldStatus,
                    newStatus,
                    order.getTotalAmount(),
                    order.getOrderDate()
            ));
        } catch (Exception ignored) {}
    }

    private void computeTotals(Order order) {
        if (order.getItems() == null || order.getItems().isEmpty())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La orden debe contener al menos un ítem");
        BigDecimal total = BigDecimal.ZERO;
        for (OrderItem it : order.getItems()) {
            if (it.getQuantity() == null || it.getQuantity() <= 0)
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "quantity debe ser >= 1 para productId " + it.getProductId());
            if (it.getUnitPrice() == null)
                it.setUnitPrice(products.getPrice(it.getProductId()));
            BigDecimal line = it.getUnitPrice().multiply(BigDecimal.valueOf(it.getQuantity()));
            it.setTotalPrice(line);
            total = total.add(line);
        }
        order.setTotalAmount(total);
    }

    private void validateItems(List<OrderItem> items) {
        if (items == null || items.isEmpty())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "items es obligatorio y no puede ser vacío");
        for (OrderItem it : items) {
            if (it == null)
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "items contiene un elemento nulo");
            if (it.getProductId() == null || it.getProductId() <= 0)
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "productId es obligatorio y debe ser > 0");
            if (it.getQuantity() == null || it.getQuantity() < 1)
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "quantity debe ser >= 1 para productId " + it.getProductId());
            if (it.getUnitPrice() != null && it.getUnitPrice().signum() <= 0)
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "unitPrice (si se envía) debe ser > 0 para productId " + it.getProductId());
        }
    }

    @Override
    public String messageFor(OrderStatus status) {
        return switch (status) {
            case PENDING_PAYMENT -> "Su orden está pendiente de pago. Muchas gracias.";
            case CONFIRMED -> "Su orden se confirmó exitosamente.";
            case SHIPPED -> "Su orden ya fue enviada y la recibirá pronto.";
            case DELIVERED -> "Su orden fue entregada exitosamente. Gracias por su compra.";
            case CANCELLED -> "Su orden ha sido cancelada.";
            default -> "Estado de orden desconocido.";
        };
    }

    public static final class OrderStatusChangedEvent {
        public final Long orderId;
        public final Long clientId;
        public final OrderStatus oldStatus;
        public final OrderStatus newStatus;
        public final BigDecimal totalAmount;
        public final LocalDateTime orderDate;

        public OrderStatusChangedEvent(Long orderId, Long clientId, OrderStatus oldStatus, OrderStatus newStatus,
                                       BigDecimal totalAmount, LocalDateTime orderDate) {
            this.orderId = orderId;
            this.clientId = clientId;
            this.oldStatus = oldStatus;
            this.newStatus = newStatus;
            this.totalAmount = totalAmount;
            this.orderDate = orderDate;
        }
    }
}
