package com.example.arkaorder.infraestructure.adapter.persistence.repository;

import com.example.arkaorder.infraestructure.adapter.persistence.entity.OrderHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderHistoryRepository extends JpaRepository<OrderHistoryEntity, Long> {
    List<OrderHistoryEntity> findByOrderIdOrderByCreatedAtDesc(Long orderId);
    List<OrderHistoryEntity> findByUserIdOrderByCreatedAtDesc(Long userId);
}
