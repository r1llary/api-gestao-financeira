
package com.beca.transacoes.core.transacao.exception;

public class TransactionAccessDeniedException extends RuntimeException {

    public TransactionAccessDeniedException(String message) {
        super(message);
    }
}
