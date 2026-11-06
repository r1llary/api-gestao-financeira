package com.beca.transacoes.core.conta.exception;

public class AccountNotFoundException  extends RuntimeException{

    public AccountNotFoundException(){
        super("Account not found");
    }
}
