package com.dtb.cards.services;


import com.dtb.cards.dtos.CardDto;
import com.dtb.cards.dtos.CardListResponseDto;
import com.dtb.cards.dtos.CreateCardDto;
import com.dtb.cards.enums.CardType;

import java.util.List;

public interface CardService {
    CardListResponseDto findCards(String cardAlias, CardType type, String pan,boolean showUnmasked, int page, int size);
    CardDto getCardById(Long id, boolean showUnmasked);

    List<CardDto> getCardByAccountId(Long id, boolean showUnmasked);

    CardDto createCard(CreateCardDto createCardDto);
    CardDto updateCardAlias(Long id, String newAlias);
    void deleteCard(Long id);
}
