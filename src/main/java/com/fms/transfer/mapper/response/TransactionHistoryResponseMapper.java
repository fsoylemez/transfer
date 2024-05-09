package com.fms.transfer.mapper.response;

import com.fms.transfer.dto.TransactionHistoryDTO;
import com.fms.transfer.entity.TransactionHistory;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;

@Component
public class TransactionHistoryResponseMapper {

    public TransactionHistoryDTO map(TransactionHistory transactionHistory) {
        if (transactionHistory == null) {
            return null;
        }

        return TransactionHistoryDTO.builder().amount(transactionHistory.getAmount())
                .balanceAfter(transactionHistory.getBalanceAfter())
                .description(transactionHistory.getDescription())
                .transactionTime(transactionHistory.getTransactionTime())
                .build();
    }

    public List<TransactionHistoryDTO> map(List<TransactionHistory> histories) {
        if (CollectionUtils.isEmpty(histories)) {
            return Collections.emptyList();
        }

        return histories.stream().map(this::map).toList();
    }
}
