package com.fms.transfer.mapper.response;

import com.fms.transfer.dto.AccountDTO;
import com.fms.transfer.entity.Account;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;

@Component
public class AccountResponseMapper {

    public AccountDTO map(Account account) {
        if (account == null) {
            return null;
        }

        return AccountDTO.builder().accountId(account.getId())
                .accountName(account.getAccountName())
                .clientId(account.getClient().getId())
                .currency(account.getCurrency())
                .balance(account.getBalance())
                .build();
    }

    public List<AccountDTO> map(List<Account> accounts) {
        if (CollectionUtils.isEmpty(accounts)) {
            return Collections.emptyList();
        }

        return accounts.stream().map(this::map).toList();
    }
}
