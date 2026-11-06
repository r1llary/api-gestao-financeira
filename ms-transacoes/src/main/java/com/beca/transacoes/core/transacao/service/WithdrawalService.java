package com.beca.transacoes.core.transacao.service;

import com.beca.transacoes.core.transacao.model.Transaction;
import com.beca.transacoes.core.transacao.model.TransactionType;
import com.beca.transacoes.core.transacao.model.TransactionStatus;
import com.beca.transacoes.core.transacao.port.input.WithdrawalUseCase;
import com.beca.transacoes.core.transacao.port.output.TransactionRepositoryPort;

import java.math.BigDecimal;
import java.time.Instant;

public class WithdrawalService implements WithdrawalUseCase {

    private final TransactionRepositoryPort repository;
    private final TransactionValidationService validationService;

    public WithdrawalService(
            TransactionRepositoryPort repository,
            TransactionValidationService validationService
    ) {
        this.repository = repository;
        this.validationService = validationService;
    }

    @Override
    public Transaction withdraw(
            Long userId,
            BigDecimal amount,
            String description,
            String category
    ) {
        Instant date = Instant.now();

        String reason = validationService.validate(userId, amount, date);

        TransactionStatus status = TransactionStatus.APPROVED;

        if (reason != null) {
            status = TransactionStatus.REJECTED;
        }

        Transaction transaction = new Transaction(
                null,
                userId,
                amount,
                TransactionType.WITHDRAWAL,
                date,
                description,
                category,
                status,
                null,
                reason
        );

        return repository.save(transaction);
    }
}