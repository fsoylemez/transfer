package com.fms.transfer.it;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-integrationtest.yml")
class TransactionHistoryControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testHistoryByAccount_accountNotFound() throws Exception {

        UUID notExistingAccountId = UUID.randomUUID();

        MvcResult result = mockMvc.perform(get("/history/{accountId}", notExistingAccountId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        assertEquals(HttpStatus.NOT_FOUND.value(), result.getResponse().getStatus());

        assertEquals(String.format("Account with identifier %s could not be found.", notExistingAccountId), result.getResponse().getContentAsString());
    }

    @Sql({"classpath:sqlScripts/account_with_no_transactions.sql"})
    @Test
    void testHistoryByAccount_noContent() throws Exception {

        String accountIdWithNoHistory = "63293cc1-0d33-4ca1-840f-cd1361d9d04b";
        MvcResult result = mockMvc.perform(get("/history/{accountId}", accountIdWithNoHistory)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent())
                .andReturn();

        assertEquals("No data found.", result.getResponse().getContentAsString());
    }


    @Sql({"classpath:sqlScripts/account_with_transactions.sql"})
    @Test
    void testHistoryByAccount_paginationAndSort() throws Exception {

        String accountWithTransactions = "9e66a064-69ad-4672-8a8b-f67514d53536";
        mockMvc.perform(get("/history/{accountId}", accountWithTransactions)
                        .param("offset", "2")
                        .param("limit", "3")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isNotEmpty())
                .andExpect(jsonPath("$.content", hasSize(3)))
                .andExpect(jsonPath("$.content[0].description").value("expenses 4"))
                .andExpect(jsonPath("$.content[1].description").value("expenses 3"))
                .andExpect(jsonPath("$.content[2].description").value("expenses 2"))
                .andExpect(jsonPath("$.totalElements").value(6))
                .andExpect(jsonPath("$.last").value(Boolean.FALSE));
    }
}
