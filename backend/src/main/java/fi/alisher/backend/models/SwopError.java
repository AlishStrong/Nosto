package fi.alisher.backend.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class SwopError {
    private Error error;

    @Data
    public static class Error {
        private String message;
    }
}
