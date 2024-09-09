package fi.alisher.backend.models;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SwopRate {
    @JsonProperty("base_currency")
    private String baseCurrency;
    
    @JsonProperty("quote_currency")
    private String quoteCurrency;
    
    private BigDecimal quote;
    
    private LocalDate date;
}