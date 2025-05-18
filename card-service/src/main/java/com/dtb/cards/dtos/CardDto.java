package com.dtb.cards.dtos;

import com.dtb.cards.enums.CardType;
import com.dtb.cards.models.Card;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CardDto {
    private Long id;
    private String cardAlias;
    private Long accountId;
    private CardType type;
    private String pan;
    private String cvv;

    public static CardDto fromEntity(Card card, boolean showUnmasked) {
        CardDto dto = new CardDto();
        dto.setId(card.getCardId());
        dto.setCardAlias(card.getCardAlias());
        dto.setAccountId(card.getCardAccountId());
        dto.setType(card.getCardType());

        if (showUnmasked) {
            dto.setPan(card.getCardPan());
            dto.setCvv(card.getCardCvv());
        } else {
            dto.setPan(maskPan(card.getCardPan()));
            dto.setCvv("***");
        }

        return dto;
    }

    private static String maskPan(String pan) {
        if (pan == null || pan.length() < 4) {
            return pan;
        }

        return "****-****-****-" + pan.substring(pan.length() - 4);
    }

    public static List<CardDto> fromEntityDto(List<Card> cards, boolean showUnmasked) {
        return cards.stream()
                .map(card -> fromEntity(card, showUnmasked))
                .collect(Collectors.toList());
    }
}
