package com.fms.transfer.it;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fms.transfer.dto.TransferRequestDTO;
import com.fms.transfer.dto.TransferResponseDTO;
import com.fms.transfer.entity.Account;
import com.fms.transfer.repository.AccountRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.text.DecimalFormat;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-integrationtest.yml")
class TransferControllerIntegrationTest {

    private static final DecimalFormat df = new DecimalFormat("0.00");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AccountRepository accountRepository;

    @Test
    void when_CurrencyNotSupported() throws Exception {

        TransferRequestDTO payload = TransferRequestDTO.builder()
                .senderAccountId(UUID.randomUUID())
                .receiverAccountId(UUID.randomUUID())
                .transferCurrency("ABC")
                .transferAmount(10d)
                .build();


        MvcResult result = mockMvc.perform(post("/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(payload)))
                .andExpect(status().isBadRequest())
                .andReturn();

        assertEquals(String.format("Currency %s is not supported.", payload.getTransferCurrency()), result.getResponse().getContentAsString());
    }

    @Test
    void when_senderNotFound() throws Exception {

        TransferRequestDTO payload = TransferRequestDTO.builder()
                .senderAccountId(UUID.randomUUID())
                .receiverAccountId(UUID.randomUUID())
                .transferCurrency("USD")
                .transferAmount(10d)
                .build();


        MvcResult result = mockMvc.perform(post("/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(payload)))
                .andExpect(status().isBadRequest())
                .andReturn();

        assertEquals(String.format("Account with identifier %s could not be found.", payload.getSenderAccountId()), result.getResponse().getContentAsString());
    }

    @Sql({"classpath:sqlScripts/only_sender_account.sql"})
    @Test
    void when_receiverNotFound() throws Exception {

        TransferRequestDTO payload = TransferRequestDTO.builder()
                .senderAccountId(UUID.fromString("8806545c-280c-41bc-9de0-df4f30672ff0"))
                .receiverAccountId(UUID.randomUUID())
                .transferCurrency("USD")
                .transferAmount(10d)
                .build();


        MvcResult result = mockMvc.perform(post("/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(payload)))
                .andExpect(status().isBadRequest())
                .andReturn();

        assertEquals(String.format("Account with identifier %s could not be found.", payload.getReceiverAccountId()), result.getResponse().getContentAsString());
    }

    @Sql({"classpath:sqlScripts/currency_no_match_receiver.sql"})
    @Test
    void when_currencyDoesNotMatch() throws Exception {

        TransferRequestDTO payload = TransferRequestDTO.builder()
                .senderAccountId(UUID.fromString("62339e03-af01-44c6-96a1-470fa6d9e6c1"))
                .receiverAccountId(UUID.fromString("fbc9e987-fd5f-4f9f-9bf9-65a93b84e33a"))
                .transferCurrency("USD")
                .transferAmount(10d)
                .build();


        MvcResult result = mockMvc.perform(post("/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(payload)))
                .andExpect(status().isBadRequest())
                .andReturn();

        assertEquals(String.format("Transfer currency %s does not match receiver account currency %s.", payload.getTransferCurrency(), "GBP"), result.getResponse().getContentAsString());
    }

    @Sql({"classpath:sqlScripts/not_enough_balance.sql"})
    @Test
    void when_notEnoughBalance() throws Exception {

        TransferRequestDTO payload = TransferRequestDTO.builder()
                .senderAccountId(UUID.fromString("0e4f235d-3a49-4e81-a497-ae12ea930a9c"))
                .receiverAccountId(UUID.fromString("6f70f9c7-c94d-4fa0-8f3f-e635ee6d2bfe"))
                .transferCurrency("EUR")
                .transferAmount(5000.43d)
                .build();


        MvcResult result = mockMvc.perform(post("/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(payload)))
                .andExpect(status().isBadRequest())
                .andReturn();

        assertEquals("Not enough funds in the account for transferring.", result.getResponse().getContentAsString());
    }

    @Sql({"classpath:sqlScripts/transfer_success.sql"})
    @Test
    void when_success() throws Exception {

        Optional<Account> sender = accountRepository.findById(UUID.fromString("d3c17634-79f8-4a18-add3-aeee470b8505"));
        assertTrue(sender.isPresent());
        Optional<Account> receiver = accountRepository.findById(UUID.fromString("4c47d15c-eb10-44c9-9fe5-4f4b9290f837"));
        assertTrue(receiver.isPresent());

        TransferRequestDTO payload = TransferRequestDTO.builder()
                .senderAccountId(sender.get().getId())
                .receiverAccountId(receiver.get().getId())
                .transferCurrency("EUR")
                .transferAmount(500.43d)
                .build();


        MvcResult result = mockMvc.perform(post("/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(payload)))
                .andExpect(status().isOk())
                .andReturn();

        var responseDto = objectMapper.readValue(result.getResponse().getContentAsString(), TransferResponseDTO.class);

        assertEquals(String.format("%s %s successfully transferred to %s %s",
                df.format(payload.getTransferAmount()),
                payload.getTransferCurrency(),
                "Michael",
                "Bane"), responseDto.getMessage());

        Optional<Account> senderAfter = accountRepository.findById(sender.get().getId());
        assertTrue(senderAfter.isPresent());
        Optional<Account> receiverAfter = accountRepository.findById(receiver.get().getId());
        assertTrue(receiverAfter.isPresent());

        assertEquals(sender.get().getBalance() - payload.getTransferAmount(), senderAfter.get().getBalance(), 0.000001);
        assertEquals(receiver.get().getBalance() + payload.getTransferAmount(), receiverAfter.get().getBalance(), 0.000001);
    }

    @Sql({"classpath:sqlScripts/transfer_success_converted.sql"})
    @Test
    void when_successConverted() throws Exception {

        Optional<Account> sender = accountRepository.findById(UUID.fromString("7b6095d9-7d1d-47ff-b517-1be89ecac650"));
        assertTrue(sender.isPresent());
        Optional<Account> receiver = accountRepository.findById(UUID.fromString("b9de56ad-37a2-41de-aba4-da2d34ab094f"));
        assertTrue(receiver.isPresent());

        TransferRequestDTO payload = TransferRequestDTO.builder()
                .senderAccountId(sender.get().getId())
                .receiverAccountId(receiver.get().getId())
                .transferCurrency("USD")
                .transferAmount(500.43d)
                .build();


        MvcResult result = mockMvc.perform(post("/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(payload)))
                .andExpect(status().isOk())
                .andReturn();

        var responseDto = objectMapper.readValue(result.getResponse().getContentAsString(), TransferResponseDTO.class);

        assertEquals(String.format("%s %s successfully transferred to %s %s",
                df.format(payload.getTransferAmount()),
                payload.getTransferCurrency(),
                "Michael",
                "Bane"), responseDto.getMessage());

        Optional<Account> senderAfter = accountRepository.findById(sender.get().getId());
        assertTrue(senderAfter.isPresent());
        Optional<Account> receiverAfter = accountRepository.findById(receiver.get().getId());
        assertTrue(receiverAfter.isPresent());

        assertNotEquals(sender.get().getBalance() - senderAfter.get().getBalance(), payload.getTransferAmount());
        assertEquals(receiver.get().getBalance() + payload.getTransferAmount(), receiverAfter.get().getBalance(), 0.000001);
    }
}
