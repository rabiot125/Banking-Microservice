package com.dtb.accounts.services;

import com.dtb.accounts.dtos.AccountDto;
import com.dtb.accounts.dtos.AccountsListResponseDto;
import com.dtb.accounts.dtos.CreateAccountDto;
import com.dtb.accounts.exceptions.RecordNotFoundException;
import com.dtb.accounts.feigns.CardServiceClient;
import com.dtb.accounts.models.Account;
import com.dtb.accounts.repository.AccountRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class AccountServiceImpl implements AccountService {
    private final AccountRepository accountRepository;

    private final CardServiceClient cardServiceClient;

    public AccountServiceImpl(AccountRepository accountRepository, CardServiceClient cardServiceClient) {
        this.accountRepository = accountRepository;
        this.cardServiceClient = cardServiceClient;
    }

    @Override
    public AccountsListResponseDto findAccounts(String iban, String bicSwift, String cardAlias, int page, int size)
    {
        Pageable pageable = PageRequest.of(page,size);

        Page<Account> accounts =accountRepository.findWithFilters(iban,bicSwift,pageable);

        List<AccountDto>  dtoList = accounts.getContent().stream().
                map(this::toDto).collect(Collectors.toList());

        /**
        *Filter if card alias is provided
         */
        if (cardAlias != null && !cardAlias.isEmpty()) {
            System.out.println("Before filter, accounts1: " + dtoList.size());
            dtoList = dtoList.stream()
                    .filter(accountDTO -> accountDTO.getCards() != null &&
                            accountDTO.getCards().stream()
                                    .anyMatch(card -> card.getCardAlias().toLowerCase().contains(cardAlias.toLowerCase())))
                    .collect(Collectors.toList());
            System.out.println("After filter, accounts2: " + dtoList.size());
        }

        AccountsListResponseDto dto =new AccountsListResponseDto();
        dto.setAccounts(dtoList);
        dto.setPage(accounts.getNumber());
        dto.setSize(accounts.getSize());
        dto.setTotalItems(accounts.getTotalElements());
        dto.setTotalPages(accounts.getTotalPages());
        return dto;

    }
    @Override
    public AccountDto findById(Long id) {
        return accountRepository.findById(id)
                .map(this::toDto)
                .orElseThrow(() -> new RecordNotFoundException("Customer not found with id: " + id));
    }
    @Override
    public AccountDto createAccountDto(CreateAccountDto createAccountDto) {

        Account account = new Account();
        account.setCustomerId(createAccountDto.getCustomerId());
        account.setIban(createAccountDto.getIban());
        account.setBicSwift(createAccountDto.getBicSwift());

        Account saved = accountRepository.save(account);
        return toDto(saved);

    }
    @Override
    @Transactional
    public AccountDto updateAccount(AccountDto dto) {

        Account account = accountRepository.findById(Long.valueOf(dto.getId()))
                .orElseThrow(() -> new RecordNotFoundException("No such card details exists" + dto.getId()));
        log.info("Updating account details");
        account.setBicSwift(dto.getBicSwift());
        account.setIban(dto.getIban());
        account.setCustomerId(dto.getCustomerId());

        Account updatedAcc = accountRepository.save(account);
        return toDto(updatedAcc);
                //new AccountDto(updatedAcc.getBicSwift(), updatedAcc.getCustomerId(), updatedAcc.getIban())

    }

    @Override
    @Transactional
    public void deleteAccount(Long id) {
        if (!accountRepository.existsById(id)) {
            throw new RecordNotFoundException(" Account with id :" + id + "not found");
        }
        accountRepository.deleteById(id);

    }
    private AccountDto toDto(Account account) {
        AccountDto dto = new AccountDto();
        dto.setId(String.valueOf(account.getId()));
        dto.setCustomerId(account.getCustomerId());
        dto.setBicSwift(account.getBicSwift());
        dto.setIban(account.getIban());
        //System.out.println("Test customer ID: " + account.getCustomerId());
        try {
            List<AccountDto.CardInfo> cards = cardServiceClient.getCardsByAccountId(Long.valueOf(account.getCustomerId()));
            dto.setCards(cards);
            System.out.println("Card service returned " + cards.size() + " cards");

        } catch (Exception e) {
            e.printStackTrace();
            dto.setCards(null);
        }
        return dto;
    }

}
