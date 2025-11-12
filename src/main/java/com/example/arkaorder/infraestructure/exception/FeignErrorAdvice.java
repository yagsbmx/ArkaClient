package com.example.arkaorder.infraestructure.exception;

import feign.FeignException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@RestControllerAdvice
public class FeignErrorAdvice {
    @ExceptionHandler(FeignException.Unauthorized.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public Map<String,String> on401(FeignException.Unauthorized e){
        return Map.of("message","No autorizado en servicio remoto");
    }
}