package fi.alisher.backend.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

import fi.alisher.backend.controllers.ExchangeRateController;
import fi.alisher.backend.models.SwopError;

@ControllerAdvice(basePackageClasses = {ExchangeRateController.class})
public class ExchangeRateControllerAdvice {
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
