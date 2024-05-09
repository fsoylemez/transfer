package com.fms.transfer.service;

import com.fms.transfer.dto.TransactionHistoryDTO;
import com.fms.transfer.entity.TransactionHistory;
import com.fms.transfer.exceptions.AccountNotFoundException;
import com.fms.transfer.mapper.response.TransactionHistoryResponseMapper;
import com.fms.transfer.repository.TransactionHistoryRepository;
import com.fms.transfer.utils.OffsetBasedPageRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
public class TransactionHistoryService {

    @Autowired
    private AccountService accountService;

    @Autowired
    private TransactionHistoryRepository historyRepository;

    @Autowired
    private TransactionHistoryResponseMapper responseMapper;

    public Page<TransactionHistoryDTO> getHistoryByAccount(UUID accountId, int offset, int limit) {

        if (!accountService.existsById(accountId)) {
            log.debug("account with id: {} could not be found", accountId);
            throw new AccountNotFoundException(accountId);
        }

        Sort.Order order = new Sort.Order(Sort.Direction.DESC, "transactionTime");

        Pageable pageable = new OffsetBasedPageRequest(offset, limit, Sort.by(order));

        Page<TransactionHistory> historyByAccount = historyRepository.findAllByAccount(accountId, pageable);

        return historyByAccount.map(a -> responseMapper.map(a));
    }

    public void save(TransactionHistory transactionHistory) {
        historyRepository.save(transactionHistory);
    }
}
