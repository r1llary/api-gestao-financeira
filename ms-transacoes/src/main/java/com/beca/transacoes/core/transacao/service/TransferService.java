package com.beca.transacoes.core.transacao.service;

import com.beca.transacoes.core.transacao.model.Transaction;
import com.beca.transacoes.core.transacao.model.TransactionStatus;
import com.beca.transacoes.core.transacao.model.TransactionType;
import com.beca.transacoes.core.transacao.port.input.TransferUseCase;
import com.beca.transacoes.core.transacao.port.output.TransactionRepositoryPort;
import com.beca.transacoes.core.transacao.port.output.UserLookupPort;

import java.math.BigDecimal;
import java.time.Instant;


public class TransferService implements TransferUseCase {

    private final TransactionRepositoryPort repository;
    private final TransactionValidationService validationService;
    private final UserLookupPort userLookupPort;

    public TransferService(
            TransactionRepositoryPort repository,
            TransactionValidationService validationService,
            UserLookupPort userLookupPort) {

        this.repository = repository;
        this.validationService = validationService;
        this.userLookupPort = userLookupPort;
    }

    @Override
    public Transaction transfer(
            Long userId,
            Long destinationUserId,
            BigDecimal amount,
            String description,
            String category,
            String authorization) {

        Instant now = Instant.now();
        String reason = null;

        if (destinationUserId.equals(userId)) {
            reason = "Cannot transfer to yourself";

        } else if (!userLookupPort.userExists(destinationUserId, authorization)) {
            reason = "Destination user not found";

        } else {
            reason = validationService.validate(userId, amount, now);
        }

        TransactionStatus status = TransactionStatus.APPROVED;

        if (reason != null) {
            status = TransactionStatus.REJECTED;
        }

        Transaction transaction = new Transaction(
                null,
                userId,
                amount,
                TransactionType.TRANSFER,
                now,
                description,
                category,
                status,
                destinationUserId,
                reason
        );

        return repository.save(transaction);
    }

}