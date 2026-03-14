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
import com.example.exchangeportal.dto.ExchangeRateDto;
import com.example.exchangeportal.entity.Currency;
import com.example.exchangeportal.entity.ExchangeRate;
import com.example.exchangeportal.parser.ExchangeRateXmlParser;
import com.example.exchangeportal.record.ParsedExchangeRate;
import com.example.exchangeportal.record.RatesClientResponse;
import com.example.exchangeportal.repository.CurrencyRepository;
import com.example.exchangeportal.repository.ExchangeRateRepository;
import com.example.exchangeportal.transformer.ExchangeRateTransformer;

@Service
public class ExchangeRateService {
    @Autowired
    private ExchangeRateRepository exchangeRateRepository;

    @Autowired
    private CurrencyRepository currencyRepository;

    @Autowired
    private HttpClient httpClient;

    @Autowired
    private ExchangeRateXmlParser exchangeRateXmlParser;

    @Autowired
    private ExchangeRateTransformer exchangeRateTransformer;

    @Autowired
    private ModelMapper modelMapper;

    public void populate() throws IOException, InterruptedException, SAXException, ParserConfigurationException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://www.lb.lt/webservices/FxRates/FxRates.asmx/getFxRates"))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString("tp=LT&dt=" + LocalDate.now()))
                .build();

        HttpResponse<String> httpResponse = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (httpResponse.statusCode() != 200) {
            throw new RuntimeException("Bad response: expected 200 http status.");
        }

        RatesClientResponse response = new RatesClientResponse(httpResponse.body());
        List<ParsedExchangeRate> parsedRates = exchangeRateXmlParser.parse(response);
        Map<String, Currency> currencyMap = currencyRepository.findAll().stream()
                .collect(Collectors.toMap(Currency::getCode, c -> c));
        List<ExchangeRate> rates = exchangeRateTransformer.toExchangeRates(parsedRates, currencyMap);
        exchangeRateRepository.saveAll(rates);
    }

    public List<ExchangeRateDto> get() {
        return exchangeRateRepository.findAll()
                .stream()
                .map(rate -> modelMapper.map(rate, ExchangeRateDto.class))
                .collect(Collectors.toList());
    }
}
