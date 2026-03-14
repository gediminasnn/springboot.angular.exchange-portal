package com.example.exchangeportal.controller;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GlobalExceptionHandlerControllerTest {

    private final GlobalExceptionHandlerController handler = new GlobalExceptionHandlerController();

    @Test
    public void testHandleException() {
        Exception e = new RuntimeException("Test exception");
        ResponseEntity<String> result = handler.handleException(e);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, result.getStatusCode());
        assertEquals("Internal server error", result.getBody());
    }
}
