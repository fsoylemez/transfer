package com.fms.transfer.mapper.response;

import com.fms.transfer.dto.AccountDTO;
import com.fms.transfer.entity.Account;
import com.fms.transfer.entity.Client;
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
class AccountResponseMapperTest {

    @Spy
    private AccountResponseMapper responseMapper;

    @Test
    void when_toDto_Null() {
        AccountDTO dto = responseMapper.map((Account) null);

        assertNull(dto);
    }

    @Test
    void when_toDtoList_Empty() {
        List<AccountDTO> dtos = responseMapper.map(Collections.emptyList());

        assertEquals(0, dtos.size());
    }

    @Test
    void when_toDtoList() {
        List<Account> accountList = getAccountList();

        List<AccountDTO> dtos = responseMapper.map(accountList);

        dtos.forEach(dto ->
                assertAll(
                        () -> assertNotNull(dto.getAccountId()),
                        () -> assertNotNull(dto.getAccountName()),
                        () -> assertNotNull(dto.getBalance()),
                        () -> assertNotNull(dto.getCurrency()),
                        () -> assertNotNull(dto.getClientId())
                ));
    }

    private List<Account> getAccountList() {
        Client client = new Client();
        client.setId(UUID.randomUUID());

        List<Account> accountList = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            Account account = new Account();
            account.setId(UUID.randomUUID());
            account.setClient(client);
            account.setAccountName("someName" + i);
            account.setBalance(100 * Math.random());
            account.setCurrency("USD");
            account.setCreationTime(LocalDateTime.now());

            accountList.add(account);
        }

        return accountList;
    }
}
