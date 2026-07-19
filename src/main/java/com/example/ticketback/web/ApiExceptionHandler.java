package com.example.ticketback.web;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import javax.naming.AuthenticationException;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, Object>> handleResponseStatus(ResponseStatusException ex, HttpServletRequest request) {
        HttpStatusCode status = ex.getStatusCode();
        Map<String, Object> response = Map.of(
                "status", status.value(),
                "error", ex.getReason() != null ? ex.getReason() : "",
                "message", ex.getReason() != null ? ex.getReason() : "",
                "path", request.getRequestURI()
        );
        return new ResponseEntity<>(response, status);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<Map<String, Object>> handleAuth(Exception ex, HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                Map.of("status", 401,
                "error", "unauthorized",
                "message", "Identifiants invalides",
                "path", request.getRequestURI())
        );
    }
}
