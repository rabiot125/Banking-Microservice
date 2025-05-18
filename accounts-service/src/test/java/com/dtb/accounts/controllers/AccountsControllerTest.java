package com.dtb.accounts.controllers;

import com.dtb.accounts.dtos.AccountDto;
import com.dtb.accounts.dtos.AccountsListResponseDto;
import com.dtb.accounts.dtos.CreateAccountDto;
import com.dtb.accounts.services.AccountService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class AccountsControllerTest {

    private MockMvc mockMvc;

    @Mock
    private AccountService accountService;

    @InjectMocks
    private AccountsController accountsController;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(accountsController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void testGetAllAccounts() throws Exception {
        AccountsListResponseDto responseDto = new AccountsListResponseDto();
        List<AccountDto> accounts = new ArrayList<>();
        AccountDto accountDto = createSampleAccountDto();
        accounts.add(accountDto);
        responseDto.setAccounts(accounts);
        responseDto.setPage(0);
        responseDto.setTotalItems(1);
        responseDto.setTotalPages(1);

        when(accountService.findAccounts(anyString(), anyString(), anyString(), anyInt(), anyInt()))
                .thenReturn(responseDto);

        mockMvc.perform(get("/api/accounts")
                        .param("iban", "IBAN123")
                        .param("bicSwift", "BIC001")
                        .param("cardAlias", "Card1")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accounts", hasSize(1)))
                .andExpect(jsonPath("$.accounts[0].id", is("1")))
                .andExpect(jsonPath("$.accounts[0].iban", is("IBAN123")))
                .andExpect(jsonPath("$.totalItems", is(1)));

        verify(accountService).findAccounts("IBAN123", "BIC001", "Card1", 0, 10);
    }

    @Test
    void testGetAllAccountsWithDefaultPagination() throws Exception {
        AccountsListResponseDto responseDto = new AccountsListResponseDto();
        responseDto.setAccounts(new ArrayList<>());
        responseDto.setPage(0);
        responseDto.setTotalItems(0);
        responseDto.setTotalPages(0);

        when(accountService.findAccounts(any(), any(), any(), anyInt(), anyInt()))
                .thenReturn(responseDto);

        mockMvc.perform(get("/api/accounts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page", is(0)));

        verify(accountService).findAccounts(null, null, null, 0, 10);
    }
    @Test
    void testFindAccountById() throws Exception {
        AccountDto accountDto = createSampleAccountDto();

        when(accountService.findById(1L)).thenReturn(accountDto);

        mockMvc.perform(get("/api/accounts/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is("1")))
                .andExpect(jsonPath("$.iban", is("IBAN123")))
                .andExpect(jsonPath("$.bicSwift", is("BIC001")));

        verify(accountService).findById(1L);
    }

    @Test
    void testCreateAccount() throws Exception {
        CreateAccountDto createDto = new CreateAccountDto();
        createDto.setIban("IBAN123");
        createDto.setBicSwift("BIC001");
        createDto.setCustomerId("100");

        AccountDto responseDto = createSampleAccountDto();

        when(accountService.createAccountDto(any(CreateAccountDto.class))).thenReturn(responseDto);

        mockMvc.perform(post("/api/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is("1")))
                .andExpect(jsonPath("$.iban", is("IBAN123")));

        verify(accountService).createAccountDto(any(CreateAccountDto.class));
    }

    @Test
    void testUpdateAccountDetails() throws Exception {
        AccountDto updateDto = new AccountDto();
        updateDto.setIban("NEWIBAN");
        updateDto.setBicSwift("NEWBIC");
        updateDto.setCustomerId("200");

        AccountDto responseDto = new AccountDto();
        responseDto.setId("1");
        responseDto.setIban("NEWIBAN");
        responseDto.setBicSwift("NEWBIC");
        responseDto.setCustomerId("200");

        when(accountService.updateAccount(any(AccountDto.class))).thenReturn(responseDto);

        mockMvc.perform(put("/api/accounts/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is("1")))
                .andExpect(jsonPath("$.iban", is("NEWIBAN")))
                .andExpect(jsonPath("$.bicSwift", is("NEWBIC")));

        verify(accountService).updateAccount(any(AccountDto.class));
    }

    @Test
    void testDeleteAccount() throws Exception {
        doNothing().when(accountService).deleteAccount(1L);

        mockMvc.perform(delete("/api/accounts/1"))
                .andExpect(status().isNoContent());

        verify(accountService).deleteAccount(1L);
    }

    private AccountDto createSampleAccountDto() {
        AccountDto accountDto = new AccountDto();
        accountDto.setId("1");
        accountDto.setIban("IBAN123");
        accountDto.setBicSwift("BIC001");
        accountDto.setCustomerId("100");
        return accountDto;
    }
}