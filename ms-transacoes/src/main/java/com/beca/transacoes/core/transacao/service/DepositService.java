package com.beca.transacoes.core.transacao.service;

import com.beca.transacoes.core.transacao.model.Transaction;
import com.beca.transacoes.core.transacao.model.TransactionStatus;
import com.beca.transacoes.core.transacao.model.TransactionType;
import com.beca.transacoes.core.transacao.port.input.DepositUseCase;
import com.beca.transacoes.core.transacao.port.output.TransactionRepositoryPort;

import java.math.BigDecimal;
import java.time.Instant;

public class DepositService implements DepositUseCase {

    private final TransactionRepositoryPort repository;

    public DepositService(TransactionRepositoryPort repository) {
        this.repository = repository;
    }

    @Override
    public Transaction deposit(
            Long userId,
            BigDecimal amount,
            String description,
            String category
    ) {
        Transaction transaction = new Transaction(
                null,
                userId,
                amount,
                TransactionType.DEPOSIT,
                Instant.now(),
                description,
                category,
                TransactionStatus.APPROVED,
                null,
                null
        );

        return repository.save(transaction);
    }
}
