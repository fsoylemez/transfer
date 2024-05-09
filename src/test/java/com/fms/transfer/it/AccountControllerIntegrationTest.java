package com.fms.transfer.it;

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

import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-integrationtest.yml")
class AccountControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testAccountsByClient_clientNotFound() throws Exception {

        UUID notExistingClientId = UUID.randomUUID();

        MvcResult result = mockMvc.perform(get("/accounts/byClient/{clientId}", notExistingClientId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andReturn();

        assertEquals(String.format("Client with identifier %s could not be found.", notExistingClientId), result.getResponse().getContentAsString());
    }

    @Sql({"classpath:sqlScripts/client_with_no_accounts.sql"})
    @Test
    void testAccountsByClient_noContent() throws Exception {

        String clientIdWithNoAccounts = "1224d408-35ad-4ce6-a638-03bc7b2542be";
        MvcResult result = mockMvc.perform(get("/accounts/byClient/{clientId}", clientIdWithNoAccounts)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent())
                .andReturn();

        assertEquals("No data found.", result.getResponse().getContentAsString());
    }

    @Sql({"classpath:sqlScripts/client_with_two_accounts.sql"})
    @Test
    void testAccountsByClient_success() throws Exception {

        String clientIdWithTwoAccounts = "7ad26675-2bf3-407c-8b15-3d9dee7c70e4";

        mockMvc.perform(get("/accounts/byClient/{clientId}", clientIdWithTwoAccounts)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].accountName").value("dollarAcc"))
                .andExpect(jsonPath("$[1].accountName").value("euroAcc"));
    }
}
