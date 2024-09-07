package fi.alisher.backend.models;

import java.math.BigDecimal;
import java.util.Locale;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=true)
public class ExchangeRateResponseBody extends ExchangeRateRequestBody {
    private BigDecimal convertedValue;
    private String localeFormattedConvertedValue;
    private Locale locale;

    public ExchangeRateResponseBody(
        ExchangeRateRequestBody rb, 
        BigDecimal convertedValue,
        String localeFormattedConvertedValue,
        Locale locale
    ) {
        super(rb.getSourceCurrency(), rb.getTargetCurrency(), rb.getMonetaryValue());
        this.convertedValue = convertedValue;
        this.localeFormattedConvertedValue = localeFormattedConvertedValue;
        this.locale = locale;
    }
}
