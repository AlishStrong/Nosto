package fi.alisher.backend.controllers;

import org.springframework.web.bind.annotation.RestController;

import fi.alisher.backend.models.ExchangeRateRequestBody;
import fi.alisher.backend.models.ExchangeRateResponseBody;
import fi.alisher.backend.services.CurrencyConversionService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.Currency;
import java.util.Locale;


@RestController
@RequestMapping("/api")
public class ExchangeRateController {

    private LocaleResolver localeResolver;
    private CurrencyConversionService conversionService;

    public ExchangeRateController(
        LocaleResolver localeResolver,
        CurrencyConversionService conversionService
    ) {
        this.localeResolver = localeResolver;
        this.conversionService = conversionService;
    }
    
    @PostMapping
    public ExchangeRateResponseBody convertCurrency(
        HttpServletRequest request,
        @Valid @RequestBody ExchangeRateRequestBody body
    ) throws Exception {
        BigDecimal convertedValue = conversionService.convertCurrency(body);
        Locale locale = localeResolver.resolveLocale(request);
        String targetCurrency = body.getTargetCurrency();
        String localeFormattedConvertedValue = formatConvertedValueByLocale(convertedValue, targetCurrency, locale);
        ExchangeRateResponseBody responseBody = new ExchangeRateResponseBody(body, convertedValue, localeFormattedConvertedValue, locale);
        return responseBody;
    }

    private String formatConvertedValueByLocale(BigDecimal convertedValue, String targetCurrency, Locale locale) {
        NumberFormat formatter = NumberFormat.getCurrencyInstance(locale);
        Currency currency = Currency.getInstance(targetCurrency);
        formatter.setCurrency(currency);
        formatter.setRoundingMode(RoundingMode.HALF_UP);
        formatter.setMaximumFractionDigits(currency.getDefaultFractionDigits());
        return formatter.format(convertedValue);
    }
}
