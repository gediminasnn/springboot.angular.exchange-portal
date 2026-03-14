package com.example.exchangeportal.controller;

import com.example.exchangeportal.dto.CurrencyDto;
import com.example.exchangeportal.dto.ExchangeRateDto;
import com.example.exchangeportal.service.CurrencyService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CurrencyController.class)
public class CurrencyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CurrencyService currencyService;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ModelMapper mockModelMapper;

    @Test
    public void testIndex_Success() throws Exception {
        List<ExchangeRateDto> exchangeRatesDto = Arrays.asList(
                ExchangeRateDto.builder().rate(1.23).date(LocalDate.of(2024, 5, 1)).build(),
                ExchangeRateDto.builder().rate(1.24).date(LocalDate.of(2024, 5, 2)).build(),
                ExchangeRateDto.builder().rate(1.25).date(LocalDate.of(2024, 5, 3)).build(),
                ExchangeRateDto.builder().rate(1.14).date(LocalDate.of(2024, 5, 4)).build(),
                ExchangeRateDto.builder().rate(1.23).date(LocalDate.of(2024, 5, 5)).build());

        CurrencyDto expectedCurrencyDto = CurrencyDto.builder()
                .id(1L)
                .code("USD")
                .name("US Dollar")
                .build();

        expectedCurrencyDto.setExchangeRates(exchangeRatesDto);

        when(currencyService.get(
                1L,
                LocalDate.of(2024, 5, 1),
                LocalDate.of(2024, 5, 5)))
                .thenReturn(expectedCurrencyDto);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/api/currencies/1/exchange-rates")
                .param("fromDate", "2024-05-01")
                .param("toDate", "2024-05-05")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        CurrencyDto actualCurrencyDto = objectMapper.readValue(result.getResponse().getContentAsString(),
                CurrencyDto.class);

        assertEquals(expectedCurrencyDto, actualCurrencyDto);
    }
}
