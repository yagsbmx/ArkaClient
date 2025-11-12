package com.example.arkaorder.application.service;

import com.example.arkaorder.infraestructure.adapter.persistence.entity.OrderHistoryEntity;
import com.example.arkaorder.infraestructure.adapter.persistence.repository.OrderHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrderHistoryRecorder {

    private final OrderHistoryRepository repository;

    private Long currentUserId() {
        Authentication a = SecurityContextHolder.getContext().getAuthentication();
        if (a == null) return null;
        try {
            // Allow numeric usernames to be treated as IDs, else null
            return Long.valueOf(a.getName());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    @Transactional
    public void record(Long orderId, String action, String details) {
        Long userId = currentUserId();
        var entity = OrderHistoryEntity.builder()
                .orderId(orderId)
                .userId(userId)
                .action(action)
                .details(details)
                .build();
        repository.save(entity);
    }
}
