package com.example.arkaorder.domain.exceptions;


import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.validation.ConstraintViolationException;
import java.util.Map;

@RestControllerAdvice
public class ApiExceptionHandler {

  @ExceptionHandler(MethodArgumentNotValidException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public Map<String, Object> handleValidation(MethodArgumentNotValidException ex) {
    return Map.of(
        "status", 400,
        "error", "Bad Request",
        "message", ex.getBindingResult().getFieldErrors()
            .stream().map(fe -> fe.getField() + ": " + fe.getDefaultMessage()).toList()
    );
  }

  @ExceptionHandler({DataIntegrityViolationException.class, ConstraintViolationException.class})
  @ResponseStatus(HttpStatus.CONFLICT)
  public Map<String, Object> handleConstraint(Exception ex) {
    return Map.of("status", 409, "error", "Conflict", "message", ex.getMessage());
  }

  @ExceptionHandler(Throwable.class) // ⬅️ catch-all para garantizar body
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public Map<String, Object> handleGeneric(Throwable ex) {
    return Map.of("status", 500, "error", "Internal Server Error", "message", ex.getMessage());
  }
}