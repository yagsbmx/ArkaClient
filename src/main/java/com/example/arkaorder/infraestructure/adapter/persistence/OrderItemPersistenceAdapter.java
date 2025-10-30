package com.example.arkaorder.infraestructure.adapter.persistence;

import com.example.arkaorder.domain.model.OrderItem;
import com.example.arkaorder.domain.ports.out.OrderItemRepositoryPort;
import com.example.arkaorder.infraestructure.adapter.persistence.repository.OrderItemJpaRepository;
import com.example.arkaorder.infraestructure.mapper.OrderItemMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class OrderItemPersistenceAdapter implements OrderItemRepositoryPort {

    private final OrderItemJpaRepository repo;
    private final OrderItemMapper mapper;

    @Override
    public List<OrderItem> findAll() {
        return repo.findAll()
                   .stream()
                   .map(mapper::toDomain)
                   .toList();
    }

    @Override
    public Optional<OrderItem> findById(Long id) {
        return repo.findById(id).map(mapper::toDomain);
    }

    @Override
    public OrderItem save(OrderItem orderItem) {
        var entity = mapper.toEntity(orderItem);
        var saved = repo.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public OrderItem update(Long id, OrderItem orderItem) {
        orderItem.setId(id);
        var updated = repo.save(mapper.toEntity(orderItem));
        return mapper.toDomain(updated);
    }

    @Override
    public void deleteById(Long id) {
        repo.deleteById(id);
    }
}
