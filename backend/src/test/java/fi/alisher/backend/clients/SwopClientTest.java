package fi.alisher.backend.clients;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

public class SwopClientTest {
    private String baseUrl = "https://swop.cx/rest/rates";

    private SwopClient swopClient;

    @Test
    void getSingleQuote_401_noKey() throws Exception {
        swopClient = new SwopClient(baseUrl, null);
        assertThrows(
            RuntimeException.class, 
            () -> swopClient.getSingleQuote("USD"), 
            "Make sure that SWOP API Key is added and valid!"
        );
    }

    @Test
    void getSingleQuote_401_wrongKey() throws Exception {
        swopClient = new SwopClient(baseUrl, "wrong key");
        assertThrows(
            RuntimeException.class, 
            () -> swopClient.getSingleQuote("USD"), 
            "Make sure that SWOP API Key is added and valid!"
        );
    }

    @Test
    void getSingleQuote_400_other() throws Exception {
        swopClient = new SwopClient(baseUrl, System.getenv("SWOP_API_KEY"));
        assertThrows(
            RuntimeException.class, 
            () -> swopClient.getSingleQuote("USDA"), 
            "Unknown client exception!"
        );
    }
}
