
package com.beca.transacoes.core.transacao.service;

import com.beca.transacoes.core.transacao.model.Transaction;
import com.beca.transacoes.core.transacao.model.TransactionStatus;
import com.beca.transacoes.core.transacao.model.TransactionType;
import com.beca.transacoes.core.transacao.port.input.PurchaseUseCase;
import com.beca.transacoes.core.transacao.port.output.TransactionRepositoryPort;

import java.math.BigDecimal;
import java.time.Instant;

public class PurchaseService implements PurchaseUseCase {

    private final TransactionRepositoryPort repository;
    private final TransactionValidationService validationService;

    public PurchaseService(
            TransactionRepositoryPort repository,
            TransactionValidationService validationService) {

        this.repository = repository;
        this.validationService = validationService;
    }

    @Override
    public Transaction purchase(
            Long userId,
            BigDecimal amount,
            String description,
            String category) {

        Instant now = Instant.now();

        String reason = validationService.validate(userId, amount, now);

        TransactionStatus status = TransactionStatus.APPROVED;

        if (reason != null) {
            status = TransactionStatus.REJECTED;
        }

        Transaction transaction = new Transaction(
                null,
                userId,
                amount,
                TransactionType.PURCHASE,
                now,
                description,
                category,
                status,
                null,
                reason
        );

        return repository.save(transaction);
    }

}
