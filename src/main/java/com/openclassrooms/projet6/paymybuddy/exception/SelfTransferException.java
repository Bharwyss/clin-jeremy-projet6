package com.openclassrooms.projet6.paymybuddy.exception;

public class SelfTransferException extends BusinessException {
    public SelfTransferException(String message) {
        super(message);
    }
}