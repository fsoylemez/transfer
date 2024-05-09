package com.fms.transfer.mapper.response;

import com.fms.transfer.dto.TransactionHistoryDTO;
import com.fms.transfer.entity.Account;
import com.fms.transfer.entity.Client;
import com.fms.transfer.entity.TransactionHistory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class TransactionHistoryResponseMapperTest {

    @Spy
    private TransactionHistoryResponseMapper responseMapper;

    @Test
    void when_toDto_Null() {
        TransactionHistoryDTO dto = responseMapper.map((TransactionHistory) null);

        assertNull(dto);
    }

    @Test
    void when_toDtoList_Empty() {
        List<TransactionHistoryDTO> dtos = responseMapper.map(Collections.emptyList());

        assertEquals(0, dtos.size());
    }

    @Test
    void when_toDtoList() {
        List<TransactionHistory> transactionHistoryList = getTransactionHistoryList();

        List<TransactionHistoryDTO> dtos = responseMapper.map(transactionHistoryList);

        dtos.forEach(dto ->
                assertAll(
                        () -> assertNotNull(dto.getDescription()),
                        () -> assertNotNull(dto.getTransactionTime()),
                        () -> assertNotNull(dto.getAmount()),
                        () -> assertNotNull(dto.getBalanceAfter())
                ));
    }

    private List<TransactionHistory> getTransactionHistoryList() {
        List<TransactionHistory> transactionHistoryList = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            TransactionHistory history = new TransactionHistory();
            history.setId(UUID.randomUUID());
            history.setClient(new Client());
            history.setAccount(new Account());
            history.setAmount(100 * Math.random());
            history.setBalanceAfter(1000 * Math.random());
            history.setDescription("transfer");
            history.setTransactionTime(LocalDateTime.now());

            transactionHistoryList.add(history);
        }

        return transactionHistoryList;
    }
}
