package com.dtb.cards.services;

import com.dtb.cards.dtos.CardDto;
import com.dtb.cards.dtos.CardListResponseDto;
import com.dtb.cards.dtos.CreateCardDto;
import com.dtb.cards.enums.CardType;
import com.dtb.cards.exceptions.CardLimitExceededException;
import com.dtb.cards.exceptions.RecordNotFoundException;
import com.dtb.cards.models.Card;
import com.dtb.cards.repository.CardRepository;
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
public class CardServiceImpl implements CardService {
    private final CardRepository cardRepository;
    public CardServiceImpl(CardRepository cardRepository) {
        this.cardRepository = cardRepository;
    }

    @Override
    public CardListResponseDto findCards(String cardAlias, CardType type, String pan, boolean showUnmasked, int page, int size) {
        Pageable pageable = PageRequest.of(page,size);

        Page<Card> cards = cardRepository.findCardByCardAliasOrCardPanOrCardType(cardAlias, type, pan, pageable);
        List<CardDto> cardDtoList = cards.getContent().stream()
                .map(card ->
                        CardDto.fromEntity(card, showUnmasked)
                ).collect(Collectors.toList());
        CardListResponseDto listResponseDto = new CardListResponseDto();
        listResponseDto.setCards(cardDtoList);
        listResponseDto.setSize(cards.getSize());
        listResponseDto.setPage(cards.getNumber());
        listResponseDto.setTotalItems(cards.getTotalElements());
        listResponseDto.setTotalPages(cards.getTotalPages());

        return listResponseDto;
    }
    @Override
    public CardDto getCardById(Long id, boolean showUnmasked) {
        Card card = cardRepository.findById(id)
                .orElseThrow(() -> new RecordNotFoundException("Card not found with id: " + id));

        return CardDto.fromEntity(card, showUnmasked);
    }
    @Override
    public List<CardDto> getCardByAccountId(Long id, boolean showUnmasked) {
        List<Card> cards = cardRepository.findAllByCardAccountId(id);

        if (cards.isEmpty()) {
            throw new RecordNotFoundException("No cards found for customer with id: " + id);
        }
        return CardDto.fromEntityDto(cards, showUnmasked);
    }
    @Override
    public CardDto createCard(CreateCardDto createCardDto) {
        /* Check if account already has maximum allowed cards (2)*/
        long cardCount = cardRepository.countByCardAccountId(createCardDto.getAccountId());
        if (cardCount >= 2) {
            throw new CardLimitExceededException ("Account can have maximum of 2 cards");
        }

        /* Check if account already has a card of this type*/
        boolean cardTypeExists = cardRepository.findByCardAccountIdAndCardType(
                createCardDto.getAccountId(), createCardDto.getType()).isPresent();

        if (cardTypeExists) {
            throw new CardLimitExceededException(
                    "Account already has a " + createCardDto.getType() + " card. Only one card of each type is allowed.");
        }
        log.info("Creating a new card");
        Card card = new Card();
        card.setCardAccountId(createCardDto.getAccountId());
        card.setCardCvv(createCardDto.getCvv());
        card.setCardAlias(createCardDto.getCardAlias());
        card.setCardPan(createCardDto.getPan());
        card.setCardType(createCardDto.getType());

        /* Save the card*/
        Card savedCard = cardRepository.save(card);
        return CardDto.fromEntity(savedCard, false);
    }

    @Override
    @Transactional
    public CardDto updateCardAlias(Long id, String newAlias) {
        Card card = cardRepository.findById(id)
                .orElseThrow(() -> new RecordNotFoundException("Card not found with id: " + id));
       log.info("Updating card ===");
        card.setCardAlias(newAlias);
        Card updatedCard = cardRepository.save(card);

        return CardDto.fromEntity(updatedCard, false);
    }
    @Override
    @Transactional
    public void deleteCard(Long id) {
        if(!cardRepository.existsById(id)){
            throw new RecordNotFoundException("Card not found with id: " + id);
        }
        log.info("Deleting card with id : "+ id);
        cardRepository.deleteById(id);
    }
}
