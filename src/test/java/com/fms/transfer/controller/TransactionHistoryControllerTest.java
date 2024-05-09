package com.fms.transfer.controller;

import com.fms.transfer.dto.TransactionHistoryDTO;
import com.fms.transfer.exceptions.AccountNotFoundException;
import com.fms.transfer.service.TransactionHistoryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TransactionHistoryController.class)
class TransactionHistoryControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private TransactionHistoryService historyService;

    @Test
    void when_invalidAccountId() throws Exception {
        String invalidAccountId = "1";
        mvc.perform(get("/history/{accountId}", invalidAccountId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void when_accountNotFound() throws Exception {
        UUID randomUuId = UUID.randomUUID();

        when(historyService.getHistoryByAccount(randomUuId, 0, 20)).thenThrow(new AccountNotFoundException(randomUuId));

        mvc.perform(get("/history/{accountId}", randomUuId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void when_noDataFound() throws Exception {

        when(historyService.getHistoryByAccount(any(UUID.class), anyInt(), anyInt())).thenReturn(Page.empty());

        mvc.perform(get("/history/{accountId}", UUID.randomUUID())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    void when_success() throws Exception {

        List<TransactionHistoryDTO> transactions = List.of(TransactionHistoryDTO.builder().description("new transfer").build());
        Page<TransactionHistoryDTO> pagedResponse = new PageImpl(transactions);
        when(historyService.getHistoryByAccount(any(UUID.class), anyInt(), anyInt())).thenReturn(pagedResponse);

        mvc.perform(get("/history/{accountId}", UUID.randomUUID())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

}
