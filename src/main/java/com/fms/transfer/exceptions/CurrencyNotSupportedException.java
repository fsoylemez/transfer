package com.fms.transfer.exceptions;


import lombok.AllArgsConstructor;

@AllArgsConstructor
public class CurrencyNotSupportedException extends RuntimeException {

    private final String currency;

    @Override
    public String getMessage() {
        return String.format("Currency %s is not supported.", currency);
    }
}
