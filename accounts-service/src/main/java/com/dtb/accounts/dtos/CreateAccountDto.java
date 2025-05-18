package com.dtb.accounts.dtos;

import lombok.Data;

@Data
public class CreateAccountDto {
    private String customerId;
    private String iban;
    private String bicSwift;
}
