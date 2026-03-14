package com.example.exchangeportal.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import com.example.exchangeportal.record.CurrencyClientResponse;
import com.example.exchangeportal.dto.CurrencyDto;
import com.example.exchangeportal.dto.ExchangeRateDto;
import com.example.exchangeportal.entity.Currency;
import com.example.exchangeportal.entity.ExchangeRate;
import com.example.exchangeportal.parser.CurrencyXmlParser;
import com.example.exchangeportal.parser.ExchangeRateXmlParser;
import com.example.exchangeportal.record.ParsedCurrency;
import com.example.exchangeportal.repository.CurrencyRepository;
import com.example.exchangeportal.repository.ExchangeRateRepository;
import com.example.exchangeportal.transformer.CurrencyTransformer;
import com.example.exchangeportal.transformer.ExchangeRateTransformer;
import com.example.exchangeportal.util.DateUtils;
import org.modelmapper.ModelMapper;

@SpringBootTest
public class CurrencyServiceTest {

    @MockBean
    private CurrencyRepository mockCurrencyRepository;

    @MockBean
    private HttpClient mockHttpClient;

    @MockBean
    private HttpResponse<String> mockHttpResponse;

    @MockBean
    private CurrencyXmlParser mockCurrencyXmlParser;

    @MockBean
    private CurrencyTransformer mockCurrencyTransformer;

    @MockBean
    private ExchangeRateRepository mockExchangeRateRepository;

    @MockBean
    private ExchangeRateXmlParser mockExchangeRateXmlParser;

    @MockBean
    private ExchangeRateTransformer mockExchangeRateTransformer;

    @MockBean
    private DateUtils mockDateUtils;

    @MockBean
    private ModelMapper mockModelMapper;

    @Autowired
    private CurrencyService currencyService;

    @BeforeEach
    void setUp() {
        Mockito.reset(mockCurrencyRepository, mockHttpClient, mockHttpResponse, mockCurrencyXmlParser, mockCurrencyTransformer);
    }

    @SuppressWarnings("unchecked")
    @Test
    void populate_Success() throws Exception {
        String xmlData = "<xml>mock</xml>";
        List<ParsedCurrency> parsedCurrencies = List.of(
                new ParsedCurrency("USD", "US Dollar", 2));
        List<Currency> currencies = Arrays.asList(Currency.builder()
                .id(1L)
                .code("USD")
                .name("US Dollar")
                .minorUnits(2)
                .build());

        when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(mockHttpResponse);
        when(mockHttpResponse.statusCode()).thenReturn(200);
        when(mockHttpResponse.body()).thenReturn(xmlData);
        when(mockCurrencyXmlParser.parse(new CurrencyClientResponse(xmlData))).thenReturn(parsedCurrencies);
        when(mockCurrencyTransformer.toCurrencies(parsedCurrencies)).thenReturn(currencies);

        currencyService.populate();

        verify(mockHttpClient).send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class));
        verify(mockCurrencyXmlParser).parse(new CurrencyClientResponse(xmlData));
        verify(mockCurrencyTransformer).toCurrencies(parsedCurrencies);
        verify(mockCurrencyRepository).saveAll(currencies);
    }

    @Test
    public void testGet_Success() throws Exception {
        Currency expectedCurrency = Currency.builder()
                .id(1L)
                .code("USD")
                .name("US Dollar")
                .minorUnits(2)
                .build();

        LocalDate fromDate = LocalDate.of(2024, 5, 1);
        LocalDate toDate = LocalDate.of(2024, 5, 5);

        List<ExchangeRate> exchangeRates = Arrays.asList(
                ExchangeRate.builder().currency(expectedCurrency).rate(1.23).date(fromDate).build(),
                ExchangeRate.builder().currency(expectedCurrency).rate(1.24).date(LocalDate.of(2024, 5, 2)).build());

        when(mockCurrencyRepository.findById(1L)).thenReturn(Optional.of(expectedCurrency));
        when(mockExchangeRateRepository.findAll(expectedCurrency, fromDate, toDate)).thenReturn(exchangeRates);
        when(mockDateUtils.findMissingDates(exchangeRates, fromDate, toDate)).thenReturn(List.of());

        CurrencyDto expectedCurrencyDto = CurrencyDto.builder()
                .id(1L).code("USD").name("US Dollar").build();
        when(mockModelMapper.map(expectedCurrency, CurrencyDto.class)).thenReturn(expectedCurrencyDto);

        ExchangeRateDto rateDto1 = ExchangeRateDto.builder().rate(1.24).date(LocalDate.of(2024, 5, 2)).build();
        ExchangeRateDto rateDto2 = ExchangeRateDto.builder().rate(1.23).date(fromDate).build();
        when(mockModelMapper.map(exchangeRates.get(0), ExchangeRateDto.class)).thenReturn(rateDto2);
        when(mockModelMapper.map(exchangeRates.get(1), ExchangeRateDto.class)).thenReturn(rateDto1);

        CurrencyDto actualCurrencyDto = currencyService.get(1L, fromDate, toDate);

        expectedCurrencyDto.setExchangeRates(List.of(rateDto1, rateDto2));
        assertEquals(expectedCurrencyDto, actualCurrencyDto);

        verify(mockExchangeRateRepository).findAll(expectedCurrency, fromDate, toDate);
    }
}
