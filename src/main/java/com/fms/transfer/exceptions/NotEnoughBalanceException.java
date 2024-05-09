package com.fms.transfer.exceptions;


import lombok.AllArgsConstructor;

@AllArgsConstructor
public class NotEnoughBalanceException extends RuntimeException {

    @Override
    public String getMessage() {
        return "Not enough funds in the account for transferring.";
    }
}
