package com.dtb.cards.repository;

import com.dtb.cards.dtos.CardDto;
import com.dtb.cards.enums.CardType;
import com.dtb.cards.models.Card;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
public interface CardRepository extends JpaRepository<Card,Long> {
    Page<Card> findByCardId(Long cardId, Pageable pageable);
    long countByCardAccountIdAndCardType(Long cardAccountId, CardType type);
    long countByCardAccountId(Long cardAccountId);
    List<Card> findAllByCardAccountId(Long cardAccountId);
    Optional<Card> findByCardAccountIdAndCardType(Long cardAccountId, CardType type);

    @Query("SELECT c FROM Card c WHERE " +
            "(:cardAlias IS NULL OR c.cardAlias LIKE %:cardAlias%) AND " +
            "(:type IS NULL OR c.cardType = :type) AND " +
            "(:pan IS NULL OR c.cardPan LIKE %:pan%)")
    Page<Card> findCardByCardAliasOrCardPanOrCardType(@Param("cardAlias") String cardAlias, @Param("type") CardType type, @Param("pan") String pan, Pageable pageable);
}
