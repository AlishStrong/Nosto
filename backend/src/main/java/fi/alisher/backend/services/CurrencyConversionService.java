package fi.alisher.backend.services;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.springframework.stereotype.Service;

import fi.alisher.backend.clients.SwopClient;
import fi.alisher.backend.models.ExchangeRateRequestBody;

@Service
public class CurrencyConversionService {
    private SwopClient swopClient;

    public CurrencyConversionService(
        SwopClient swopClient
    ) {
        this.swopClient = swopClient;
    }

    public BigDecimal convertCurrency(ExchangeRateRequestBody body) throws Exception {
        if ("eur".equalsIgnoreCase(body.getSourceCurrency())  && !"eur".equalsIgnoreCase(body.getTargetCurrency())) {
            BigDecimal quote = getQuote(body.getTargetCurrency());
            BigDecimal toTarget = body.getMonetaryValue().multiply(quote);
            
            BigDecimal result = toTarget.setScale(2, RoundingMode.HALF_UP);
            return result;
        } else if (!"eur".equalsIgnoreCase(body.getSourceCurrency()) && "eur".equalsIgnoreCase(body.getTargetCurrency())) {
            BigDecimal quoteToEur = getQuote(body.getSourceCurrency());
            BigDecimal toEur = body.getMonetaryValue().divide(quoteToEur, 6, RoundingMode.HALF_UP);
            
            BigDecimal result = toEur.setScale(2, RoundingMode.HALF_UP);
            return result;
        } else if (body.getSourceCurrency().equalsIgnoreCase(body.getTargetCurrency())) {
            return body.getMonetaryValue().setScale(2, RoundingMode.HALF_UP);
        } 
        else {
            BigDecimal quoteToEur = getQuote(body.getSourceCurrency());
            BigDecimal sourcetoEur = body.getMonetaryValue().divide(quoteToEur, 6, RoundingMode.HALF_UP);

            BigDecimal quoteToTarget = getQuote(body.getTargetCurrency());
            BigDecimal eurToTarget = sourcetoEur.multiply(quoteToTarget);

            BigDecimal result = eurToTarget.setScale(2, RoundingMode.HALF_UP);
            return result;
        }
    }

    private BigDecimal getQuote(String targetCurrency) throws Exception {
        return swopClient.getSingleQuote(targetCurrency);
    }
}
