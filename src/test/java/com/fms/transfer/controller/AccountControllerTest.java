package com.fms.transfer.controller;

import com.fms.transfer.dto.AccountDTO;
import com.fms.transfer.exceptions.ClientNotFoundException;
import com.fms.transfer.service.AccountService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AccountController.class)
class AccountControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private AccountService accountService;


    @Test
    void when_invalidClientId() throws Exception {
        String invalidClientId = "1";
        mvc.perform(get("/accounts/byClient/{invalidClientId}", invalidClientId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void when_clientNotFound() throws Exception {
        UUID randomUuId = UUID.randomUUID();

        when(accountService.findAccountsByClient(randomUuId)).thenThrow(new ClientNotFoundException(randomUuId));

        mvc.perform(get("/accounts/byClient/{clientId}", randomUuId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void when_noAccountFound() throws Exception {

        when(accountService.findAccountsByClient(any(UUID.class))).thenReturn(Collections.emptyList());

        mvc.perform(get("/accounts/byClient/{clientId}", UUID.randomUUID())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    void when_success() throws Exception {
        UUID validClientId = UUID.randomUUID();

        when(accountService.findAccountsByClient(validClientId)).thenReturn(Arrays.asList(AccountDTO.builder().accountName("someAccount").build()));

        mvc.perform(get("/accounts/byClient/{clientId}", validClientId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

}
