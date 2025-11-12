package com.example.arkaorder.infraestructure.security;

import feign.FeignException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class GlobalSecurityAdvice {

    @ExceptionHandler(org.springframework.security.access.AccessDeniedException.class)
    public Map<String,Object> onAccessDenied(Exception e) {
        return Map.of("status", 403, "error", "Forbidden", "message", "No está autorizado para acceder a este recurso");
    }

    @ExceptionHandler(FeignException.Forbidden.class)
    public Map<String,Object> onFeign403(FeignException.Forbidden e) {
        return Map.of("status", 403, "error", "Forbidden", "message", "No está autorizado para acceder a este recurso");
    }

    @ExceptionHandler(FeignException.Unauthorized.class)
    public Map<String,Object> onFeign401(FeignException.Unauthorized e) {
        return Map.of("status", 401, "error", "Unauthorized", "message", "No está autorizado para acceder a este recurso");
    }
}
