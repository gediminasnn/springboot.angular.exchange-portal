package com.example.exchangeportal.transformer;

import static org.junit.jupiter.api.Assertions.*;

import com.example.exchangeportal.entity.Currency;
import com.example.exchangeportal.record.ParsedCurrency;
import java.util.List;
import org.junit.jupiter.api.Test;

class CurrencyTransformerTest {

    private final CurrencyTransformer transformer = new CurrencyTransformer();

    @Test
    void testToCurrencies_Success() {
        List<ParsedCurrency> parsedCurrencies = List.of(
                new ParsedCurrency("USD", "US Dollar", 2),
                new ParsedCurrency("JPY", "Japanese yen", 0));

        List<Currency> expected = List.of(
                Currency.builder().code("USD").name("US Dollar").minorUnits(2).build(),
                Currency.builder().code("JPY").name("Japanese yen").minorUnits(0).build());

        List<Currency> actual = transformer.toCurrencies(parsedCurrencies);

        assertEquals(expected, actual);
    }
}
