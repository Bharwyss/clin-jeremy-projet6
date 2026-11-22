package com.openclassrooms.projet6.paymybuddy.exception;

public class SelfConnectionException extends BusinessException{
    public SelfConnectionException(String message){
        super(message);
    }
}
