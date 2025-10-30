package com.example.arkaorder.infraestructure.adapter.web.controller;

import com.example.arkaorder.domain.enums.OrderStatus;
import com.example.arkaorder.domain.ports.in.OrderUseCase;
import com.example.arkaorder.infraestructure.dto.OrderRequestDto;
import com.example.arkaorder.infraestructure.dto.OrderResponseDto;
import com.example.arkaorder.infraestructure.mapper.OrderMapper;
import org.springframework.http.MediaType;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import lombok.extern.slf4j.Slf4j;

import java.net.URI;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderUseCase useCase;
    private final OrderMapper mapper;

    /**@PostMapping("/create")
    public ResponseEntity<OrderResponseDto> createOrder(@Valid @RequestBody OrderRequestDto requestDto) {
        var created = useCase.createOrder(mapper.requestToDomain(requestDto));
        return ResponseEntity
                .created(URI.create("/api/orders/" + created.getId()))
                .body(mapper.toResponseDto(created));**/
    @PostMapping(
        value = "/create",
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<OrderResponseDto> createOrder(@Valid @RequestBody OrderRequestDto requestDto) {
        log.debug("Incoming order: clientId={}, items={}",
                requestDto.getClientId(),
                requestDto.getItems() != null ? requestDto.getItems().size() : 0);

        var created = useCase.createOrder(mapper.requestToDomain(requestDto));
        URI location = URI.create("/api/orders/" + created.getId());
        return ResponseEntity.created(location).body(mapper.toResponseDto(created));
    }
    

    @PutMapping("/update/{id}")
    public ResponseEntity<OrderResponseDto> updateOrder(
            @PathVariable("id") Long id,
            @Valid @RequestBody OrderRequestDto requestDto) {
        var updated = useCase.updateOrder(id, mapper.requestToDomain(requestDto));
        return ResponseEntity.ok(mapper.toResponseDto(updated));
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponseDto> getById(@PathVariable("id") Long id) {
        var order = useCase.getOrderById(id)
                .orElseThrow(() -> new IllegalArgumentException("Order not found: " + id));
        return ResponseEntity.ok(mapper.toResponseDto(order));
    }

    @GetMapping
    public ResponseEntity<List<OrderResponseDto>> listAll() {
        var orders = useCase.list().stream()
                .map(mapper::toResponseDto)
                .toList();
        return ResponseEntity.ok(orders);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable("id") Long id) {
        useCase.deleteOrder(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/filter/status/{status}")
    public ResponseEntity<List<OrderResponseDto>> findByStatus(@PathVariable("status") String status) {
        var list = useCase.findByStatus(OrderStatus.fromString(status)).stream()
                .map(mapper::toResponseDto)
                .toList();
        return ResponseEntity.ok(list);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<Void> updateStatus(@PathVariable("id") Long id,
                                            @RequestParam("status") String status) {
        OrderStatus st = OrderStatus.valueOf(status.trim().toUpperCase());
        useCase.updateStatus(id, st);
        return ResponseEntity.noContent().build();
    }

    

    
}
