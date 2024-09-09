package fi.alisher.backend.services;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import fi.alisher.backend.exceptions.InvalidCurrencyCodeException;
import fi.alisher.backend.models.SwopRate;

public class LocalCacheServiceTest {

    private final int testTTL = 1;

    private LocalCacheService localCacheService;

    @BeforeEach
    public void before() {
        localCacheService = new LocalCacheService(testTTL);
    }

    @Test
    void testIsCached_validCurrency() throws InvalidCurrencyCodeException, InterruptedException {
        String targetCurrency = "USD";
        SwopRate swopRate = new SwopRate();
        swopRate.setQuoteCurrency(targetCurrency);

        assertFalse(localCacheService.isCached(targetCurrency));

        localCacheService.chacheSwopRate(swopRate);
        assertTrue(localCacheService.isCached(targetCurrency));

        Thread.sleep(testTTL*1500);

        assertFalse(localCacheService.isCached(targetCurrency));
    }

    @Test
    void testIsCached_invalidCurrency() throws InvalidCurrencyCodeException {
        String targetCurrency = "USA";

        assertThrows(InvalidCurrencyCodeException.class, () -> {
            localCacheService.isCached(targetCurrency);
        }, String.format("%s is not a valid ISO 4217 currency code", targetCurrency));
    }
}
