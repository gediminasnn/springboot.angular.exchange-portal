package com.example.exchangeportal.transformer;

import static org.junit.jupiter.api.Assertions.*;

import com.example.exchangeportal.entity.Currency;
import com.example.exchangeportal.entity.ExchangeRate;
import com.example.exchangeportal.record.ParsedExchangeRate;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

class ExchangeRateTransformerTest {

    private final ExchangeRateTransformer transformer = new ExchangeRateTransformer();

    @Test
    void testToExchangeRates_Success() {
        LocalDate date = LocalDate.of(2024, 5, 10);
        Currency currencyUSD = Currency.builder().id(1L).code("USD").name("United States dollar").minorUnits(2).build();
        Currency currencyJPY = Currency.builder().id(2L).code("JPY").name("Japanese yen").minorUnits(2).build();

        List<ParsedExchangeRate> parsedRates = List.of(
                new ParsedExchangeRate("USD", 1.0926, date),
                new ParsedExchangeRate("JPY", 142.75, date));

        Map<String, Currency> currencyMap = Map.of("USD", currencyUSD, "JPY", currencyJPY);

        List<ExchangeRate> expected = List.of(
                ExchangeRate.builder().currency(currencyUSD).rate(1.0926).date(date).build(),
                ExchangeRate.builder().currency(currencyJPY).rate(142.75).date(date).build());

        List<ExchangeRate> actual = transformer.toExchangeRates(parsedRates, currencyMap);

        assertEquals(expected, actual);
    }

    @Test
    void testToExchangeRates_NonExistingCurrencyCode() {
        LocalDate date = LocalDate.of(2024, 5, 10);
        List<ParsedExchangeRate> parsedRates = List.of(
                new ParsedExchangeRate("XYZ", 142.75, date));

        assertThrows(RuntimeException.class, () -> transformer.toExchangeRates(parsedRates, Map.of()));
    }
}
