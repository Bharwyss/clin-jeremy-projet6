package com.openclassrooms.projet6.paymybuddy.exception;

public class ConnectionAlreadyExistsException extends RuntimeException{
    public ConnectionAlreadyExistsException(String message){
        super(message);
    }
}
