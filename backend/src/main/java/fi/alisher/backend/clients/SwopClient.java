package fi.alisher.backend.clients;

import java.util.Objects;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import fi.alisher.backend.models.SwopRate;

@Component
public class SwopClient {
    private RestClient restClient;

    public SwopClient(
        @Value("${swop.url}") String baseUrl,
        @Value("${swop.apikey}") String apiKey
    ) {
        this.restClient = RestClient.builder()
            .baseUrl(baseUrl)
            .defaultHeader("Authorization", String.format("ApiKey %s", apiKey))
            .build();
    }

    public SwopRate getSingleRate(String targetCurrency) throws Exception {
        SwopRate body = this.restClient.get()
            .uri(String.format("/EUR/%s", targetCurrency))
            .retrieve()
            .body(SwopRate.class);

        if (Objects.nonNull(body)) {
            return body;
        } else {
            throw new Exception(String.format("No quote for EUR-%s", targetCurrency));
        }
    }
}
