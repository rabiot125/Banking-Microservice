package com.dtb.cards.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CardListResponseDto {
    private List<CardDto> cards;
    private int page;
    private int size;
    private long totalItems;
    private int totalPages;
}
