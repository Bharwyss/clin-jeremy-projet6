package com.openclassrooms.projet6.paymybuddy.exception;

public class BusinessException extends RuntimeException {
    public BusinessException(String message) {
        super(message);
    }
}