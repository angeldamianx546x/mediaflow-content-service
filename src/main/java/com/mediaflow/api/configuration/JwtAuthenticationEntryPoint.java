package com.mediaflow.api.configuration;

import java.io.IOException;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @ExceptionHandler(SecurityException.class)
    public ResponseEntity<?> handleSecurityException(SecurityException ex) {
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(error("FORBIDDEN", ex.getMessage()));
    }

    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException
    ) throws IOException, ServletException {

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        Map<String, Object> errorDetails = new HashMap<>();
        errorDetails.put("timestamp", Instant.now().toString());

        // Usa el mensaje de la excepción como código
        errorDetails.put("code", authException.getMessage());

        // Mensaje personalizado según el tipo de error
        String message = authException.getMessage();
        if (message.contains("expired")) {
            errorDetails.put("message", "El token ha expirado. Por favor, inicia sesión nuevamente.");
        } else if (message.contains("invalid")) {
            errorDetails.put("message", "Token inválido. Por favor, proporciona un token válido.");
        } else if (message.contains("malformed")) {
            errorDetails.put("message", "Token malformado. Verifica el formato del token.");
        } else {
            errorDetails.put("message", "Error de autenticación: " + message);
        }

        errorDetails.put("path", request.getRequestURI());

        String jsonResponse = objectMapper.writeValueAsString(errorDetails);
        response.getWriter().write(jsonResponse);
    }

    private java.util.Map<String, Object> error(String code, String message) {
        java.util.Map<String, Object> map = new java.util.HashMap<>();
        map.put("timestamp", java.time.Instant.now().toString());
        map.put("code", code);
        map.put("message", message);
        return map;
    }
}
