package com.example.arkaorder.domain.ports.out;

import com.example.arkaorder.domain.enums.OrderStatus;
import com.example.arkaorder.domain.model.Order;

public interface NotificationPort {
    void notifyOrderStatusChange(Order order, OrderStatus oldStatus, OrderStatus newStatus, String toEmail);
}
