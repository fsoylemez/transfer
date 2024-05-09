package com.fms.transfer.exceptions;


import lombok.AllArgsConstructor;

@AllArgsConstructor
public class NoDataFoundException extends RuntimeException {

    @Override
    public String getMessage() {
        return "No data found.";
    }
}
