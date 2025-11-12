package com.example.arkaorder.infraestructure.adapter.web.controller;

import com.example.arkaorder.infraestructure.adapter.persistence.entity.OrderHistoryEntity;
import com.example.arkaorder.infraestructure.adapter.persistence.repository.OrderHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders/history")
@RequiredArgsConstructor
public class OrderHistoryController {

    private final OrderHistoryRepository repository;

    @GetMapping("/order/{orderId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<OrderHistoryEntity>> byOrder(@PathVariable Long orderId) {
        return ResponseEntity.ok(repository.findByOrderIdOrderByCreatedAtDesc(orderId));
    }

    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<OrderHistoryEntity>> byUser(@RequestParam Long userId) {
        return ResponseEntity.ok(repository.findByUserIdOrderByCreatedAtDesc(userId));
    }
}
