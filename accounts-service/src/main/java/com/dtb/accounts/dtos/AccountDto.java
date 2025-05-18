package com.dtb.accounts.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountDto {
    private String id;
    private String customerId;
    private String iban;
    private String bicSwift;
    private List<CardInfo> cards;

    /*public AccountDto(String bicSwift, String customerId, String iban) {
        this.bicSwift=bicSwift;
        this.customerId=customerId;
        this.iban=iban;
    }*/
    @Data
    public static class CardInfo {
        private Long cardId;
        private String cardAlias;
        private String type;
    }
}
