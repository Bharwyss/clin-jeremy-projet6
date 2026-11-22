package com.openclassrooms.projet6.paymybuddy.exception;

public class ConnectionAlreadyExistsException extends BusinessException{
    public ConnectionAlreadyExistsException(String message){
        super(message);
    }
}
