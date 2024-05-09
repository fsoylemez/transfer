package com.fms.transfer.service;

import com.fms.transfer.entity.TransactionHistory;
import com.fms.transfer.exceptions.AccountNotFoundException;
import com.fms.transfer.mapper.response.TransactionHistoryResponseMapper;
import com.fms.transfer.repository.TransactionHistoryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionHistoryServiceTest {

    @Mock
    private TransactionHistoryRepository historyRepository;

    @Mock
    private TransactionHistoryResponseMapper responseMapper;

    @Mock
    private AccountService accountService;

    @InjectMocks
    private TransactionHistoryService transactionHistoryService;

    @Test
    void when_getHistoryByAccount_AccountNotFound() {
        UUID notExistingAccountId = UUID.randomUUID();

        when(accountService.existsById(notExistingAccountId)).thenReturn(false);

        assertThrows(AccountNotFoundException.class,
                () -> transactionHistoryService.getHistoryByAccount(notExistingAccountId, 0, 20));
    }

    @Test
    void when_getHistoryByAccount_success() {
        List<TransactionHistory> transactions = List.of(new TransactionHistory());
        Page<TransactionHistory> pagedResponse = new PageImpl(transactions);

        when(accountService.existsById(any(UUID.class))).thenReturn(true);
        when(historyRepository.findAllByAccount(any(UUID.class), any(Pageable.class))).thenReturn(pagedResponse);

        transactionHistoryService.getHistoryByAccount(UUID.randomUUID(), 0, 20);

        verify(responseMapper, times(transactions.size())).map(transactions.get(0));
    }

    @Test
    void when_save() {
        TransactionHistory history = Mockito.mock(TransactionHistory.class);

        transactionHistoryService.save(history);

        verify(historyRepository, times(1)).save(history);
    }
}
