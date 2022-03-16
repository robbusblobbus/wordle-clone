package com.robbie.wordleclone;

public class NotEnglishException extends Exception {
    public NotEnglishException(String errorMessage) {
        super(errorMessage);
    }
}
