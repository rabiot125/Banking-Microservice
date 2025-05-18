package com.dtb.accounts.services;

import com.dtb.accounts.dtos.AccountDto;
import com.dtb.accounts.dtos.AccountsListResponseDto;
import com.dtb.accounts.dtos.CreateAccountDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public interface AccountService {

    AccountsListResponseDto findAccounts(String iban, String bicSwift, String cardAlias, int page, int size);

    AccountDto findById(Long id);

    AccountDto createAccountDto (CreateAccountDto createAccountDto);

    AccountDto updateAccount(AccountDto dto);

    void  deleteAccount(Long id);
}
