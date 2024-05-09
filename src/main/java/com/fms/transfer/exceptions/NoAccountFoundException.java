package com.fms.transfer.exceptions;


import lombok.AllArgsConstructor;

import java.util.UUID;

@AllArgsConstructor
public class NoAccountFoundException extends RuntimeException {

    private final UUID accountId;

    @Override
    public String getMessage() {
        return String.format("Account with identifier %s could not be found.", accountId.toString());
    }
}
