package com.dtb.cards.dtos;

import com.dtb.cards.enums.CardType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateCardDto {
    private Long id;
    private String cardAlias;
    private Long accountId;
    private CardType type;
    private String pan;
    private String cvv;
}
