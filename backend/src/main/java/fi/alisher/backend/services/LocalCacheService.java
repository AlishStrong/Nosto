package fi.alisher.backend.services;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Currency;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import fi.alisher.backend.exceptions.InvalidCurrencyCodeException;
import fi.alisher.backend.models.SwopRate;
import fi.alisher.backend.models.SwopRateCache;

@Component
public class LocalCacheService {
    private final int ttl; // in seconds
    private final TreeMap<String, SwopRateCache> cacheMap = new TreeMap<String, SwopRateCache>();
    private final Set<String> validCurrencyCodes = Currency.getAvailableCurrencies().stream().map(Currency::getCurrencyCode).collect(Collectors.toSet());

    public LocalCacheService(
        @Value("${cache.ttl: 60}") int ttl 
    ) {
        this.ttl = ttl;
    }

    public boolean isCached(String targetCurrency) throws InvalidCurrencyCodeException {
        isValidCurrencyCode(targetCurrency);
        SwopRateCache cachedRate = cacheMap.get(targetCurrency);
        return Objects.nonNull(cachedRate) && ZonedDateTime.now().isBefore(cachedRate.getExpirationDateTime());
    }

    public BigDecimal getChachedQuote(String targetCurrency) {
        return cacheMap.get(targetCurrency).getQuote();
    }

    public void chacheSwopRate(SwopRate swopRate) {
        SwopRateCache cache = new SwopRateCache(swopRate, ZonedDateTime.now().plusSeconds(ttl));
        cacheMap.put(cache.getQuoteCurrency(), cache);
    }

    public long getRemainingTTL(String targetCurrency) {
        ZonedDateTime now = ZonedDateTime.now();
        SwopRateCache cachedRate = cacheMap.get(targetCurrency);
        if (Objects.nonNull(cachedRate) && now.isBefore(cachedRate.getExpirationDateTime())) {
            return cachedRate.getExpirationDateTime().toEpochSecond() - now.toEpochSecond();
        } else {
            return 0;
        }
    }

    private boolean isValidCurrencyCode(String code) throws InvalidCurrencyCodeException {
        if (validCurrencyCodes.contains(code)) {
            return true;
        } else {
            throw new InvalidCurrencyCodeException(String.format("%s is not a valid ISO 4217 currency code", code));
        }
    }
}
