package fi.alisher.backend.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import fi.alisher.backend.clients.SwopClient;
import fi.alisher.backend.models.ExchangeRateRequestBody;

public class CurrencyConversionServiceTest {

    @Mock
    private SwopClient swopClient;

    private CurrencyConversionService conversionService;

    private static BigDecimal baseMonetaryValue = new BigDecimal("100");
    private static BigDecimal quoteEurToUsd = new BigDecimal("1.110414"); // EUR to USD on 06.09.2024
    private static BigDecimal quoteEurToGbp = new BigDecimal("0.843216"); // EUR to GBP on 06.09.2024

    @BeforeEach
    public void before() {
        MockitoAnnotations.openMocks(this);
        conversionService = new CurrencyConversionService(swopClient);
    }
    
    @ParameterizedTest
    @MethodSource("requestBodyData")
    public void convertCurrency(String sourceCurrency, String targetCurrency, BigDecimal expectedValue) throws Exception {
        ExchangeRateRequestBody body = new ExchangeRateRequestBody();
        body.setSourceCurrency(sourceCurrency);
        body.setTargetCurrency(targetCurrency);
        body.setMonetaryValue(baseMonetaryValue);

        when(swopClient.getSingleQuote("USD")).thenReturn(quoteEurToUsd);
        when(swopClient.getSingleQuote("GBP")).thenReturn(quoteEurToGbp);

        assertEquals(expectedValue, conversionService.convertCurrency(body));
    }

    private static Stream<Arguments> requestBodyData() {
        return Stream.of(
            Arguments.of("EUR", "USD", baseMonetaryValue.multiply(quoteEurToUsd).setScale(2, RoundingMode.HALF_UP)),
            Arguments.of("eur", "USD", baseMonetaryValue.multiply(quoteEurToUsd).setScale(2, RoundingMode.HALF_UP)),
            Arguments.of( "USD", "EUR", baseMonetaryValue.divide(quoteEurToUsd, 6, RoundingMode.HALF_UP).setScale(2, RoundingMode.HALF_UP)),
            Arguments.of("USD", "GBP", baseMonetaryValue.divide(quoteEurToUsd, 6, RoundingMode.HALF_UP).multiply(quoteEurToGbp).setScale(2, RoundingMode.HALF_UP)),
            Arguments.of("GBP", "USD", baseMonetaryValue.divide(quoteEurToGbp, 6, RoundingMode.HALF_UP).multiply(quoteEurToUsd).setScale(2, RoundingMode.HALF_UP)),
            Arguments.of("EUR", "EUR", baseMonetaryValue.setScale(2, RoundingMode.HALF_UP)),
            Arguments.of("eUr", "eur", baseMonetaryValue.setScale(2, RoundingMode.HALF_UP)),
            Arguments.of("USD", "USD", baseMonetaryValue.setScale(2, RoundingMode.HALF_UP))
        );
    }
}
