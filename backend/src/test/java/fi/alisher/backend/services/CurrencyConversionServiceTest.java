package fi.alisher.backend.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import fi.alisher.backend.models.ExchangeRateRequestBody;

public class CurrencyConversionServiceTest {

    private CurrencyConversionService conversionService;

    @BeforeEach
    public void before() {
        conversionService = new CurrencyConversionService();
    }
    
    @ParameterizedTest
    @MethodSource("requestBodyData")
    public void testConvertCurrency(String sourceCurrency, String targetCurrency, BigDecimal expectedValue) {
        ExchangeRateRequestBody body = new ExchangeRateRequestBody();
        body.setSourceCurrency(sourceCurrency);
        body.setTargetCurrency(targetCurrency);
        body.setMonetaryValue(new BigDecimal("100"));

        assertEquals(expectedValue, conversionService.convertCurrency(body));
    }

    private static Stream<Arguments> requestBodyData() {
        BigDecimal baseMonetaryValue = new BigDecimal("100");
        BigDecimal quoteOne = new BigDecimal("1.110414"); // EUR to USD on 06.09.2024
        BigDecimal quoteTwo = new BigDecimal("0.843216"); // EUR to GBP on 06.09.2024

        return Stream.of(
            Arguments.of("EUR", "USD", baseMonetaryValue.multiply(quoteOne).setScale(2, RoundingMode.HALF_UP)),
            Arguments.of("eur", "USD", baseMonetaryValue.multiply(quoteOne).setScale(2, RoundingMode.HALF_UP)),
            Arguments.of( "USD", "EUR", baseMonetaryValue.divide(quoteOne, 6, RoundingMode.HALF_UP).setScale(2, RoundingMode.HALF_UP)),
            Arguments.of("USD", "GBP", baseMonetaryValue.divide(quoteOne, 6, RoundingMode.HALF_UP).multiply(quoteTwo).setScale(2, RoundingMode.HALF_UP)),
            Arguments.of("EUR", "EUR", baseMonetaryValue.setScale(2, RoundingMode.HALF_UP)),
            Arguments.of("eUr", "eur", baseMonetaryValue.setScale(2, RoundingMode.HALF_UP)),
            Arguments.of("USD", "USD", baseMonetaryValue.setScale(2, RoundingMode.HALF_UP))
        );
    }
}
