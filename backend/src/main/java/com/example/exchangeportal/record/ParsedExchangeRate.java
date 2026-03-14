package com.example.exchangeportal.record;

import java.time.LocalDate;

public record ParsedExchangeRate(String currencyCode, double rate, LocalDate date) {
}
