package com.dtb.cards.models;

import com.dtb.cards.enums.CardType;
import jakarta.persistence.*;
import lombok.Data;
@Data
@Entity
@Table(name = "cards")
public class Card {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long cardId;
    private String cardAlias;
    private Long cardAccountId;
    @Enumerated(EnumType.STRING)
    private CardType cardType; //PHYSICAL or VIRTUAL
    private String cardPan;
    private String cardCvv;
}
