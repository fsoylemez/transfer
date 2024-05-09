package com.fms.transfer.service;

import com.fms.transfer.dto.TransferRequestDTO;
import com.fms.transfer.dto.TransferResponseDTO;
import com.fms.transfer.entity.Account;
import com.fms.transfer.entity.TransactionHistory;
import com.fms.transfer.exceptions.CurrencyDoesNotMatchException;
import com.fms.transfer.exceptions.CurrencyNotSupportedException;
import com.fms.transfer.exceptions.NotEnoughBalanceException;
import com.fms.transfer.exceptions.TransferFailedException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.text.DecimalFormat;

@Slf4j
@Service
public class TransferService {

    private static final DecimalFormat df = new DecimalFormat("0.00");

    @Autowired
    private AccountService accountService;

    @Autowired
    private ExchangeService exchangeService;

    @Autowired
    private TransactionHistoryService historyService;

    @Transactional(propagation = Propagation.REQUIRES_NEW, isolation = Isolation.READ_COMMITTED)
    public TransferResponseDTO transfer(TransferRequestDTO requestDto) throws CurrencyNotSupportedException {

        if (!exchangeService.currencySupported(requestDto.getTransferCurrency())) {
            log.debug("currency {} is not supported", requestDto.getTransferCurrency());
            throw new CurrencyNotSupportedException(requestDto.getTransferCurrency());
        }

        Account senderAccount = accountService.findForTransfer(requestDto.getSenderAccountId());
        Account receiverAccount = accountService.findForTransfer(requestDto.getReceiverAccountId());

        if (!requestDto.getTransferCurrency().equalsIgnoreCase(receiverAccount.getCurrency())) {
            log.debug("currency {} does not match the receiver account currency {}", requestDto.getTransferCurrency(), receiverAccount.getCurrency());
            throw new CurrencyDoesNotMatchException(requestDto.getTransferCurrency(), receiverAccount.getCurrency());
        }

        double transferFromAmount = requestDto.getTransferAmount();
        if (!senderAccount.getCurrency().equalsIgnoreCase(requestDto.getTransferCurrency())) {
            transferFromAmount = exchangeService.convertCurrency(requestDto.getTransferCurrency(), senderAccount.getCurrency(), transferFromAmount);
        }

        if (transferFromAmount > senderAccount.getBalance()) {
            log.debug("insufficient funds for transferring from account: {}", senderAccount.getId());
            throw new NotEnoughBalanceException();
        }

        return concludeTransfer(senderAccount, receiverAccount, transferFromAmount, requestDto);
    }

    private TransferResponseDTO concludeTransfer(Account senderAccount, Account receiverAccount, double transferFromAmount, TransferRequestDTO requestDto) {

        double newSenderBalance = senderAccount.getBalance() - transferFromAmount;
        double newReceiverBalance = receiverAccount.getBalance() + requestDto.getTransferAmount();

        try {
            log.debug("updating balances for accounts sender: {}, receiver: {}", senderAccount.getId(), receiverAccount.getId());

            accountService.updateBalance(senderAccount.getId(), newSenderBalance);
            accountService.updateBalance(receiverAccount.getId(), newReceiverBalance);
        } catch (Exception e) {
            log.error(String.format("unable to complete transfer of %f %s from account %s to %s.", requestDto.getTransferAmount(), requestDto.getTransferCurrency(), senderAccount.getId().toString(), receiverAccount.getId().toString()), e);
            throw new TransferFailedException();
        } finally {
            TransactionHistory senderHistory = new TransactionHistory();
            senderHistory.setAccount(senderAccount);
            senderHistory.setClient(senderAccount.getClient());
            senderHistory.setDescription(String.format("Transfer to client %s %s", receiverAccount.getClient().getFirstName(), receiverAccount.getClient().getLastName()));
            senderHistory.setAmount(-transferFromAmount);
            senderHistory.setBalanceAfter(newSenderBalance);

            historyService.save(senderHistory);

            TransactionHistory receiverHistory = new TransactionHistory();
            receiverHistory.setAccount(receiverAccount);
            receiverHistory.setClient(receiverAccount.getClient());
            receiverHistory.setDescription(String.format("Transfer from client %s %s", senderAccount.getClient().getFirstName(), senderAccount.getClient().getLastName()));
            receiverHistory.setAmount(requestDto.getTransferAmount());
            receiverHistory.setBalanceAfter(newReceiverBalance);

            historyService.save(receiverHistory);
        }

        return TransferResponseDTO.builder()
                .success(true)
                .message(String.format("%s %s successfully transferred to %s %s",
                        df.format(requestDto.getTransferAmount()),
                        requestDto.getTransferCurrency(),
                        receiverAccount.getClient().getFirstName(),
                        receiverAccount.getClient().getLastName()))
                .build();
    }

}
