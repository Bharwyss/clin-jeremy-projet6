package com.openclassrooms.projet6.paymybuddy.exception;

public class InvalidAmountException extends BusinessException {
    public InvalidAmountException(String message) {
        super(message);
    }
}