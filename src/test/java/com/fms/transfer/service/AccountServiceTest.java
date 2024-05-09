package com.fms.transfer.service;

import com.fms.transfer.entity.Account;
import com.fms.transfer.exceptions.ClientNotFoundException;
import com.fms.transfer.exceptions.NoAccountFoundException;
import com.fms.transfer.mapper.response.AccountResponseMapper;
import com.fms.transfer.repository.AccountRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private ClientService clientService;

    @Mock
    private AccountResponseMapper responseMapper;

    @InjectMocks
    private AccountService accountService;

    @Test
    void when_existsById() {
        accountService.existsById(UUID.randomUUID());

        verify(accountRepository, times(1)).existsById(any(UUID.class));
    }

    @Test
    void when_findAccountsByClient_AccountNotFound() {
        UUID notExistingClientId = UUID.randomUUID();

        when(clientService.existsById(any(UUID.class))).thenReturn(false);

        assertThrows(ClientNotFoundException.class,
                () -> accountService.findAccountsByClient(notExistingClientId));

        verify(clientService, times(1)).existsById(any(UUID.class));
    }

    @Test
    void when_findAccountsByClient() {
        when(clientService.existsById(any(UUID.class))).thenReturn(true);

        accountService.findAccountsByClient(UUID.randomUUID());

        verify(clientService, times(1)).existsById(any(UUID.class));
        verify(accountRepository, times(1)).findAllByClient(any(UUID.class));
        verify(responseMapper, times(1)).map(anyList());
    }

    @Test
    void when_findForTransfer_NoAccountFound() {
        UUID randomUuid = UUID.randomUUID();

        when(accountRepository.findForTransfer(randomUuid)).thenThrow(new NoAccountFoundException(randomUuid));

        assertThrows(NoAccountFoundException.class,
                () -> accountService.findForTransfer(randomUuid));
    }

    @Test
    void when_findForTransfer() {
        UUID accountId = UUID.randomUUID();
        when(accountRepository.findForTransfer(accountId)).thenReturn(Optional.of(new Account()));

        accountService.findForTransfer(accountId);

        verify(accountRepository, times(1)).findForTransfer(any(UUID.class));
    }

    @Test
    void when_updateBalance() {
        accountService.updateBalance(UUID.randomUUID(), 20);

        verify(accountRepository, times(1)).updateBalance(any(UUID.class), anyDouble());
    }
}
