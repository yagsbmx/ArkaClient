package com.example.arkaorder.infraestructure.adapter.out.orderclient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;

@FeignClient(
        name = "product-service",
        path = "/api/products",
        configuration = ProductFeignConfig.class
)
public interface ProductFeignClient {

    @PutMapping("/update/stock/{id}/decrement")
    void decrementStock(@PathVariable("id") Long id, @RequestParam("quantity") int quantity);

    @PutMapping("/update/stock/{id}/increment")
    void incrementStock(@PathVariable("id") Long id, @RequestParam("quantity") int quantity);

    @GetMapping("/{id}/available")
    ApiResponse<Boolean> isAvailable(@PathVariable("id") Long id, @RequestParam("quantity") int quantity);

    @GetMapping("/{id}")
    ApiResponse<ProductDto> getById(@PathVariable("id") Long id);

    record ApiResponse<T>(boolean success, String message, T data) {}

    record ProductDto(
            Long id,
            String name,
            String categoryName,
            String brandName,
            String description,
            BigDecimal price,
            Integer stock,
            String productStatus
    ) {}
}
