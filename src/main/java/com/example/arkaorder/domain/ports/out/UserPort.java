package com.example.arkaorder.domain.ports.out;

public interface UserPort { 
    boolean existsAndActive(Long userId); 
 }
