package fi.alisher.backend.models;

import java.time.LocalDateTime;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=true)
public class SwopRateCache extends SwopRate {  
    private LocalDateTime expirationDateTime;

    public SwopRateCache(
        SwopRate sr,
        LocalDateTime expirationDateTime
    ) {
        super(sr.getBaseCurrency(), sr.getQuoteCurrency(), sr.getQuote(), sr.getDate());
        this.expirationDateTime = expirationDateTime;
    }
}