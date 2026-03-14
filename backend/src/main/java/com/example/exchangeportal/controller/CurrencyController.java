package com.example.exchangeportal.controller;

import org.springframework.web.bind.annotation.*;
import com.example.exchangeportal.dto.CurrencyDto;
import com.example.exchangeportal.service.CurrencyService;
import com.example.exchangeportal.validation.ConsistentDateBetweenParameters;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import java.time.LocalDate;

@Validated
@RestController
@RequestMapping("/api/currencies")
@CrossOrigin(origins = "*")
public class CurrencyController {

    @Autowired
    private CurrencyService currencyService;

    @GetMapping("/{id}/exchange-rates")
    @ConsistentDateBetweenParameters(fromDatePosition = 2, toDatePosition = 3)
    public ResponseEntity<CurrencyDto> index(
            @PathVariable("id") Long id,
            @RequestParam(required = false) LocalDate fromDate,
            @RequestParam(required = false) LocalDate toDate) throws Exception {
        CurrencyDto currencyDto = currencyService.get(id, fromDate, toDate);
        return new ResponseEntity<>(currencyDto, HttpStatus.OK);
    }
}
