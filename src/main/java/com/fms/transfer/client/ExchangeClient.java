package com.fms.transfer.client;

import com.fms.transfer.client.model.RatesModel;
import com.fms.transfer.exceptions.ExchangeApiNotAvailableException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import java.time.Duration;
import java.time.LocalDateTime;

@Slf4j
@Component
public class ExchangeClient {

    @Value("${freecurrencyapi.url}")
    private String exchangeRatesUrl;

    @Value("${freecurrencyapi.api-key}")
    private String exchangeRatesApiKey;

    private RatesModel lastRatesModel;

    private LocalDateTime lastFetched;

    @Retryable(retryFor = {HttpClientErrorException.class},
            maxAttempts = 2, backoff = @Backoff(value = 2000))
    public RatesModel getRatesByUsd() {

        log.debug("fetching rates from exchange api.");

        RestClient restClient = RestClient.builder()
                .baseUrl(exchangeRatesUrl)
                .build();

        ResponseEntity<RatesModel> response = restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .queryParam("apikey", exchangeRatesApiKey)
                        .build())
                .header("Content-Type", "application/json")
                .retrieve()
                .toEntity(RatesModel.class);

        lastRatesModel = response.getBody();
        lastFetched = LocalDateTime.now();

        return lastRatesModel;
    }

    @Recover
    public RatesModel recover(HttpClientErrorException ex) {
        log.warn("Can not reach exchange api, recovering.");

        if (lastFetched != null) {
            Duration duration = Duration.between(lastFetched, LocalDateTime.now());
            if ((duration.getSeconds() / 60) < 5) {
                log.warn("Using exchange rates from {}", lastFetched.toString());
                return lastRatesModel;
            }
        }

        log.error("Could not recover exchange data! Message: {}", ex.getLocalizedMessage());
        throw new ExchangeApiNotAvailableException();
    }
}
