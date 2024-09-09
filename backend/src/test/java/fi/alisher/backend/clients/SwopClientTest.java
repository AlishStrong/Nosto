package fi.alisher.backend.clients;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.web.client.HttpClientErrorException;

public class SwopClientTest {
    private String baseUrl = "https://swop.cx/rest/rates";

    private SwopClient swopClient;

    @ParameterizedTest
    @MethodSource("requestData")
    public void getSingleRate(String apiKey, String targetCurrency) throws Exception {
        swopClient = new SwopClient(baseUrl, apiKey);
        assertThrows(
            HttpClientErrorException.class, 
            () -> swopClient.getSingleRate(targetCurrency)
        );
    }

    private static Stream<Arguments> requestData() {
        return Stream.of(
            Arguments.of(null, "USD"),
            Arguments.of("wrong key", "USD"),
            Arguments.of(System.getenv("SWOP_API_KEY"), "USDA")
        );
    }
}
