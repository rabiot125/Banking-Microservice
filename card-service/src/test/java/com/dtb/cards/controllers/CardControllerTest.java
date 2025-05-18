package com.dtb.cards.controllers;

import com.dtb.cards.dtos.CardDto;
import com.dtb.cards.dtos.CardListResponseDto;
import com.dtb.cards.dtos.CreateCardDto;
import com.dtb.cards.enums.CardType;
import com.dtb.cards.services.CardService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class CardControllerTest {
    @Mock
    private CardService cardService;
    @InjectMocks
    private CardController cardController;
    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(cardController).build();
        objectMapper = new ObjectMapper();
    }
    @Test
    void getCards_withAllParameters_returnsFilteredCards() throws Exception {
        CardListResponseDto responseDto = createSampleCardListResponse();
        when(cardService.findCards(eq("Alias1"), eq(CardType.VIRTUAL), eq("1234"), eq(true), eq(0), eq(10)))
                .thenReturn(responseDto);

        // Act & Assert
        mockMvc.perform(get("/api/cards")
                        .param("cardAlias", "Alias1")
                        .param("type", "VIRTUAL")
                        .param("pan", "1234")
                        .param("showUnmasked", "true")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cards", hasSize(2)))
                .andExpect(jsonPath("$.cards[0].cardAlias", is("Alias1")))
                .andExpect(jsonPath("$.totalItems", is(2)));

        verify(cardService).findCards("Alias1", CardType.VIRTUAL, "1234", true, 0, 10);
    }

    @Test
    void getCards_withDefaultParameters_returnsAllCards() throws Exception {
        CardListResponseDto responseDto = createSampleCardListResponse();
        when(cardService.findCards(isNull(), isNull(), isNull(), eq(false), eq(0), eq(10)))
                .thenReturn(responseDto);

        // Act & Assert
        mockMvc.perform(get("/api/cards"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cards", hasSize(2)))
                .andExpect(jsonPath("$.page", is(0)));

        verify(cardService).findCards(null, null, null, false, 0, 10);
    }

    @Test
    void getCardById_withShowUnmaskedTrue_returnsUnmaskedCard() throws Exception {

        CardDto cardDto = createSampleCardDto();
        cardDto.setPan("1234567890123456"); // Unmasked PAN
        when(cardService.getCardById(eq(1L), eq(true))).thenReturn(cardDto);

        // Act & Assert
        mockMvc.perform(get("/api/cards/1")
                        .param("showUnmasked", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.pan", is("1234567890123456")));

        verify(cardService).getCardById(Long.valueOf("1"), true);
    }

    @Test
    void getCardById_withShowUnmaskedFalse_returnsMaskedCard() throws Exception {

        CardDto cardDto = createSampleCardDto();
        cardDto.setPan("XXXXXXXXXXXX3456"); // Masked PAN
        when(cardService.getCardById(eq(1L), eq(false))).thenReturn(cardDto);

        // Act & Assert
        mockMvc.perform(get("/api/cards/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.pan", is("XXXXXXXXXXXX3456")));

        verify(cardService).getCardById(Long.valueOf("1"), false);
    }

    @Test
    void getCardByAccountId_withValidAccountId_returnsCards() throws Exception {

        List<CardDto> cards = Arrays.asList(createSampleCardDto(), createSampleCardDto());
        when(cardService.getCardByAccountId(eq(100L), eq(false))).thenReturn(cards);

        // Act & Assert
        mockMvc.perform(get("/api/cards/100/accounts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)));

        verify(cardService).getCardByAccountId(100L, false);
    }

    @Test
    void getCardByAccountId_withShowUnmaskedTrue_returnsUnmaskedCards() throws Exception {
        List<CardDto> cards = Arrays.asList(createSampleCardDto(), createSampleCardDto());
        cards.forEach(card -> card.setPan("1234567890123456")); // Unmasked PAN
        when(cardService.getCardByAccountId(eq(100L), eq(true))).thenReturn(cards);

        // Act & Assert
        mockMvc.perform(get("/api/cards/100/accounts")
                        .param("showUnmasked", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].pan", is("1234567890123456")));

        verify(cardService).getCardByAccountId(100L, true);
    }

    @Test
    void createCard_withValidData_returnsCreatedCard() throws Exception {
        CreateCardDto createDto = new CreateCardDto();
        createDto.setCardAlias("New Card");
        createDto.setType(CardType.PHYSICAL);
        createDto.setPan("1234567890123456");
        createDto.setCvv("123");
        createDto.setAccountId(200L);

        CardDto createdCard = createSampleCardDto();
        createdCard.setCardAlias("New Card");
        createdCard.setType(CardType.PHYSICAL);

        when(cardService.createCard(any(CreateCardDto.class))).thenReturn(createdCard);

        // Act & Assert
        mockMvc.perform(post("/api/cards")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.cardAlias", is("New Card")))
                .andExpect(jsonPath("$.type", is("PHYSICAL")));

        verify(cardService).createCard(any(CreateCardDto.class));
    }

    @Test
    void updateCardAlias_withValidAlias_returnsUpdatedCard() throws Exception {
        String newAlias = "Updated Alias";
        CardDto updatedCard = createSampleCardDto();
        updatedCard.setCardAlias(newAlias);

        when(cardService.updateCardAlias(eq(1L), eq(newAlias))).thenReturn(updatedCard);

        // Act & Assert
        mockMvc.perform(put("/api/cards/1/alias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newAlias))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.cardAlias", is(newAlias)));

        verify(cardService).updateCardAlias(1L, newAlias);
    }

    @Test
    void deleteCard_withValidId_returnsNoContent() throws Exception {

        doNothing().when(cardService).deleteCard(1L);

        // Act & Assert
        mockMvc.perform(delete("/api/cards/1"))
                .andExpect(status().isNoContent());

        verify(cardService).deleteCard(1L);
    }

    @Test
    void getCards_withEnumConversionFailure_handlesException() throws Exception {

        mockMvc.perform(get("/api/cards")
                        .param("type", "INVALID_TYPE"))
                .andExpect(status().isBadRequest());

        verify(cardService, never()).findCards(any(), any(), any(), anyBoolean(), anyInt(), anyInt());
    }


    private CardListResponseDto createSampleCardListResponse() {
        CardListResponseDto responseDto = new CardListResponseDto();
        List<CardDto> cards = new ArrayList<>();
        cards.add(createSampleCardDto());
        cards.add(createSampleCardDto());
        responseDto.setCards(cards);
        responseDto.setPage(0);
        responseDto.setTotalItems(2);
        responseDto.setTotalPages(1);
        return responseDto;
    }

    private CardDto createSampleCardDto() {
        CardDto dto = new CardDto();
        dto.setId(Long.valueOf("1"));
        dto.setCardAlias("Alias1");
        dto.setType(CardType.PHYSICAL);
        dto.setPan("XXXXXXXXXXXX3456"); // Default to masked
        dto.setCvv("XXX");
        dto.setAccountId(100L);
        return dto;
    }
}