package fi.alisher.backend.models;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ExchangeRateRequestBody {
    private String sourceCurrency;
    private String targetCurrency;
    private BigDecimal monetaryValue;
}
