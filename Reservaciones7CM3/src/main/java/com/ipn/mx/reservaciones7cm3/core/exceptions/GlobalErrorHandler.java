package com.ipn.mx.reservaciones7cm3.core.exceptions;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalErrorHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler(value = { EntityNotFoundException.class })
    public ResponseEntity<CustomErrorRecord> handleResourceNotFound(EntityNotFoundException ex, WebRequest request) {
        CustomErrorRecord error = new CustomErrorRecord(
                LocalDateTime.now(),
                ex.getMessage(),
                request.getDescription(false)
        );
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = { BussinesValidationException.class })
    public ResponseEntity<CustomErrorRecord>
    handleBussinesValidationException(BussinesValidationException ex, WebRequest request) {
        CustomErrorRecord error = new CustomErrorRecord(
                LocalDateTime.now(),
                ex.getMessage(),
                request.getDescription(false)
        );
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    public ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        String detalle = ex.getBindingResult()
                .getFieldErrors().stream()
                .map(error -> error.getField() + " : " + error.getDefaultMessage())
                .collect(Collectors.joining(" , "));

        CustomErrorRecord errorRecord = new CustomErrorRecord(
                LocalDateTime.now(),
                "Hubo errores en la validacion de uno o mas campos",
                detalle
        );
        return new ResponseEntity<>(errorRecord, headers, status);
    }
}
