package com.fms.transfer.client.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Map;

@Data
public class RatesModel implements Serializable {

    @JsonProperty("data")
    private Map<String, Double> rates;
}
