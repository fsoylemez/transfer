package com.fms.transfer.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fms.transfer.dto.TransferRequestDTO;
import com.fms.transfer.dto.TransferResponseDTO;
import com.fms.transfer.exceptions.CurrencyDoesNotMatchException;
import com.fms.transfer.exceptions.CurrencyNotSupportedException;
import com.fms.transfer.exceptions.NoAccountFoundException;
import com.fms.transfer.exceptions.NotEnoughBalanceException;
import com.fms.transfer.service.TransferService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TransferController.class)
class TransferControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private TransferService transferService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void when_zeroAmount() throws Exception {
        double zeroAmount = 0;
        TransferRequestDTO payload = TransferRequestDTO.builder().transferAmount(zeroAmount).build();

        mvc.perform(post("/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(payload)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void when_negativeAmount() throws Exception {
        double negativeAmount = -25;
        TransferRequestDTO payload = TransferRequestDTO.builder().transferAmount(negativeAmount).build();

        mvc.perform(post("/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(payload)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void when_invalidCurrency() throws Exception {
        String invalidCurrency = "ABCDEF";
        TransferRequestDTO payload = TransferRequestDTO.builder().transferCurrency(invalidCurrency).build();

        mvc.perform(post("/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(payload)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void when_currencyNotSupported() throws Exception {
        TransferRequestDTO payload = getTransferRequestDTO();
        payload.setTransferCurrency("ABC");

        when(transferService.transfer(any(TransferRequestDTO.class))).thenThrow(CurrencyNotSupportedException.class);

        mvc.perform(post("/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(payload)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void when_senderNotFound() throws Exception {
        TransferRequestDTO payload = getTransferRequestDTO();

        when(transferService.transfer(any(TransferRequestDTO.class))).thenThrow(new NoAccountFoundException(payload.getSenderAccountId()));

        mvc.perform(post("/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(payload)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void when_receiverNotFound() throws Exception {
        TransferRequestDTO payload = getTransferRequestDTO();

        when(transferService.transfer(any(TransferRequestDTO.class))).thenThrow(new NoAccountFoundException(payload.getReceiverAccountId()));

        mvc.perform(post("/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(payload)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void when_currencyDoesNotMatch() throws Exception {
        TransferRequestDTO payload = getTransferRequestDTO();

        when(transferService.transfer(any(TransferRequestDTO.class))).thenThrow(CurrencyDoesNotMatchException.class);

        mvc.perform(post("/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(payload)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void when_notEnoughBalance() throws Exception {
        TransferRequestDTO payload = getTransferRequestDTO();

        when(transferService.transfer(any(TransferRequestDTO.class))).thenThrow(NotEnoughBalanceException.class);

        mvc.perform(post("/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(payload)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void when_success() throws Exception {
        TransferRequestDTO payload = getTransferRequestDTO();

        when(transferService.transfer(payload)).thenReturn(TransferResponseDTO.builder().success(true).message("successfully transferred.").build());

        mvc.perform(post("/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(payload)))
                .andExpect(status().isOk());
    }

    public TransferRequestDTO getTransferRequestDTO() {
        return TransferRequestDTO.builder()
                .senderAccountId(UUID.randomUUID())
                .receiverAccountId(UUID.randomUUID())
                .transferCurrency("USD")
                .transferAmount(10d)
                .build();
    }
}
