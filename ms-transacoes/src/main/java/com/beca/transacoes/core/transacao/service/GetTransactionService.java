
package com.beca.transacoes.core.transacao.service;

import com.beca.transacoes.core.transacao.exception.TransactionAccessDeniedException;
import com.beca.transacoes.core.transacao.exception.TransactionNotFoundException;
import com.beca.transacoes.core.transacao.model.Transaction;
import com.beca.transacoes.core.transacao.port.input.GetTransactionUseCase;
import com.beca.transacoes.core.transacao.port.output.TransactionRepositoryPort;

public class GetTransactionService implements GetTransactionUseCase {

    private final TransactionRepositoryPort repository;

    public GetTransactionService(TransactionRepositoryPort repository) {
        this.repository = repository;
    }

    @Override
    public Transaction getById(Long id, Long userId) {

        Transaction transaction = repository.findById(id);

        if (transaction == null) {
            throw new TransactionNotFoundException("Transaction not found");
        }

        if (!transaction.getUserId().equals(userId)) {
            throw new TransactionAccessDeniedException("Access denied");
        }

        return transaction;
    }
}
