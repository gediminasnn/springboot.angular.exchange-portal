package com.example.exchangeportal.repository;

import com.example.exchangeportal.entity.Currency;
import com.example.exchangeportal.entity.ExchangeRate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.lang.NonNull;

@Repository
public interface ExchangeRateRepository extends JpaRepository<ExchangeRate, Long> {
    /**
     * Finds all exchange rates where the date
     * is the most recent (latest) date in the database.
     *
     * @return A list of exchange rates matching the criteria.
     */
    @Query("SELECT er FROM ExchangeRate er WHERE er.date = (SELECT MAX(er2.date) FROM ExchangeRate er2)")
    @NonNull
    List<ExchangeRate> findAll();

    /**
     * Finds all exchange rates with the given currency and within the
     * specified date range.
     *
     * @param currency The currency entity to find by.
     * @param fromDate The start date of the date range.
     * @param toDate   The end date of the date range.
     * @return A list of exchange rates with the given currency and within
     *         the specified date range.
     */
    @Query("SELECT er FROM ExchangeRate er WHERE er.currency = :currency AND er.date BETWEEN :fromDate AND :toDate")
    @NonNull
    List<ExchangeRate> findAll(
            @Param("currency") Currency currency,
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate);

}
