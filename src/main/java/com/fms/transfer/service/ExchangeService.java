package com.fms.transfer.service;

import com.fms.transfer.client.ExchangeClient;
import com.fms.transfer.client.model.RatesModel;
import com.fms.transfer.configuration.CachingConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
public class ExchangeService {

    private static final String BASE_CURRENCY = "USD";

    @Autowired
    private ExchangeClient exchangeClient;

    public Double convertCurrency(String fromCurrency, String toCurrency, double amount) {
        if (BASE_CURRENCY.equalsIgnoreCase(fromCurrency)) {
            return getRates().get(toCurrency) * amount;
        } else {
            return (1 / getRates().get(fromCurrency)) * amount;
        }
    }

    public boolean currencySupported(String currency) {
        return getRates().containsKey(currency);
    }

    @Cacheable(CachingConfig.EXCHANGE_RATES)
    public Map<String, Double> getRates() {
        log.debug("fetching exchange rates");

        RatesModel ratesModel = exchangeClient.getRatesByUsd();

        return ratesModel.getRates();
    }
}
