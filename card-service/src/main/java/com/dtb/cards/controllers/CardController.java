package com.dtb.cards.controllers;

import com.dtb.cards.dtos.CardDto;
import com.dtb.cards.dtos.CardListResponseDto;
import com.dtb.cards.dtos.CreateCardDto;
import com.dtb.cards.enums.CardType;
import com.dtb.cards.services.CardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cards")
@Tag(name = "Card API Operations", description = "Operations for managing customers' cards")
public class CardController {
    private final CardService cardService;
    public CardController(CardService cardService) {
        this.cardService = cardService;
    }
    @GetMapping
    @Operation(summary = "Get paginated list of Cards")
    public ResponseEntity<CardListResponseDto> getCards(
            @RequestParam(required = false) String cardAlias,
            @RequestParam(required = false) CardType type,
            @RequestParam(required = false) String pan,
            @RequestParam(defaultValue = "false") boolean showUnmasked,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
            ) {

        CardListResponseDto response = cardService.findCards(cardAlias, type, pan,showUnmasked, page, size);
        return ResponseEntity.ok(response);
    }
    @GetMapping("/{id}")
    @Operation(summary = "Get card by ID")
    public ResponseEntity<CardDto> getCardById( @PathVariable Long id,
            @Parameter(description = "Show unmasked PAN and CVV") @RequestParam(required = false, defaultValue = "false") boolean showUnmasked) {

        CardDto card = cardService.getCardById(id, showUnmasked);
        return ResponseEntity.ok(card);
    }
    @GetMapping("/{id}/accounts")
    @Operation(summary = "Get card by AccountId")
    public ResponseEntity<List<CardDto>> getCardAliasByCustomerId( @PathVariable Long id,
                                                @Parameter(description = "Show unmasked PAN and CVV") @RequestParam(required = false, defaultValue = "false") boolean showUnmasked) {

        List<CardDto> card = cardService.getCardByAccountId(id, showUnmasked);
        return ResponseEntity.ok(card);
    }
    @PostMapping
    @Operation(summary = "Create a new card")
    public ResponseEntity<CardDto> createCard( @RequestBody CreateCardDto createCardDto) {
        CardDto createdCard = cardService.createCard(createCardDto);
        return new ResponseEntity<>(createdCard, HttpStatus.CREATED);
    }

    @PutMapping("/{id}/alias")
    @Operation(summary = "Update card alias",description = "Updates the alias of an existing card")
    public ResponseEntity<CardDto> updateCardAlias(
            @Parameter(description = "Card ID", required = true) @PathVariable Long id,
            @Parameter(description = "New alias", required = true) @RequestBody String newAlias) {

        CardDto updatedCard = cardService.updateCardAlias(id, newAlias);
        return ResponseEntity.ok(updatedCard);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a card by Id")
    public ResponseEntity<Void> deleteCard(@PathVariable Long id) {
        cardService.deleteCard(id);
        return ResponseEntity.noContent().build();
    }
}
