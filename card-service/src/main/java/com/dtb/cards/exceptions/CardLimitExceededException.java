package com.dtb.cards.exceptions;

public class CardLimitExceededException extends RuntimeException{
    public CardLimitExceededException(String message){
        super(message);
    }
}
