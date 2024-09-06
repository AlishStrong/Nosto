package fi.alisher.backend.services;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.springframework.stereotype.Service;

import fi.alisher.backend.models.ExchangeRateRequestBody;

@Service
public class CurrencyConversionService {
    private BigDecimal quote = new BigDecimal("1.110414");

    public BigDecimal convertCurrency(ExchangeRateRequestBody body) {
        return body.getMonetaryValue().multiply(quote).setScale(2, RoundingMode.HALF_UP);
    }
}
