package com.fms.transfer.dto;

import lombok.Builder;
import lombok.Getter;

import java.io.Serializable;
import java.util.UUID;

@Getter
@Builder
public class AccountDTO implements Serializable {

    private UUID accountId;

    private UUID clientId;

    private String accountName;

    private String currency;

    private Double balance;
}
