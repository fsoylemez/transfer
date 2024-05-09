package com.fms.transfer.exceptions;


import lombok.AllArgsConstructor;

import java.util.UUID;

@AllArgsConstructor
public class ClientNotFoundException extends RuntimeException {

    private final UUID clientId;

    @Override
    public String getMessage() {
        return String.format("Client with identifier %s could not be found.", clientId.toString());
    }
}
