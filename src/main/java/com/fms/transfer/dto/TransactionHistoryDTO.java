package com.fms.transfer.dto;

import lombok.Builder;
import lombok.Getter;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Builder
public class TransactionHistoryDTO implements Serializable {

    private Double amount;

    private Double balanceAfter;

    private String description;

    private LocalDateTime transactionTime;
}
