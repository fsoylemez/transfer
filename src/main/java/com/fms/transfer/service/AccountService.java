package com.fms.transfer.service;

import com.fms.transfer.dto.AccountDTO;
import com.fms.transfer.entity.Account;
import com.fms.transfer.exceptions.ClientNotFoundException;
import com.fms.transfer.exceptions.NoAccountFoundException;
import com.fms.transfer.mapper.response.AccountResponseMapper;
import com.fms.transfer.repository.AccountRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class AccountService {

    @Autowired
    private ClientService clientService;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private AccountResponseMapper responseMapper;

    public boolean existsById(UUID clientId) {
        return accountRepository.existsById(clientId);
    }

    public List<AccountDTO> findAccountsByClient(UUID clientId) {

        if (!clientService.existsById(clientId)) {
            log.debug("client with id: {} could not be found", clientId);
            throw new ClientNotFoundException(clientId);
        }

        log.debug("fetching accounts for client: {}", clientId);

        List<Account> allByClient = accountRepository.findAllByClient(clientId);

        return responseMapper.map(allByClient);
    }

    public Account findForTransfer(UUID accountId) {
        log.debug("fetching account for updating: {}", accountId);

        return accountRepository.findForTransfer(accountId).orElseThrow(() -> new NoAccountFoundException(accountId));
    }

    public void updateBalance(UUID accountId, double newBalance) {
        log.debug("updating balance for account: {}", accountId);

        accountRepository.updateBalance(accountId, newBalance);
    }
}
