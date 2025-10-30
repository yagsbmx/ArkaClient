package com.example.arkaorder.infraestructure.adapter.persistence.repository;

import com.example.arkaorder.infraestructure.adapter.persistence.entity.OrderItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderItemJpaRepository extends JpaRepository<OrderItemEntity, Long> {

    List<OrderItemEntity> findByProductId(Long productId);

    List<OrderItemEntity> findByQuantity(Integer quantity);
}
