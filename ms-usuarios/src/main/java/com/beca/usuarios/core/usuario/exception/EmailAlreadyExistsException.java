package com.beca.usuarios.core.usuario.exception;

public class EmailAlreadyExistsException extends RuntimeException{

    public EmailAlreadyExistsException(){
        super ("Email already registered");
    }
}
