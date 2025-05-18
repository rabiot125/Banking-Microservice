package com.dtb.accounts.controllers;

import com.dtb.accounts.dtos.AccountDto;
import com.dtb.accounts.dtos.AccountsListResponseDto;
import com.dtb.accounts.dtos.CreateAccountDto;
import com.dtb.accounts.services.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/accounts")
@Tag(name = "Accounts API Operations",description = "Operations for managing accounts")
public class AccountsController {
    private final AccountService accountService;

    public AccountsController(AccountService accountService) {
        this.accountService = accountService;
    }
    @GetMapping
    @Operation(summary = "Get Accounts with Cards Details")
    public ResponseEntity<AccountsListResponseDto> getAllCustomers(
            @RequestParam(required = false) String iban,
            @RequestParam(required = false)  String bicSwift,
            @RequestParam(required = false) String cardAlias,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        AccountsListResponseDto customerListResponse = accountService.findAccounts(
                iban, bicSwift, cardAlias, page, size);

        return ResponseEntity.ok(customerListResponse);
    }
    @GetMapping("/{id}")
    @Operation(summary = "Get account by Id")
    public ResponseEntity<AccountDto> findAccountById ( @PathVariable Long id){
        return  ResponseEntity.ok(accountService.findById(id));
    }
    @PostMapping()
    @Operation(summary = "Create new account")
    public ResponseEntity<AccountDto> createAccount(@RequestBody CreateAccountDto accountDto){

        return ResponseEntity.status(HttpStatus.CREATED).body(accountService.createAccountDto(accountDto));
    }
    @PutMapping("/{id}")
    @Operation(summary = "Update Account details")
    public ResponseEntity<AccountDto> updateAccountDetails( @PathVariable Long id,@RequestBody AccountDto dto){
        dto.setId(String.valueOf((id)));
        return ResponseEntity.ok(accountService.updateAccount(dto));
    }
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete acccount by ID")
    private ResponseEntity<Void> deleteAccount(@PathVariable  Long id){
        accountService.deleteAccount(id);
        return ResponseEntity.noContent().build();
    }

}
