package com.example.arkaorder.infraestructure.adapter.web.controller;

import com.example.arkaorder.domain.enums.OrderStatus;
import com.example.arkaorder.domain.ports.in.OrderUseCase;
import com.example.arkaorder.infraestructure.dto.OrderRequestDto;
import com.example.arkaorder.infraestructure.dto.OrderResponseDto;
import com.example.arkaorder.infraestructure.mapper.OrderMapper;
import com.example.arkaorder.infraestructure.security.UserContext;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderUseCase useCase;
    private final OrderMapper mapper;
    private final UserContext userContext;

    @GetMapping("/ping")
    public ResponseEntity<String> ping() {
        return ResponseEntity.ok("order ok");
    }

    @PostMapping(value = "/from-cart/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, String>> createFromCart(@PathVariable("userId") Long userId) {
        var created = useCase.createOrderFromCart(userId);
        var location = URI.create("/api/orders/" + created.getId());
        return ResponseEntity.created(location).body(Map.of(
                "message", useCase.messageFor(created.getStatus()),
                "orderId", String.valueOf(created.getId())
        ));
    }


    @PostMapping(value = "/create", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<OrderResponseDto> createOrder(@Valid @RequestBody @P("dto") OrderRequestDto dto) {
        log.debug("Incoming order: clientId={}, items={}", dto.getClientId(),
                dto.getItems() != null ? dto.getItems().size() : 0);
        var created = useCase.createOrder(mapper.requestToDomain(dto));
        var location = URI.create("/api/orders/" + created.getId());
        return ResponseEntity.created(location).body(mapper.toResponseDto(created));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<OrderResponseDto> updateOrder(@PathVariable("id") Long id,
                                                        @Valid @RequestBody OrderRequestDto requestDto) {
        var updated = useCase.updateOrder(id, mapper.requestToDomain(requestDto));
        return ResponseEntity.ok(mapper.toResponseDto(updated));
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponseDto> getById(@PathVariable("id") Long id) {
        var order = useCase.getOrderById(id).orElse(null);
        if (order == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(mapper.toResponseDto(order));
    }

    @GetMapping
    public ResponseEntity<List<OrderResponseDto>> listAll() {
        var auth = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = auth != null && auth.getAuthorities().stream().anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));
        Long me = userContext.currentUserIdOrNull();
        var all = useCase.list();
        var filtered = isAdmin || me == null ? all : all.stream().filter(o -> me.equals(o.getClientId())).toList();
        return ResponseEntity.ok(filtered.stream().map(mapper::toResponseDto).toList());
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable("id") Long id) {
        useCase.deleteOrder(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/filter/status/{status}")
    public ResponseEntity<List<OrderResponseDto>> findByStatus(@PathVariable("status") String status) {
        var st = OrderStatus.valueOf(status.trim().toUpperCase());
        var list = useCase.findByStatus(st).stream().map(mapper::toResponseDto).toList();
        return ResponseEntity.ok(list);
    }

    @PutMapping(value = "/{id}/status", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, String>> updateStatus(@PathVariable("id") Long id,
                                                            @RequestParam("status") String status) {
        var st = OrderStatus.valueOf(status.trim().toUpperCase());
        var updated = useCase.updateStatus(id, st);
        return ResponseEntity.ok(Map.of("message", useCase.messageFor(updated.getStatus())));
    }
}
