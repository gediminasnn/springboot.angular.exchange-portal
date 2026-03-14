package com.example.exchangeportal.service;

import com.example.exchangeportal.record.RatesClientResponse;
import com.example.exchangeportal.entity.Currency;
import com.example.exchangeportal.entity.ExchangeRate;
import com.example.exchangeportal.parser.ExchangeRateXmlParser;
import com.example.exchangeportal.record.ParsedExchangeRate;
import com.example.exchangeportal.repository.CurrencyRepository;
import com.example.exchangeportal.repository.ExchangeRateRepository;
import com.example.exchangeportal.transformer.ExchangeRateTransformer;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ExchangeRateServiceTest {

    @Mock
    private ExchangeRateRepository mockExchangeRateRepository;

    @Mock
    private CurrencyRepository mockCurrencyRepository;

    @Mock
    private HttpClient mockHttpClient;

    @Mock
    private HttpResponse<String> mockHttpResponse;

    @Mock
    private ExchangeRateXmlParser mockExchangeRateXmlParser;

    @Mock
    private ExchangeRateTransformer mockExchangeRateTransformer;

    @Mock
    private ModelMapper mockModelMapper;

    @InjectMocks
    private ExchangeRateService exchangeRateService;

    @SuppressWarnings("unchecked")
    @Test
    public void testPopulate_Success() throws Exception {
        LocalDate date = LocalDate.parse("2024-05-12");
        String xmlData = "<xml>mock</xml>";

        Currency usd = Currency.builder()
                .code("USD")
                .name("US Dollar")
                .minorUnits(2)
                .build();
        Currency eur = Currency.builder()
                .code("EUR")
                .name("Euro")
                .minorUnits(2)
                .build();

        List<ExchangeRate> rates = Arrays.asList(
                ExchangeRate.builder().currency(usd).rate(1.23).date(date).build(),
                ExchangeRate.builder().currency(eur).rate(0.85).date(date).build());

        List<ParsedExchangeRate> parsedRates = List.of(
                new ParsedExchangeRate("USD", 1.23, date),
                new ParsedExchangeRate("EUR", 0.85, date));

        when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(mockHttpResponse);
        when(mockHttpResponse.statusCode()).thenReturn(200);
        when(mockHttpResponse.body()).thenReturn(xmlData);
        when(mockExchangeRateXmlParser.parse(new RatesClientResponse(xmlData))).thenReturn(parsedRates);
        when(mockCurrencyRepository.findAll()).thenReturn(List.of(usd, eur));
        when(mockExchangeRateTransformer.toExchangeRates(eq(parsedRates), anyMap())).thenReturn(rates);

        exchangeRateService.populate();

        verify(mockHttpClient).send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class));
        verify(mockExchangeRateXmlParser).parse(new RatesClientResponse(xmlData));
        verify(mockExchangeRateTransformer).toExchangeRates(eq(parsedRates), anyMap());
        verify(mockExchangeRateRepository).saveAll(rates);
    }
}
