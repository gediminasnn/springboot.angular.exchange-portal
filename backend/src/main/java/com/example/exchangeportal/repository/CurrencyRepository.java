package com.example.exchangeportal.repository;

import com.example.exchangeportal.entity.Currency;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

@Repository
public interface CurrencyRepository extends JpaRepository<Currency, Long> {
    /**
     * Finds a list of Currency entities based on their currency codes.
     *
     * @param currencyCodes A list of currency codes.
     * @return A list of matching Currency entities, or an empty list if no matches
     *         are found.
     */
    @Query("SELECT c FROM Currency c WHERE c.code IN :currencyCodes")
    @NonNull
    List<Currency> findAll(List<String> currencyCodes);
}
