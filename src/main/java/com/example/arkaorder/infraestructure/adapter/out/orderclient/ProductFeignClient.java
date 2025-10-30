package com.example.arkaorder.infraestructure.adapter.out.orderclient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@FeignClient(
    name = "product-service",
    path = "/products",
    configuration = ProductFeignConfig.class
)
public interface ProductFeignClient {

    @PutMapping("/update/stock/{id}/decrement")
    void decrementStock(@PathVariable("id") Long id, @RequestParam("quantity") int quantity);

    @PutMapping("/update/stock/{id}/increment")
    void incrementStock(@PathVariable("id") Long id, @RequestParam("quantity") int quantity);

    @GetMapping("/{id}/available")
    boolean isAvailable(@PathVariable("id") Long id, @RequestParam("quantity") int quantity);

    @PostMapping("/{id}/reserve")
    String reserve(@PathVariable("id") Long id, @RequestParam("quantity") int quantity, @RequestParam("ttlSeconds") long ttlSeconds);

    @PostMapping("/reservation/{reservationId}/commit")
    void commitReservation(@PathVariable("reservationId") String reservationId);

    @DeleteMapping("/reservation/{reservationId}/release")
    void releaseReservation(@PathVariable("reservationId") String reservationId);

    @GetMapping("/{id}")
    ProductResponse getById(@PathVariable("id") Long id);

    @GetMapping("/{id}/price")
    BigDecimal getPrice(@PathVariable("id") Long id);


    record ProductResponse(Long id, String name, String categoryName, String brandName,
                           String description, BigDecimal price, Integer stock, String productStatus) {}
}
