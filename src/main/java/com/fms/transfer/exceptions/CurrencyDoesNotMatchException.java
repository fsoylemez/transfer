package com.fms.transfer.exceptions;


import lombok.AllArgsConstructor;

@AllArgsConstructor
public class CurrencyDoesNotMatchException extends RuntimeException {

    private final String transferCurrency;

    private final String receiverCurrency;

    @Override
    public String getMessage() {
        return String.format("Transfer currency %s does not match receiver account currency %s.", transferCurrency, receiverCurrency);
    }
}
