package com.fms.transfer.service;

import com.fms.transfer.dto.TransferRequestDTO;
import com.fms.transfer.entity.Account;
import com.fms.transfer.entity.Client;
import com.fms.transfer.entity.TransactionHistory;
import com.fms.transfer.exceptions.CurrencyDoesNotMatchException;
import com.fms.transfer.exceptions.CurrencyNotSupportedException;
import com.fms.transfer.exceptions.NoAccountFoundException;
import com.fms.transfer.exceptions.NotEnoughBalanceException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransferServiceTest {

    @Mock
    private AccountService accountService;

    @Mock
    private ExchangeService exchangeService;

    @Mock
    private TransactionHistoryService historyService;

    @InjectMocks
    private TransferService transferService;

    @Test
    void when_currencyNotSupported() {

        when(exchangeService.currencySupported(anyString())).thenReturn(false);

        TransferRequestDTO request = TransferRequestDTO.builder().transferCurrency("ABC").build();

        assertThrows(CurrencyNotSupportedException.class,
                () -> transferService.transfer(request));
    }

    @Test
    void when_senderNotFound() {

        when(exchangeService.currencySupported(anyString())).thenReturn(true);
        when(accountService.findForTransfer(any(UUID.class))).thenThrow(NoAccountFoundException.class);

        TransferRequestDTO request = TransferRequestDTO.builder().senderAccountId(UUID.randomUUID()).transferCurrency("USD").build();

        assertThrows(NoAccountFoundException.class,
                () -> transferService.transfer(request));
    }

    @Test
    void when_currencyDoesNotMatch() {
        Account account = new Account();
        account.setCurrency("TRY");

        when(exchangeService.currencySupported(anyString())).thenReturn(true);
        when(accountService.findForTransfer(any(UUID.class))).thenReturn(account);

        TransferRequestDTO request = TransferRequestDTO.builder()
                .senderAccountId(UUID.randomUUID())
                .receiverAccountId(UUID.randomUUID())
                .transferCurrency("USD")
                .build();

        assertThrows(CurrencyDoesNotMatchException.class,
                () -> transferService.transfer(request));
    }

    @Test
    void when_notEnoughFunds() {
        Account account = new Account();
        account.setCurrency("USD");
        account.setBalance(99d);

        when(exchangeService.currencySupported(anyString())).thenReturn(true);
        when(accountService.findForTransfer(any(UUID.class))).thenReturn(account);

        TransferRequestDTO request = TransferRequestDTO.builder()
                .senderAccountId(UUID.randomUUID())
                .receiverAccountId(UUID.randomUUID())
                .transferCurrency("USD")
                .transferAmount(100d)
                .build();

        assertThrows(NotEnoughBalanceException.class,
                () -> transferService.transfer(request));
    }

    @Test
    void when_success() {
        Client client = new Client();

        Account account = new Account();
        account.setId(UUID.randomUUID());
        account.setCurrency("USD");
        account.setBalance(999d);
        account.setClient(client);


        when(exchangeService.currencySupported(anyString())).thenReturn(true);
        when(accountService.findForTransfer(any(UUID.class))).thenReturn(account);

        TransferRequestDTO request = TransferRequestDTO.builder()
                .senderAccountId(UUID.randomUUID())
                .receiverAccountId(UUID.randomUUID())
                .transferCurrency("USD")
                .transferAmount(100d)
                .build();

        transferService.transfer(request);

        verify(accountService, times(2)).updateBalance(any(UUID.class), anyDouble());
        verify(historyService, times(2)).save(any(TransactionHistory.class));
    }
}
