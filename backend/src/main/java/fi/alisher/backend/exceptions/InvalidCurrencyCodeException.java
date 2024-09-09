package fi.alisher.backend.exceptions;

public class InvalidCurrencyCodeException extends Exception {
    public InvalidCurrencyCodeException(String message) {
        super(message);
    }
}
