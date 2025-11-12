package com.example.arkaorder.infraestructure.adapter.web.controller;

import com.example.arkaorder.domain.enums.OrderStatus;
import com.example.arkaorder.domain.ports.in.OrderUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/orders/reports")
@RequiredArgsConstructor
public class OrderReportController {

    private final OrderUseCase useCase;

    @GetMapping(value = "/weekly.csv", produces = "text/csv")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<byte[]> weeklyCsv(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to
    ) {
        LocalDate end = to != null ? to : LocalDate.now();
        LocalDate start = from != null ? from : end.minusDays(7);

        var rows = useCase.list().stream()
                .filter(o -> o.getOrderDate() != null && !o.getOrderDate().toLocalDate().isBefore(start) && !o.getOrderDate().toLocalDate().isAfter(end))
                .collect(Collectors.toList());

        long total = rows.stream().count();
        long delivered = rows.stream().filter(o -> o.getStatus() == OrderStatus.DELIVERED).count();

        StringBuilder sb = new StringBuilder();
        sb.append("from,to,total_orders,delivered\n");
        sb.append(start).append(",").append(end).append(",").append(total).append(",").append(delivered).append("\n");

        byte[] body = sb.toString().getBytes(StandardCharsets.UTF_8);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=weekly_orders.csv")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(body);
    }
}
