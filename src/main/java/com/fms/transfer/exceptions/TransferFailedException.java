package com.fms.transfer.exceptions;


import lombok.AllArgsConstructor;

@AllArgsConstructor
public class TransferFailedException extends RuntimeException {

    @Override
    public String getMessage() {
        return "an error occurred while transferring funds.";
    }
}
