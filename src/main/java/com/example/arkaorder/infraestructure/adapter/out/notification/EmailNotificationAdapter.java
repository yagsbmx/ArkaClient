package com.example.arkaorder.infraestructure.adapter.out.notification;

import com.example.arkaorder.domain.enums.OrderStatus;
import com.example.arkaorder.domain.model.Order;
import com.example.arkaorder.domain.ports.out.NotificationPort;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EmailNotificationAdapter implements NotificationPort {

    private final JavaMailSender mailSender;

    @Value("${app.notifications.from:no-reply@arka.local}")
    private String from;

    @Override
    public void notifyOrderStatusChange(Order order, OrderStatus oldStatus, OrderStatus newStatus, String toEmail) {
        if (toEmail == null || toEmail.isBlank()) return;
        var subject = "AR KA - Estado de tu orden #" + order.getId() + ": " + newStatus.name();
        var body = """
                Hola,

                El estado de tu orden #%d cambió de %s a %s.
                Total: %s
                Fecha: %s

                ¡Gracias por tu compra!
                """.formatted(order.getId(), oldStatus, newStatus, order.getTotalAmount(), order.getOrderDate());

        var msg = new SimpleMailMessage();
        msg.setFrom(from);
        msg.setTo(toEmail);
        msg.setSubject(subject);
        msg.setText(body);

        try {
            mailSender.send(msg);
        } catch (MailException ignored) { /* no romper flujo */ }
    }
}
