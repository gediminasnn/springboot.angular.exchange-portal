package com.example.exchangeportal.service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.xml.parsers.ParserConfigurationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.modelmapper.ModelMapper;
import org.xml.sax.SAXException;
import com.example.exchangeportal.dto.CurrencyDto;
import com.example.exchangeportal.dto.ExchangeRateDto;
import com.example.exchangeportal.entity.Currency;
import com.example.exchangeportal.entity.ExchangeRate;
import com.example.exchangeportal.parser.CurrencyXmlParser;
import com.example.exchangeportal.parser.ExchangeRateXmlParser;
import com.example.exchangeportal.record.CurrencyClientResponse;
import com.example.exchangeportal.record.ParsedCurrency;
import com.example.exchangeportal.record.ParsedExchangeRate;
import com.example.exchangeportal.record.RatesClientResponse;
import com.example.exchangeportal.repository.CurrencyRepository;
import com.example.exchangeportal.repository.ExchangeRateRepository;
import com.example.exchangeportal.transformer.CurrencyTransformer;
import com.example.exchangeportal.transformer.ExchangeRateTransformer;
import com.example.exchangeportal.util.DateUtils;

@Service
public class CurrencyService {
    @Autowired
    private CurrencyRepository currencyRepository;

    @Autowired
    private HttpClient httpClient;

    @Autowired
    private CurrencyXmlParser currencyXmlParser;

    @Autowired
    private CurrencyTransformer currencyTransformer;

    @Autowired
    private ExchangeRateRepository exchangeRateRepository;

    @Autowired
    private ExchangeRateXmlParser exchangeRateXmlParser;

    @Autowired
    private ExchangeRateTransformer exchangeRateTransformer;

    @Autowired
    private DateUtils dateUtils;

    @Autowired
    private ModelMapper modelMapper;

    public void populate() throws IOException, InterruptedException, SAXException, ParserConfigurationException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://www.lb.lt/webservices/FxRates/FxRates.asmx/getCurrencyList?"))
                .header("Accept", "application/xml")
                .GET()
                .build();
        HttpResponse<String> httpResponse = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (httpResponse.statusCode() != 200) {
            throw new RuntimeException("Bad response: expected 200 http status.");
        }

        CurrencyClientResponse response = new CurrencyClientResponse(httpResponse.body());
        List<ParsedCurrency> parsedCurrencies = currencyXmlParser.parse(response);
        List<Currency> currencies = currencyTransformer.toCurrencies(parsedCurrencies);
        currencyRepository.saveAll(currencies);
    }

    public CurrencyDto get(Long currencyId, LocalDate fromDate, LocalDate toDate)
            throws IOException, InterruptedException, SAXException, ParserConfigurationException {
        Currency currency = currencyRepository.findById(currencyId)
                .orElseThrow(() -> new RuntimeException("Couldn't find currency with id: " + currencyId));

        List<ExchangeRate> exchangeRates = exchangeRateRepository.findAll(currency, fromDate, toDate);

        List<LocalDate> missingDates = dateUtils.findMissingDates(exchangeRates, fromDate, toDate);
        if (!missingDates.isEmpty()) {
            String requestBody = "tp=LT&ccy=" + currency.getCode()
                    + "&dtFrom=" + fromDate + "&dtTo=" + toDate;
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://www.lb.lt/webservices/FxRates/FxRates.asmx/getFxRatesForCurrency"))
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();
            HttpResponse<String> httpResponse = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (httpResponse.statusCode() != 200) {
                throw new RuntimeException("Bad response: expected 200 http status.");
            }

            RatesClientResponse ratesResponse = new RatesClientResponse(httpResponse.body());
            List<ParsedExchangeRate> parsedRates = exchangeRateXmlParser.parse(ratesResponse);
            Map<String, Currency> currencyMap = currencyRepository.findAll().stream()
                    .collect(Collectors.toMap(Currency::getCode, c -> c));
            List<ExchangeRate> transformedRates = exchangeRateTransformer.toExchangeRates(parsedRates, currencyMap);
            List<ExchangeRate> missingRates = transformedRates.stream()
                    .filter(rate -> missingDates.contains(rate.getDate()))
                    .collect(Collectors.toList());

            exchangeRateRepository.saveAll(missingRates);
            exchangeRates.addAll(missingRates);
        }

        exchangeRates.sort((rate1, rate2) -> rate2.getDate().compareTo(rate1.getDate()));

        CurrencyDto currencyDto = modelMapper.map(currency, CurrencyDto.class);
        List<ExchangeRateDto> exchangeRateDtos = exchangeRates.stream()
                .map(rate -> modelMapper.map(rate, ExchangeRateDto.class))
                .collect(Collectors.toList());
        currencyDto.setExchangeRates(exchangeRateDtos);
        return currencyDto;
    }
}
