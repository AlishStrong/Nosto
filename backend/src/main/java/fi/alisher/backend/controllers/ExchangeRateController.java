package fi.alisher.backend.controllers;

import org.springframework.web.bind.annotation.RestController;

import fi.alisher.backend.models.ExchangeRateRequestBody;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api")
public class ExchangeRateController {
    
    @PostMapping
    public BigDecimal postMethodName(@RequestBody ExchangeRateRequestBody body) {
        return body.getMonetaryValue();
    }
    
}
