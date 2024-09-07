package fi.alisher.backend.controllers;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

import fi.alisher.backend.models.ExchangeRateRequestBody;
import fi.alisher.backend.models.ExchangeRateResponseBody;
import fi.alisher.backend.models.SwopError;
import fi.alisher.backend.services.CurrencyConversionService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

import org.springframework.web.servlet.LocaleResolver;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.Currency;
import java.util.Locale;


@RestController
@RequestMapping("/api")
public class ExchangeRateController {

    private LocaleResolver localeResolver;
    private CurrencyConversionService conversionService;

    public ExchangeRateController(
        LocaleResolver localeResolver,
        CurrencyConversionService conversionService
    ) {
        this.localeResolver = localeResolver;
        this.conversionService = conversionService;
    }
    
    @PostMapping
    public ExchangeRateResponseBody convertCurrency(
        HttpServletRequest request,
        @Valid @RequestBody ExchangeRateRequestBody body
    ) throws Exception {
        BigDecimal convertedValue = conversionService.convertCurrency(body);
        Locale locale = localeResolver.resolveLocale(request);
        NumberFormat formatter = NumberFormat.getCurrencyInstance(locale);
        Currency targetCurrency = Currency.getInstance(body.getTargetCurrency());
        formatter.setCurrency(targetCurrency);
        formatter.setRoundingMode(RoundingMode.HALF_UP);
        formatter.setMaximumFractionDigits(targetCurrency.getDefaultFractionDigits());
        String localeFormattedConvertedValue = formatter.format(convertedValue);
        ExchangeRateResponseBody responseBody = new ExchangeRateResponseBody(body, convertedValue, localeFormattedConvertedValue, locale);
        return responseBody;
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
