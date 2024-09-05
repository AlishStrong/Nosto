package fi.alisher.backend.controllers;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import java.math.BigDecimal;

import java.util.stream.Stream;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = {ExchangeRateController.class})
public class ExchangeRateControllerTest {
    
    @Autowired
    private MockMvc mockMvc;

    @ParameterizedTest
    @MethodSource("requestData")
    public void testPostMethodNameWithMissingProperties(String requestBodyJson, int responseStatus, String responseBody) throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .post("/api")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBodyJson))
                .andExpect(status().is(responseStatus))
                .andExpect(content().string(responseBody));
    }

    private static Stream<Arguments> requestData() {
        // correct values
        String correctSourceCurrency = "EUR";
        String correctTargetCurrency = "USD";
        BigDecimal correctMonetaryValue_whole = new BigDecimal(30);
        BigDecimal correctMonetaryValue_cented = new BigDecimal("30.15");
        int okResponseStatus = HttpStatus.OK.value();
        String okResponseBody_whole = correctMonetaryValue_whole.toPlainString();
        String okResponseBody_cented = correctMonetaryValue_cented.toPlainString();

        String irrelevantField = "\"irrelevant\": \"field\"";

        // incorrect values 
        String incorrectSourceCurrency_short = "EU";
        String incorrectSourceCurrency_long = "EURO";
        String incorrectSourceCurrency_null = null;
        String incorrectSourceCurrency_nullstr = "null";
        String incorrectSourceCurrency_empty = " ";

        BigDecimal incorrectMonetaryValue_whole = new BigDecimal(0);
        BigDecimal incorrectMonetaryValue_cented = new BigDecimal(0.0);
        BigDecimal incorrectMonetaryValue_negative = new BigDecimal(-0.1);
        BigDecimal incorrectMonetaryValue_null = null;
        String incorrectMonetaryValue_nullstr = "null";
        String incorrectMonetaryValue_empty = " ";
        String incorrectMonetaryValue_notnumeric = "not numeric";

        int errorResponseStatus = HttpStatus.I_AM_A_TEAPOT.value();
        String errorResponseBody = "Request body was invalid!";


        return Stream.of(
            // correct
            Arguments.of(String.format("{ \"sourceCurrency\": \"%s\", \"targetCurrency\": \"%s\", \"monetaryValue\": %s }", correctSourceCurrency, correctTargetCurrency, correctMonetaryValue_whole), okResponseStatus, okResponseBody_whole),
            Arguments.of(String.format("{ \"sourceCurrency\": \"%s\", \"targetCurrency\": \"%s\", \"monetaryValue\": \"%s\" }", correctSourceCurrency, correctTargetCurrency, correctMonetaryValue_whole), okResponseStatus, okResponseBody_whole),
            Arguments.of(String.format("{ \"sourceCurrency\": \"%s\", \"targetCurrency\": \"%s\", \"monetaryValue\": %s }", correctSourceCurrency, correctTargetCurrency, correctMonetaryValue_cented), okResponseStatus, okResponseBody_cented),
            Arguments.of(String.format("{ \"sourceCurrency\": \"%s\", \"targetCurrency\": \"%s\", \"monetaryValue\": %s, %s }", correctSourceCurrency, correctTargetCurrency, correctMonetaryValue_cented, irrelevantField), okResponseStatus, okResponseBody_cented),

            // incorrect currency values 
            Arguments.of(String.format("{ \"sourceCurrency\": \"%s\", \"targetCurrency\": \"%s\", \"monetaryValue\": %s }", incorrectSourceCurrency_short, correctTargetCurrency, correctMonetaryValue_whole), errorResponseStatus, errorResponseBody),
            Arguments.of(String.format("{ \"sourceCurrency\": \"%s\", \"targetCurrency\": \"%s\", \"monetaryValue\": %s }", incorrectSourceCurrency_long, correctTargetCurrency, correctMonetaryValue_whole), errorResponseStatus, errorResponseBody),
            Arguments.of(String.format("{ \"sourceCurrency\": \"%s\", \"targetCurrency\": \"%s\", \"monetaryValue\": %s }", incorrectSourceCurrency_null, correctTargetCurrency, correctMonetaryValue_whole), errorResponseStatus, errorResponseBody),
            Arguments.of(String.format("{ \"sourceCurrency\": \"%s\", \"targetCurrency\": \"%s\", \"monetaryValue\": %s }", incorrectSourceCurrency_nullstr, correctTargetCurrency, correctMonetaryValue_whole), errorResponseStatus, errorResponseBody),
            Arguments.of(String.format("{ \"sourceCurrency\": \"%s\", \"targetCurrency\": \"%s\", \"monetaryValue\": %s }", incorrectSourceCurrency_empty, correctTargetCurrency, correctMonetaryValue_whole), errorResponseStatus, errorResponseBody),
            Arguments.of(String.format("{ \"targetCurrency\": \"%s\", \"monetaryValue\": %s }", correctTargetCurrency, correctMonetaryValue_whole), errorResponseStatus, errorResponseBody),
            Arguments.of(String.format("{ \"sourceCurrency\": \"%s\", \"monetaryValue\": %s }", correctSourceCurrency, correctMonetaryValue_whole), errorResponseStatus, errorResponseBody),

            // incorrect monetary values
            Arguments.of(String.format("{ \"sourceCurrency\": \"%s\", \"targetCurrency\": \"%s\", \"monetaryValue\": %s }", correctSourceCurrency, correctTargetCurrency, incorrectMonetaryValue_whole), errorResponseStatus, errorResponseBody),
            Arguments.of(String.format("{ \"sourceCurrency\": \"%s\", \"targetCurrency\": \"%s\", \"monetaryValue\": %s }", correctSourceCurrency, correctTargetCurrency, incorrectMonetaryValue_cented), errorResponseStatus, errorResponseBody),
            Arguments.of(String.format("{ \"sourceCurrency\": \"%s\", \"targetCurrency\": \"%s\", \"monetaryValue\": %s }", correctSourceCurrency, correctTargetCurrency, incorrectMonetaryValue_negative), errorResponseStatus, errorResponseBody),
            Arguments.of(String.format("{ \"sourceCurrency\": \"%s\", \"targetCurrency\": \"%s\", \"monetaryValue\": %s }", correctSourceCurrency, correctTargetCurrency, incorrectMonetaryValue_null), errorResponseStatus, errorResponseBody),
            Arguments.of(String.format("{ \"sourceCurrency\": \"%s\", \"targetCurrency\": \"%s\", \"monetaryValue\": %s }", correctSourceCurrency, correctTargetCurrency, incorrectMonetaryValue_nullstr), errorResponseStatus, errorResponseBody),
            Arguments.of(String.format("{ \"sourceCurrency\": \"%s\", \"targetCurrency\": \"%s\", \"monetaryValue\": %s }", correctSourceCurrency, correctTargetCurrency, incorrectMonetaryValue_empty), errorResponseStatus, errorResponseBody),
            Arguments.of(String.format("{ \"sourceCurrency\": \"%s\", \"targetCurrency\": \"%s\", \"monetaryValue\": %s }", correctSourceCurrency, correctTargetCurrency, incorrectMonetaryValue_notnumeric), errorResponseStatus, errorResponseBody),
            Arguments.of(String.format("{ \"sourceCurrency\": \"%s\", \"targetCurrency\": \"%s\", \"monetaryValue\": \"%s\" }", correctSourceCurrency, correctTargetCurrency, incorrectMonetaryValue_notnumeric), errorResponseStatus, errorResponseBody),
            Arguments.of(String.format("{ \"sourceCurrency\": \"%s\", \"targetCurrency\": \"%s\" }", correctSourceCurrency, correctTargetCurrency), errorResponseStatus, errorResponseBody)
        );
    }
}
