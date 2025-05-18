package com.dtb.accounts.feigns;

import com.dtb.accounts.dtos.AccountDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "cards-service", url = "${cards-service.url}")
public interface CardServiceClient {
/*Feigning the card service*/
    @GetMapping("/api/cards/{id}/accounts")
    List<AccountDto.CardInfo> getCardsByAccountId(@PathVariable("id") Long accountId);
}
