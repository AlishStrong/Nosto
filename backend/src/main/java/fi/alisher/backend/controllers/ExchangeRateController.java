package fi.alisher.backend.controllers;

import org.springframework.web.bind.annotation.RestController;

import fi.alisher.backend.models.ExchangeRateRequestBody;
import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api")
public class ExchangeRateController {
    
    @PostMapping
    public BigDecimal convertCurrency(@Valid @RequestBody ExchangeRateRequestBody body) {
        return body.getMonetaryValue();
    }
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        return ResponseEntity.status(HttpStatus.I_AM_A_TEAPOT).body("Request body was invalid!");
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<String> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        return ResponseEntity.status(HttpStatus.I_AM_A_TEAPOT).body("Request body was invalid!");
    }
}
