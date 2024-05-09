package com.fms.transfer.controller;

import com.fms.transfer.dto.AccountDTO;
import com.fms.transfer.exceptions.NoDataFoundException;
import com.fms.transfer.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(value = "/accounts")
public class AccountController {

    @Autowired
    private AccountService accountService;

    @GetMapping(value = "/byClient/{clientId}")
    public List<AccountDTO> getAccountsByClient(@PathVariable("clientId") UUID clientId) {

        List<AccountDTO> accountsByClient = accountService.findAccountsByClient(clientId);
        if (accountsByClient.isEmpty()) {
            throw new NoDataFoundException();
        }

        return accountsByClient;
    }
}
