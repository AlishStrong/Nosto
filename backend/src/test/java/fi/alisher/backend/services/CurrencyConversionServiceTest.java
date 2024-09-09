package fi.alisher.backend.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import fi.alisher.backend.clients.SwopClient;
import fi.alisher.backend.exceptions.InvalidCurrencyCodeException;
import fi.alisher.backend.models.ExchangeRateRequestBody;
import fi.alisher.backend.models.SwopRate;

public class CurrencyConversionServiceTest {

    @Mock
    private SwopClient swopClient;

    @Mock
    private LocalCacheService cacheService;

    private CurrencyConversionService conversionService;

    private static BigDecimal baseMonetaryValue = new BigDecimal("100");
    private static BigDecimal quoteEurToUsd = new BigDecimal("1.110414"); // EUR to USD on 06.09.2024
    private static BigDecimal quoteEurToGbp = new BigDecimal("0.843216"); // EUR to GBP on 06.09.2024

    @BeforeEach
    public void before() {
        MockitoAnnotations.openMocks(this);
        conversionService = new CurrencyConversionService(swopClient, cacheService);
    }
    
    @ParameterizedTest
    @MethodSource("requestBodyData")
    public void convertCurrency(String sourceCurrency, String targetCurrency, BigDecimal expectedValue, String exmessage) throws Exception {
        ExchangeRateRequestBody body = new ExchangeRateRequestBody();
        body.setSourceCurrency(sourceCurrency);
        body.setTargetCurrency(targetCurrency);
        body.setMonetaryValue(baseMonetaryValue);

        SwopRate toUsd = new SwopRate();
        toUsd.setQuote(quoteEurToUsd);

        SwopRate toGbp = new SwopRate();
        toGbp.setQuote(quoteEurToGbp);

        when(cacheService.isCached(anyString())).thenAnswer((i) -> {
            LocalCacheService cs = new LocalCacheService(1);
            return cs.isCached(i.getArguments()[0].toString());
        });
        when(swopClient.getSingleRate("USD")).thenReturn(toUsd);
        when(swopClient.getSingleRate("GBP")).thenReturn(toGbp);

        if (Objects.nonNull(exmessage)) {
            assertThrows(InvalidCurrencyCodeException.class, () -> {
                conversionService.convertCurrency(body);
            }, exmessage);
        } else {
            assertEquals(expectedValue, conversionService.convertCurrency(body));
        }
    }

    private static Stream<Arguments> requestBodyData() {
        return Stream.of(
            Arguments.of("EUR", "USD", baseMonetaryValue.multiply(quoteEurToUsd).setScale(2, RoundingMode.HALF_UP), null),
            Arguments.of("eur", "USD", baseMonetaryValue.multiply(quoteEurToUsd).setScale(2, RoundingMode.HALF_UP), null),
            Arguments.of( "USD", "EUR", baseMonetaryValue.divide(quoteEurToUsd, 6, RoundingMode.HALF_UP).setScale(2, RoundingMode.HALF_UP), null),
            Arguments.of("USD", "GBP", baseMonetaryValue.divide(quoteEurToUsd, 6, RoundingMode.HALF_UP).multiply(quoteEurToGbp).setScale(2, RoundingMode.HALF_UP), null),
            Arguments.of("GBP", "USD", baseMonetaryValue.divide(quoteEurToGbp, 6, RoundingMode.HALF_UP).multiply(quoteEurToUsd).setScale(2, RoundingMode.HALF_UP), null),
            Arguments.of("EUR", "EUR", baseMonetaryValue.setScale(2, RoundingMode.HALF_UP), null),
            Arguments.of("eUr", "eur", baseMonetaryValue.setScale(2, RoundingMode.HALF_UP), null),
            Arguments.of("USD", "USD", baseMonetaryValue.setScale(2, RoundingMode.HALF_UP), null),
            Arguments.of("USD", "USA", baseMonetaryValue.setScale(2, RoundingMode.HALF_UP), "USA is not a valid ISO 4217 currency code")
        );
    }
}
