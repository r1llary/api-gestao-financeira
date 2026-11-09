
package com.beca.transacoes.core.transacao.service;

import com.beca.transacoes.core.transacao.model.Transaction;
import com.beca.transacoes.core.transacao.model.TransactionType;
import com.beca.transacoes.core.transacao.port.input.ListTransactionsUseCase;
import com.beca.transacoes.core.transacao.port.output.TransactionRepositoryPort;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

public class ListTransactionsService implements ListTransactionsUseCase {

    private final TransactionRepositoryPort repository;

    public ListTransactionsService(TransactionRepositoryPort repository) {
        this.repository = repository;
    }

    @Override
    public List<Transaction> list(
            Long userId,
            LocalDate startDate,
            LocalDate endDate,
            TransactionType type
    ) {

        if (startDate != null && endDate != null
                && startDate.isAfter(endDate)) {
            throw new IllegalArgumentException(
                    "Start date must not be after end date"
            );
        }

        List<Transaction> transactions = repository.findByUserId(userId);
        List<Transaction> result = new ArrayList<>();

        for (Transaction transaction : transactions) {

            LocalDate date = transaction.getOccurredAt()
                    .atZone(ZoneId.of("America/Sao_Paulo"))
                    .toLocalDate();

            if (startDate != null && date.isBefore(startDate)) {
                continue;
            }

            if (endDate != null && date.isAfter(endDate)) {
                continue;
            }

            if (type != null && transaction.getType() != type) {
                continue;
            }

            result.add(transaction);
        }

        return result;
    }
}
