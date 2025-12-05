package com.mediaflow.api.controller;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import javax.naming.AuthenticationException;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import jakarta.persistence.EntityNotFoundException;

@RestControllerAdvice
public class RestExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<?> handleNotFound(EntityNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error("NOT_FOUND", ex.getMessage()));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<?> handleConflict(DataIntegrityViolationException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error("CONFLICT", ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, Object> body = error("VALIDATION_ERROR", "Validation failed");
        Map<String, String> fields = new HashMap<>();
        for (FieldError fe : ex.getBindingResult().getFieldErrors()) {
            fields.put(fe.getField(), fe.getDefaultMessage());
        }
        body.put("fields", fields);
        return ResponseEntity.badRequest().body(body);
    }

    private Map<String, Object> error(String code, String message) {
        Map<String, Object> map = new HashMap<>();
        map.put("timestamp", Instant.now().toString());
        map.put("code", code);
        map.put("message", message);
        return map;
    }

    //esepcion de autenticacion
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<?> handleAuthentication(AuthenticationException ex) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(error("AUTHENTICATION_FAILED", ex.getMessage()));
    }
    //Argumentos no compatibles
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> handleAuthentication(IllegalArgumentException ex) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(error("AUTHENTICATION_FAILED", ex.getMessage()));
    }

    //Valida que el método http seleccionado este disponible para la ruta
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<Map<String, Object>> handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException ex) {
        String message = String.format("El método HTTP '%s' no está soportado para esta ruta", ex.getMethod());
        Map<String, Object> body = new HashMap<>();
        body.put("error", message);
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(body);
    }

    //Ocurre cuando el recusro no ha sido encontrado
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNoResourceFound(NoResourceFoundException ex) {
        String message = String.format("El recurso solicitado no existe");
        Map<String, Object> body = new HashMap<>();
        body.put("error", message);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    //Valida que si un método pide parámetros estos se incluyan en la solicitud
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<Map<String, Object>> handleMissingServletRequestParameter(MissingServletRequestParameterException ex) {
        String message = String.format("El parámetro requerido '%s' no está presente", ex.getParameterName());
        Map<String, Object> body = new HashMap<>();
        body.put("error", message);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    //Valida que el tipo de contenido que se ingresa en request body sea el adecuado
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<Map<String, Object>> handleMissingServletRequestParameter(HttpMediaTypeNotSupportedException ex) {
        String message = String.format("El tipo de contenido '%s' no está soportado", ex.getContentType());
        Map<String, Object> body = new HashMap<>();
        body.put("error", message);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    // Excepción para tipos de argumentos incorrectos en path variables
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, Object>> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException ex) {
        String message = String.format("El parámetro '%s' debe ser de tipo %s",
                ex.getName(),
                ex.getRequiredType().getSimpleName()
        );
        Map<String, Object> body = new HashMap<>();
        body.put("error", message);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    // Excepción para JSON malformado o tipos incorrectos
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
        String message = "El formato del JSON es inválido";

        // Detectar errores específicos
        if (ex.getMessage().contains("LocalDate")) {
            message = "El formato de fecha debe ser YYYY-MM-DD";
        } else if (ex.getMessage().contains("JSON parse error")) {
            message = "El JSON enviado tiene errores de sintaxis";
        }

        Map<String, Object> body = new HashMap<>();
        body.put("error", message);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }
}
