package com.openclassrooms.projet6.paymybuddy.exception;

public class EmailAlreadyUsedException extends BusinessException{
    public EmailAlreadyUsedException(String message) {
        super(message);
    }
}
