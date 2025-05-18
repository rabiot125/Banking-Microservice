package com.dtb.accounts.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountsListResponseDto {
    private List<AccountDto> accounts;
    private int page;
    private int size;
    private long totalItems;
    private int totalPages;
}
