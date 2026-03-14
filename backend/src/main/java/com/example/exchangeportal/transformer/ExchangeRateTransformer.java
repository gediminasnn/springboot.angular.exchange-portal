package com.example.exchangeportal.transformer;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.example.exchangeportal.entity.Currency;
import com.example.exchangeportal.entity.ExchangeRate;
import com.example.exchangeportal.record.ParsedExchangeRate;

@Component
public class ExchangeRateTransformer {

    public List<ExchangeRate> toExchangeRates(List<ParsedExchangeRate> parsedRates,
            Map<String, Currency> currencyMap) {
        return parsedRates.stream()
                .map(parsed -> toExchangeRate(parsed, currencyMap))
                .toList();
    }

    private ExchangeRate toExchangeRate(ParsedExchangeRate parsed, Map<String, Currency> currencyMap) {
        Currency currency = currencyMap.get(parsed.currencyCode());
        if (currency == null) {
            throw new RuntimeException("Currency not found for code: " + parsed.currencyCode());
        }

        return ExchangeRate.builder()
                .currency(currency)
                .rate(parsed.rate())
                .date(parsed.date())
                .build();
    }
}
