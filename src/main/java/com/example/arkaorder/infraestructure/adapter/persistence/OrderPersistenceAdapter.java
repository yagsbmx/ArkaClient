package com.example.arkaorder.infraestructure.adapter.persistence;

import com.example.arkaorder.domain.enums.OrderStatus;
import com.example.arkaorder.domain.model.Order;
import com.example.arkaorder.domain.ports.out.OrderRepositoryPort;
import com.example.arkaorder.infraestructure.adapter.persistence.repository.OrderJpaRepository;
import com.example.arkaorder.infraestructure.mapper.OrderMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class OrderPersistenceAdapter implements OrderRepositoryPort {

    private final OrderJpaRepository repo;
    private final OrderMapper mapper;

    @Override
    public List<Order> findAll() {
        return repo.findAll()
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public Optional<Order> findById(Long id) {
        return repo.findById(id).map(mapper::toDomain);
    }

    @Override
    public Order save(Order order) {
        var entity = mapper.toEntity(order);
        var saved = repo.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Order update(Long id, Order order) {
        if (id == null) {
            throw new IllegalArgumentException("Order ID cannot be null for update operation");
        }
        order.setId(id);
        var updated = repo.save(mapper.toEntity(order));
        return mapper.toDomain(updated);
    }

    @Override
    public void deleteById(Long id) {
        repo.deleteById(id);
    }

    @Override
    public List<Order> findByStatus(OrderStatus status) {
        return repo.findByStatus(status)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }
}
