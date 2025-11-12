package com.example.arkaorder.infraestructure.adapter.web.advice;

import feign.FeignException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@RestControllerAdvice
public class GlobalErrorAdvice {

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String,String>> onResponseStatus(ResponseStatusException ex) {
        return ResponseEntity.status(ex.getStatusCode())
                .body(Map.of("message", ex.getReason() != null ? ex.getReason() : ex.getStatusCode().toString()));
    }

    @ExceptionHandler(FeignException.class)
    public ResponseEntity<Map<String,String>> onFeign(FeignException ex) {
        int sc = ex.status() > 0 ? ex.status() : 502;
        return ResponseEntity.status(sc).body(Map.of("message", ex.getMessage()));
    }
}
