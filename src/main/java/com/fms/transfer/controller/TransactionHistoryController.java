package com.fms.transfer.controller;

import com.fms.transfer.dto.TransactionHistoryDTO;
import com.fms.transfer.exceptions.NoDataFoundException;
import com.fms.transfer.service.TransactionHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping(value = "/history")
public class TransactionHistoryController {

    @Autowired
    private TransactionHistoryService historyService;

    @GetMapping(path = "/{accountId}")
    public Page<TransactionHistoryDTO> getHistoryByAccount(@PathVariable("accountId") UUID accountId,
                                                           @RequestParam(name = "offset", defaultValue = "0") int offset,
                                                           @RequestParam(name = "limit", defaultValue = "20") int limit) {

        Page<TransactionHistoryDTO> historyByAccount = historyService.getHistoryByAccount(accountId, offset, limit);
        if (historyByAccount.isEmpty()) {
            throw new NoDataFoundException();
        }

        return historyByAccount;
    }
}
