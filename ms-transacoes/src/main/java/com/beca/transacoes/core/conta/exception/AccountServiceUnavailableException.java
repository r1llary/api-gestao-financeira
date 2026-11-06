package com.beca.transacoes.core.conta.exception;

public class AccountServiceUnavailableException extends RuntimeException{

    public AccountServiceUnavailableException(){
        super("Account service unavailable");
    }
}
