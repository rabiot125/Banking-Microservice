
package com.dtb.cards.services;

import com.dtb.cards.dtos.CardDto;
import com.dtb.cards.dtos.CardListResponseDto;
import com.dtb.cards.dtos.CreateCardDto;
import com.dtb.cards.enums.CardType;
import com.dtb.cards.exceptions.CardLimitExceededException;
import com.dtb.cards.exceptions.RecordNotFoundException;
import com.dtb.cards.models.Card;
import com.dtb.cards.repository.CardRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CardServiceImplTest {
    @Mock
    private CardRepository cardRepository;
    @InjectMocks
    private CardServiceImpl cardService;

    private Card card1;
    private Card card2;
    private CreateCardDto createCardDto;

    @BeforeEach
    void setUp() {
        // Set up test data
        card1 = new Card();
        card1.setCardId(1L);
        card1.setCardAccountId(100L);
        card1.setCardAlias("Test Card 1");
        card1.setCardPan("1234567890123456");
        card1.setCardCvv("123");
        card1.setCardType(CardType.PHYSICAL);

        card2 = new Card();
        card2.setCardId(2L);
        card2.setCardAccountId(100L);
        card2.setCardAlias("Test Card 2");
        card2.setCardPan("9876543210987654");
        card2.setCardCvv("456");
        card2.setCardType(CardType.VIRTUAL);

        createCardDto = new CreateCardDto();
        createCardDto.setAccountId(100L);
        createCardDto.setCardAlias("New Card");
        createCardDto.setPan("5555666677778888");
        createCardDto.setCvv("789");
        createCardDto.setType(CardType.PHYSICAL);
    }

    @Test
    void findCards_returnsFilteredCards() {

        String cardAlias = "Test";
        CardType type = CardType.PHYSICAL;
        String pan = "1234";
        boolean showUnmasked = true;
        int page = 0;
        int size = 10;

        List<Card> cardList = Arrays.asList(card1);
        Page<Card> cardPage = new PageImpl<>(cardList, PageRequest.of(page, size), 1);

        when(cardRepository.findCardByCardAliasOrCardPanOrCardType(eq(cardAlias), eq(type), eq(pan), any(Pageable.class)))
                .thenReturn(cardPage);

        // Act
        CardListResponseDto result = cardService.findCards(cardAlias, type, pan, showUnmasked, page, size);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getCards().size());
        assertEquals(cardList.get(0).getCardAlias(), result.getCards().get(0).getCardAlias());
        assertEquals(cardList.get(0).getCardPan(), result.getCards().get(0).getPan());
        assertEquals(0, result.getPage());
        assertEquals(1, result.getTotalItems());

        verify(cardRepository).findCardByCardAliasOrCardPanOrCardType(cardAlias, type, pan, PageRequest.of(page, size));
    }

    @Test
    void getCardById_existingCard_returnsCard() {
        Long cardId = 1L;
        when(cardRepository.findById(cardId)).thenReturn(Optional.of(card1));

        // Act
        CardDto result = cardService.getCardById(cardId, true);

        // Assert
        assertNotNull(result);
        assertEquals(card1.getCardId(), result.getId());
        assertEquals(card1.getCardAlias(), result.getCardAlias());
        assertEquals(card1.getCardPan(), result.getPan());

        verify(cardRepository).findById(cardId);
    }

    @Test
    void getCardById_nonExistingCard_throwsException() {
        Long cardId = 999L;
        when(cardRepository.findById(cardId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RecordNotFoundException.class, () -> cardService.getCardById(cardId, false));
        verify(cardRepository).findById(cardId);
    }

    @Test
    void getCardByAccountId_existingAccount_returnsCards() {

        Long accountId = 100L;
        List<Card> cards = Arrays.asList(card1, card2);
        when(cardRepository.findAllByCardAccountId(accountId)).thenReturn(cards);

        // Act
        List<CardDto> result = cardService.getCardByAccountId(accountId, true);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());

        assertEquals(1, result.get(0).getId());
        assertEquals(2, result.get(1).getId());

        assertEquals(100L, result.get(0).getAccountId());
        assertEquals(100L, result.get(1).getAccountId());

        verify(cardRepository).findAllByCardAccountId(accountId);
    }

    @Test
    void getCardByAccountId_nonExistingAccount_throwsException() {
        Long accountId = 999L;
        when(cardRepository.findAllByCardAccountId(accountId)).thenReturn(new ArrayList<>());

        // Act & Assert
        assertThrows(RecordNotFoundException.class, () -> cardService.getCardByAccountId(accountId, false));
        verify(cardRepository).findAllByCardAccountId(accountId);
    }

    @Test
    void createCard_validCard_returnsCreatedCard() {
        // Arrange
        when(cardRepository.countByCardAccountId(createCardDto.getAccountId())).thenReturn(0L);
        when(cardRepository.findByCardAccountIdAndCardType(
                createCardDto.getAccountId(), createCardDto.getType())).thenReturn(Optional.empty());

        Card savedCard = new Card();
        savedCard.setCardId(3L);
        savedCard.setCardAccountId(createCardDto.getAccountId());
        savedCard.setCardAlias(createCardDto.getCardAlias());
        savedCard.setCardPan(createCardDto.getPan());
        savedCard.setCardCvv(createCardDto.getCvv());
        savedCard.setCardType(createCardDto.getType());

        when(cardRepository.save(any(Card.class))).thenReturn(savedCard);

        // Act
        CardDto result = cardService.createCard(createCardDto);

        // Assert
        assertNotNull(result);
        assertEquals(savedCard.getCardId(), result.getId());
        assertEquals(savedCard.getCardAlias(), result.getCardAlias());
        assertEquals(savedCard.getCardType(), result.getType());

        verify(cardRepository).countByCardAccountId(createCardDto.getAccountId());
        verify(cardRepository).findByCardAccountIdAndCardType(createCardDto.getAccountId(), createCardDto.getType());
        verify(cardRepository).save(any(Card.class));
    }

    @Test
    void createCard_exceedMaxCards_throwsException() {

        when(cardRepository.countByCardAccountId(createCardDto.getAccountId())).thenReturn(2L);

        // Act & Assert
        assertThrows(CardLimitExceededException.class, () -> cardService.createCard(createCardDto));

        verify(cardRepository).countByCardAccountId(createCardDto.getAccountId());
        verify(cardRepository, never()).findByCardAccountIdAndCardType(anyLong(), any(CardType.class));
        verify(cardRepository, never()).save(any(Card.class));
    }

    @Test
    void createCard_duplicateCardType_throwsException() {
        when(cardRepository.countByCardAccountId(createCardDto.getAccountId())).thenReturn(1L);
        when(cardRepository.findByCardAccountIdAndCardType(
                createCardDto.getAccountId(), createCardDto.getType())).thenReturn(Optional.of(card1));

        // Act & Assert
        assertThrows(CardLimitExceededException.class, () -> cardService.createCard(createCardDto));

        verify(cardRepository).countByCardAccountId(createCardDto.getAccountId());
        verify(cardRepository).findByCardAccountIdAndCardType(createCardDto.getAccountId(), createCardDto.getType());
        verify(cardRepository, never()).save(any(Card.class));
    }

    @Test
    void updateCardAlias_existingCard_returnsUpdatedCard() {
        // Arrange
        Long cardId = 1L;
        String newAlias = "Updated Card Alias";

        when(cardRepository.findById(cardId)).thenReturn(Optional.of(card1));

        Card updatedCard = new Card();
        updatedCard.setCardId(cardId);
        updatedCard.setCardAccountId(card1.getCardAccountId());
        updatedCard.setCardAlias(newAlias);
        updatedCard.setCardPan(card1.getCardPan());
        updatedCard.setCardCvv(card1.getCardCvv());
        updatedCard.setCardType(card1.getCardType());

        when(cardRepository.save(any(Card.class))).thenReturn(updatedCard);

        // Act
        CardDto result = cardService.updateCardAlias(cardId, newAlias);

        // Assert
        assertNotNull(result);
        assertEquals(cardId, result.getId());
        assertEquals(newAlias, result.getCardAlias());

        verify(cardRepository).findById(cardId);
        verify(cardRepository).save(any(Card.class));
    }

    @Test
    void updateCardAlias_nonExistingCard_throwsException() {
        Long cardId = 999L;
        String newAlias = "Updated Card Alias";

        when(cardRepository.findById(cardId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RecordNotFoundException.class, () -> cardService.updateCardAlias(cardId, newAlias));

        verify(cardRepository).findById(cardId);
        verify(cardRepository, never()).save(any(Card.class));
    }

    @Test
    void deleteCard_existingCard_deletesCard() {
        Long cardId = 1L;
        when(cardRepository.existsById(cardId)).thenReturn(true);
        doNothing().when(cardRepository).deleteById(cardId);

        // Act
        cardService.deleteCard(cardId);

        // Assert
        verify(cardRepository).existsById(cardId);
        verify(cardRepository).deleteById(cardId);
    }

    @Test
    void deleteCard_nonExistingCard_throwsException() {
        Long cardId = 999L;
        when(cardRepository.existsById(cardId)).thenReturn(false);

        // Act & Assert
        assertThrows(RecordNotFoundException.class, () -> cardService.deleteCard(cardId));

        verify(cardRepository).existsById(cardId);
        verify(cardRepository, never()).deleteById(anyLong());
    }
}