package fi.alisher.backend.controllers;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

import fi.alisher.backend.models.ExchangeRateRequestBody;
import fi.alisher.backend.models.SwopError;
import fi.alisher.backend.services.CurrencyConversionService;
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

    private CurrencyConversionService conversionService;

    public ExchangeRateController(CurrencyConversionService conversionService) {
        this.conversionService = conversionService;
    }
    
    @PostMapping
    public BigDecimal convertCurrency(@Valid @RequestBody ExchangeRateRequestBody body) throws Exception {
        return conversionService.convertCurrency(body);
    }
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Request body was invalid!");
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<String> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Request body was invalid!");
    }

    @ExceptionHandler(HttpClientErrorException.class)
    public ResponseEntity<String> handleHttpClientErrorException(HttpClientErrorException ex) {
        System.out.println(ex.getResponseBodyAsString());
        SwopError errorResponseBody = ex.getResponseBodyAs(SwopError.class);
        return ResponseEntity.status(ex.getStatusCode()).body(errorResponseBody.getError().getMessage());
    }

    @ExceptionHandler(HttpServerErrorException.class)
    public ResponseEntity<String> handleHttpServerErrorException(HttpServerErrorException ex) {
        System.out.println(ex.getResponseBodyAsString());
        SwopError errorResponseBody = ex.getResponseBodyAs(SwopError.class);
        return ResponseEntity.status(ex.getStatusCode()).body(errorResponseBody.getError().getMessage());
    }
}
