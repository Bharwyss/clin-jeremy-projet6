package com.openclassrooms.projet6.paymybuddy.exception;

public class SelfConnectionException extends RuntimeException{
    public SelfConnectionException(String message){
        super(message);
    }
}
