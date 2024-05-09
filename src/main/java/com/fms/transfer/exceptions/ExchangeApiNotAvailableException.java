package com.fms.transfer.exceptions;

public class ExchangeApiNotAvailableException extends RuntimeException {

    @Override
    public String getMessage() {
        return "Exchange API is not available";
    }
}
