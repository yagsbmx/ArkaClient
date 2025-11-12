package com.example.arkaorder.infraestructure.adapter.out.userclient;

import lombok.Data;

@Data
public class ApiResponse<T> {
    private String message;
    private T data;
}
