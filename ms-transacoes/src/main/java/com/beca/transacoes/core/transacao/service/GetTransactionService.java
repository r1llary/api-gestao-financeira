package com.beca.transacoes.core.transacao.service;

import com.beca.transacoes.core.transacao.model.Transaction;
import com.beca.transacoes.core.transacao.port.input.GetTransactionUseCase;
import com.beca.transacoes.core.transacao.port.output.TransactionRepositoryPort;

import java.util.NoSuchElementException;

public class GetTransactionService implements GetTransactionUseCase {

    private final TransactionRepositoryPort repository;

    public GetTransactionService(TransactionRepositoryPort repository) {
        this.repository = repository;
    }

    @Override
    public Transaction getById(Long id, Long userId) {

        Transaction transaction = repository.findById(id);

        if (transaction == null) {
            throw new NoSuchElementException("Transaction not found");
        }

        if (!transaction.getUserId().equals(userId)) {
            throw new SecurityException("Access denied");
        }

        return transaction;
    }
}