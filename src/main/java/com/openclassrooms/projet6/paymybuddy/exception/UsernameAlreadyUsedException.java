package com.openclassrooms.projet6.paymybuddy.exception;

public class UsernameAlreadyUsedException extends BusinessException{
    public UsernameAlreadyUsedException(String message) {
        super(message);
    }
}
