package com.example.exchangeportal.transformer;

import java.util.List;

import org.springframework.stereotype.Component;

import com.example.exchangeportal.entity.Currency;
import com.example.exchangeportal.record.ParsedCurrency;

@Component
public class CurrencyTransformer {

    public List<Currency> toCurrencies(List<ParsedCurrency> parsedCurrencies) {
        return parsedCurrencies.stream()
                .map(this::toCurrency)
                .toList();
    }

    private Currency toCurrency(ParsedCurrency parsed) {
        return Currency.builder()
                .code(parsed.code())
                .name(parsed.name())
                .minorUnits(parsed.minorUnits())
                .build();
    }
}
