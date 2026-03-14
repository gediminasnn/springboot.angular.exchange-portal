package com.example.exchangeportal.controller;

import org.springframework.web.bind.annotation.*;
import com.example.exchangeportal.dto.ExchangeRateDto;
import com.example.exchangeportal.service.ExchangeRateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import java.util.List;

@RestController
@RequestMapping("/api/exchange-rates")
@CrossOrigin(origins = "*")
public class ExchangeRateController {

    @Autowired
    private ExchangeRateService exchangeRateService;

    @GetMapping
    public ResponseEntity<List<ExchangeRateDto>> show() {
        List<ExchangeRateDto> exchangeRateDtos = exchangeRateService.get();
        return new ResponseEntity<>(exchangeRateDtos, HttpStatus.OK);
    }
}
