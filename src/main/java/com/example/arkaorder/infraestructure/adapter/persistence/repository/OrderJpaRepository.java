package com.example.arkaorder.infraestructure.adapter.persistence.repository;

import com.example.arkaorder.domain.enums.OrderStatus;
import com.example.arkaorder.infraestructure.adapter.persistence.entity.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderJpaRepository extends JpaRepository<OrderEntity, Long> {

    List<OrderEntity> findByStatus(OrderStatus status);
}

