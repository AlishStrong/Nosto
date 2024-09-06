package fi.alisher.backend.services;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.springframework.stereotype.Service;

import fi.alisher.backend.models.ExchangeRateRequestBody;

@Service
public class CurrencyConversionService {
    private BigDecimal quoteOne = new BigDecimal("1.110414"); // EUR to USD on 06.09.2024
    private BigDecimal quoteTwo = new BigDecimal("0.843216"); // EUR to GBP on 06.09.2024

    public BigDecimal convertCurrency(ExchangeRateRequestBody body) {
        if ("eur".equalsIgnoreCase(body.getSourceCurrency())  && !"eur".equalsIgnoreCase(body.getTargetCurrency())) {
            BigDecimal toTarget = body.getMonetaryValue().multiply(quoteOne);
            BigDecimal result = toTarget.setScale(2, RoundingMode.HALF_UP);
            return result;
        } else if (!"eur".equalsIgnoreCase(body.getSourceCurrency()) && "eur".equalsIgnoreCase(body.getTargetCurrency())) {
            BigDecimal toEur = body.getMonetaryValue().divide(quoteOne, 6, RoundingMode.HALF_UP);
            BigDecimal result = toEur.setScale(2, RoundingMode.HALF_UP);
            return result;
        } else if (body.getSourceCurrency().equalsIgnoreCase(body.getTargetCurrency())) {
            return body.getMonetaryValue().setScale(2, RoundingMode.HALF_UP);
        } 
        else {
            BigDecimal toEur = body.getMonetaryValue().divide(quoteOne, 6, RoundingMode.HALF_UP);
            BigDecimal toTarget = toEur.multiply(quoteTwo);
            BigDecimal result = toTarget.setScale(2, RoundingMode.HALF_UP);
            return result;
        }
    }
}
