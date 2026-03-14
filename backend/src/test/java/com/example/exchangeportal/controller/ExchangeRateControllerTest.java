package com.example.exchangeportal.controller;

import com.example.exchangeportal.dto.CurrencyDto;
import com.example.exchangeportal.dto.ExchangeRateDto;
import com.example.exchangeportal.service.ExchangeRateService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ExchangeRateController.class)
public class ExchangeRateControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ExchangeRateService exchangeRateService;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ModelMapper mockModelMapper;

    @Test
    public void testShow_ReturnsOnlyLatest() throws Exception {
        LocalDate today = LocalDate.now();

        CurrencyDto currencyUsdDto = CurrencyDto.builder().id(1L).code("USD").name("United States Dollar")
                .build();
        CurrencyDto currencyGbpDto = CurrencyDto.builder().id(2L).code("GBP").name("British Pound")
                .build();

        List<ExchangeRateDto> expectedExchangeRateDtos = Arrays.asList(
                ExchangeRateDto.builder().currency(currencyUsdDto).rate(1.1).date(today).build(),
                ExchangeRateDto.builder().currency(currencyGbpDto).rate(0.85).date(today).build());

        when(exchangeRateService.get()).thenReturn(expectedExchangeRateDtos);

        MvcResult result = mockMvc.perform(get("/api/exchange-rates")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        String jsonResponse = result.getResponse().getContentAsString();
        List<ExchangeRateDto> actualExchangeRateDtos = Arrays
                .asList(objectMapper.readValue(jsonResponse, ExchangeRateDto[].class));

        assertEquals(expectedExchangeRateDtos, actualExchangeRateDtos);
    }

    @Test
    public void testShow_NoDataFound() throws Exception {
        List<ExchangeRateDto> expectedExchangeRateDtos = new ArrayList<>();

        when(exchangeRateService.get()).thenReturn(expectedExchangeRateDtos);

        MvcResult result = mockMvc.perform(get("/api/exchange-rates")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        String jsonResponse = result.getResponse().getContentAsString();
        List<ExchangeRateDto> actualExchangeRateDtos = Arrays
                .asList(objectMapper.readValue(jsonResponse, ExchangeRateDto[].class));

        assertEquals(expectedExchangeRateDtos, actualExchangeRateDtos);
    }
}
