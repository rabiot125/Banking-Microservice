package com.dtb.accounts.exceptions;

public class RecordNotFoundException extends RuntimeException {
    public RecordNotFoundException(String message){
        super(message);
    }
}
