package fi.alisher.backend.controllers;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.servlet.LocaleResolver;

import fi.alisher.backend.models.ExchangeRateRequestBody;
import fi.alisher.backend.services.CurrencyConversionService;
import jakarta.servlet.http.HttpServletRequest;

import java.math.BigDecimal;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = {ExchangeRateController.class})
public class ExchangeRateControllerTest {
    
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LocaleResolver localeResolver;

    @MockBean
    private CurrencyConversionService conversionService;

    @ParameterizedTest
    @MethodSource("requestSuccessData")
    public void convertCurrency_successCases(
        String requestBodyJson, 
        String convertedValue, 
        String localeFormattedConvertedValue,
        Locale locale
    ) throws Exception {
        when(conversionService.convertCurrency(any(ExchangeRateRequestBody.class))).thenReturn(new BigDecimal(convertedValue));
        when(localeResolver.resolveLocale(any(HttpServletRequest.class))).thenReturn(Objects.nonNull(locale) ? locale : Locale.US);
        mockMvc.perform(MockMvcRequestBuilders
            .post("/api")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBodyJson))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.localeFormattedConvertedValue").value(localeFormattedConvertedValue))
            .andExpect(jsonPath("$.locale").value(Objects.nonNull(locale) ? locale.toString() : Locale.US.toString()));
    }

    @ParameterizedTest
    @MethodSource("requestErrorData")
    public void convertCurrency_errorCases(String requestBodyJson, int responseStatus, String responseBody) throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .post("/api")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBodyJson))
                .andExpect(status().is(responseStatus))
                .andExpect(content().string(responseBody));
    }

    private static Stream<Arguments> requestSuccessData() {
        return Stream.of(
            Arguments.of(prepareRequestBodyJson("EUR", "USD", "12345612.789"), "12345612.789", "$12,345,612.79", null),
            Arguments.of(prepareRequestBodyJson("EUR", "GBP", "1.15"), "1.15", "£1.15", null),
            Arguments.of(prepareRequestBodyJson("EUR", "GBP", "12345612.789"), "12345612.789", "12.345.612,79 £", Locale.GERMANY),
            Arguments.of(prepareRequestBodyJson("EUR", "JPY", "123456.789"), "123456.789", "123.457 ¥", Locale.GERMANY),
            Arguments.of(prepareRequestBodyJson("EUR", "JPY", "1.15"), "1.15", "￥1", Locale.JAPAN),
            Arguments.of(prepareRequestBodyJson("EUR", "USD", "1.15"), "1.15", "$1.15", Locale.JAPAN)
        );
    }

    private static Stream<Arguments> requestErrorData() {
        // correct values
        String correctSourceCurrency = "EUR";
        String correctTargetCurrency = "USD";
        String correctMonetaryValue_whole = "30";

        // incorrect values 
        String incorrectSourceCurrency_short = "EU";
        String incorrectSourceCurrency_long = "EURO";
        String incorrectSourceCurrency_null = null;
        String incorrectSourceCurrency_nullstr = "null";
        String incorrectSourceCurrency_empty = " ";
        String incorrectSourceCurrency_nonletter = "123";

        String incorrectMonetaryValue_whole = "0";
        String incorrectMonetaryValue_cented = "0.0";
        String incorrectMonetaryValue_negative = "-0.1";
        String incorrectMonetaryValue_null = null;
        String incorrectMonetaryValue_nullstr = "null";
        String incorrectMonetaryValue_empty = " ";
        String incorrectMonetaryValue_notnumeric = "not numeric";

        int errorResponseStatus = HttpStatus.BAD_REQUEST.value();
        String errorResponseBody = "Request body was invalid!";

        return Stream.of(
            // incorrect currency values 
            Arguments.of(prepareRequestBodyJson(incorrectSourceCurrency_short, correctTargetCurrency, correctMonetaryValue_whole), errorResponseStatus, errorResponseBody),
            Arguments.of(prepareRequestBodyJson(incorrectSourceCurrency_long, correctTargetCurrency, correctMonetaryValue_whole), errorResponseStatus, errorResponseBody),
            Arguments.of(prepareRequestBodyJson(incorrectSourceCurrency_null, correctTargetCurrency, correctMonetaryValue_whole), errorResponseStatus, errorResponseBody),
            Arguments.of(prepareRequestBodyJson(incorrectSourceCurrency_nullstr, correctTargetCurrency, correctMonetaryValue_whole), errorResponseStatus, errorResponseBody),
            Arguments.of(prepareRequestBodyJson(incorrectSourceCurrency_empty, correctTargetCurrency, correctMonetaryValue_whole), errorResponseStatus, errorResponseBody),
            Arguments.of(prepareRequestBodyJson(incorrectSourceCurrency_nonletter, correctTargetCurrency, correctMonetaryValue_whole), errorResponseStatus, errorResponseBody),
            Arguments.of(String.format("{ \"targetCurrency\": \"%s\", \"monetaryValue\": %s }", correctTargetCurrency, correctMonetaryValue_whole), errorResponseStatus, errorResponseBody),
            Arguments.of(String.format("{ \"sourceCurrency\": \"%s\", \"monetaryValue\": %s }", correctSourceCurrency, correctMonetaryValue_whole), errorResponseStatus, errorResponseBody),

            // incorrect monetary values
            Arguments.of(prepareRequestBodyJson(correctSourceCurrency, correctTargetCurrency, incorrectMonetaryValue_whole), errorResponseStatus, errorResponseBody),
            Arguments.of(prepareRequestBodyJson(correctSourceCurrency, correctTargetCurrency, incorrectMonetaryValue_cented), errorResponseStatus, errorResponseBody),
            Arguments.of(prepareRequestBodyJson(correctSourceCurrency, correctTargetCurrency, incorrectMonetaryValue_negative), errorResponseStatus, errorResponseBody),
            Arguments.of(prepareRequestBodyJson(correctSourceCurrency, correctTargetCurrency, incorrectMonetaryValue_null), errorResponseStatus, errorResponseBody),
            Arguments.of(prepareRequestBodyJson(correctSourceCurrency, correctTargetCurrency, incorrectMonetaryValue_nullstr), errorResponseStatus, errorResponseBody),
            Arguments.of(prepareRequestBodyJson(correctSourceCurrency, correctTargetCurrency, incorrectMonetaryValue_empty), errorResponseStatus, errorResponseBody),
            Arguments.of(prepareRequestBodyJson(correctSourceCurrency, correctTargetCurrency, incorrectMonetaryValue_notnumeric), errorResponseStatus, errorResponseBody),
            Arguments.of(prepareRequestBodyJson(correctSourceCurrency, correctTargetCurrency, incorrectMonetaryValue_notnumeric), errorResponseStatus, errorResponseBody),
            Arguments.of(String.format("{ \"sourceCurrency\": \"%s\", \"targetCurrency\": \"%s\" }", correctSourceCurrency, correctTargetCurrency), errorResponseStatus, errorResponseBody)
        );
    }

    private static String prepareRequestBodyJson(
        String sourceCurrency,
        String targetCurrency,
        String monetaryValue
    ) {
        return String.format(
            "{ \"sourceCurrency\": \"%s\", \"targetCurrency\": \"%s\", \"monetaryValue\": %s }", 
            sourceCurrency, 
            targetCurrency, 
            monetaryValue
        );
    }
}
