package com.dtb.accounts.services;

import com.dtb.accounts.dtos.AccountDto;
import com.dtb.accounts.dtos.AccountsListResponseDto;
import com.dtb.accounts.dtos.CreateAccountDto;
import com.dtb.accounts.exceptions.RecordNotFoundException;
import com.dtb.accounts.feigns.CardServiceClient;
import com.dtb.accounts.models.Account;
import com.dtb.accounts.repository.AccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AccountServiceImplTest {
    @Mock
    private AccountRepository accountRepository;
    @Mock
    private CardServiceClient cardServiceClient;
    @InjectMocks
    private AccountServiceImpl accountService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private Account sampleAccount() {
        Account account = new Account();
        account.setId(1L);
        account.setCustomerId(String.valueOf(100L));
        account.setIban("IBAN123");
        account.setBicSwift("BIC001");
        return account;
    }

    private AccountDto.CardInfo sampleCard() {
        AccountDto.CardInfo card = new AccountDto.CardInfo();
        card.setCardAlias("CardAlias123");
        return card;
    }

    @Test
    void testFindByIdSuccess() {
        Account account = sampleAccount();
        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
        when(cardServiceClient.getCardsByAccountId(100L)).thenReturn(List.of(sampleCard()));

        AccountDto dto = accountService.findById(1L);

        assertNotNull(dto);
        assertEquals("IBAN123", dto.getIban());
        verify(accountRepository).findById(1L);
    }

    @Test
    void testFindByIdNotFound() {
        when(accountRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RecordNotFoundException.class, () -> accountService.findById(1L));
    }

    @Test
    void testCreateAccount() {
        CreateAccountDto createDto = new CreateAccountDto();
        createDto.setBicSwift("BIC001");
        createDto.setIban("IBAN123");
        createDto.setCustomerId(String.valueOf(200L));

        Account savedAccount = sampleAccount();
        when(accountRepository.save(any(Account.class))).thenReturn(savedAccount);
        when(cardServiceClient.getCardsByAccountId(anyLong())).thenReturn(Collections.emptyList());

        AccountDto dto = accountService.createAccountDto(createDto);

        assertNotNull(dto);
        assertEquals("IBAN123", dto.getIban());
        verify(accountRepository).save(any(Account.class));
    }

    @Test
    void testUpdateAccountSuccess() {
        Account existing = sampleAccount();
        when(accountRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(accountRepository.save(any(Account.class))).thenReturn(existing);

        AccountDto dto = new AccountDto();
        dto.setId("1");
        dto.setBicSwift("NEWBIC");
        dto.setIban("NEWIBAN");
        dto.setCustomerId(String.valueOf(300L));

        AccountDto result = accountService.updateAccount(dto);

        assertEquals("NEWIBAN", result.getIban());
        verify(accountRepository).save(any(Account.class));
    }

    @Test
    void testUpdateAccountNotFound() {
        when(accountRepository.findById(1L)).thenReturn(Optional.empty());

        AccountDto dto = new AccountDto();
        dto.setId("1");

        assertThrows(RecordNotFoundException.class, () -> accountService.updateAccount(dto));
    }

    @Test
    void testDeleteAccountSuccess() {
        when(accountRepository.existsById(1L)).thenReturn(true);
        doNothing().when(accountRepository).deleteById(1L);

        assertDoesNotThrow(() -> accountService.deleteAccount(1L));
        verify(accountRepository).deleteById(1L);
    }

    @Test
    void testDeleteAccountNotFound() {
        when(accountRepository.existsById(1L)).thenReturn(false);

        assertThrows(RecordNotFoundException.class, () -> accountService.deleteAccount(1L));
    }

    @Test
    void testFindAccountsWithoutCardAlias() {
        Account account = sampleAccount();
        Page<Account> page = new PageImpl<>(List.of(account));
        when(accountRepository.findWithFilters("IBAN123", "BIC001", PageRequest.of(0, 10))).thenReturn(page);
        when(cardServiceClient.getCardsByAccountId(100L)).thenReturn(List.of(sampleCard()));

        AccountsListResponseDto dto = accountService.findAccounts("IBAN123", "BIC001", null, 0, 10);

        assertEquals(1, dto.getAccounts().size());
    }

    @Test
    void testFindAccountsWithCardAlias() {
        Account account = sampleAccount();
        Page<Account> page = new PageImpl<>(List.of(account));
        AccountDto.CardInfo card = sampleCard();
        card.setCardAlias("TestCardAlias");

        when(accountRepository.findWithFilters("IBAN123", "BIC001", PageRequest.of(0, 10))).thenReturn(page);
        when(cardServiceClient.getCardsByAccountId(anyLong())).thenReturn(List.of(card));

        AccountsListResponseDto dto = accountService.findAccounts("IBAN123", "BIC001", "TestCardAlias", 0, 10);

        assertEquals(1, dto.getAccounts().size());
    }

    @Test
    void testFindAccountsWithCardAliasNoMatch() {
        Account account = sampleAccount();
        Page<Account> page = new PageImpl<>(List.of(account));

        AccountDto.CardInfo card = sampleCard();
        card.setCardAlias("AnotherCard");

        when(accountRepository.findWithFilters("IBAN123", "BIC001", PageRequest.of(0, 10))).thenReturn(page);
        when(cardServiceClient.getCardsByAccountId(anyLong())).thenReturn(List.of(card));

        AccountsListResponseDto dto = accountService.findAccounts("IBAN123", "BIC001", "UnmatchedAlias", 0, 10);

        assertEquals(0, dto.getAccounts().size());
    }

    @Test
    void testToDtoHandlesCardServiceException() {

        Account account = sampleAccount();
        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));


        when(cardServiceClient.getCardsByAccountId(anyLong())).thenThrow(new RuntimeException("Service Down"));

        AccountDto dto = accountService.findById(1L);
        assertNotNull(dto);

        assertNull(dto.getCards());
    }
}