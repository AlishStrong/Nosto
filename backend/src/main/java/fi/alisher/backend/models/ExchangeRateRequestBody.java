package fi.alisher.backend.models;

import lombok.Data;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.DecimalMin;

@Data
public class ExchangeRateRequestBody {

    @NotNull
    @Size(min = 3, max = 3)
    private String sourceCurrency;

    @NotNull
    @Size(min = 3, max = 3)
    private String targetCurrency;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = false)
    private BigDecimal monetaryValue;
}
