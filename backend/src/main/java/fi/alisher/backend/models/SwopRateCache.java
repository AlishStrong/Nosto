package fi.alisher.backend.models;

import java.time.ZonedDateTime;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=true)
public class SwopRateCache extends SwopRate {  
    private ZonedDateTime expirationDateTime;

    public SwopRateCache(
        SwopRate sr,
        ZonedDateTime expirationDateTime
    ) {
        super(sr.getBaseCurrency(), sr.getQuoteCurrency(), sr.getQuote(), sr.getDate());
        this.expirationDateTime = expirationDateTime;
    }
}