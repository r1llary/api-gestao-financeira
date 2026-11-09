
package com.beca.transacoes.core.transacao.service;

import com.beca.transacoes.core.transacao.model.ExpenseSummary;
import com.beca.transacoes.core.transacao.model.Transaction;
import com.beca.transacoes.core.transacao.model.TransactionStatus;
import com.beca.transacoes.core.transacao.model.TransactionType;
import com.beca.transacoes.core.transacao.port.input.ExpenseSummaryUseCase;
import com.beca.transacoes.core.transacao.port.input.ListTransactionsUseCase;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class ExpenseSummaryService implements ExpenseSummaryUseCase {

    private final ListTransactionsUseCase listUseCase;

    public ExpenseSummaryService(ListTransactionsUseCase listUseCase) {
        this.listUseCase = listUseCase;
    }

    @Override
    public ExpenseSummary summarize(
            Long userId,
            LocalDate startDate,
            LocalDate endDate) {

        if (startDate == null && endDate == null) {
            LocalDate today = LocalDate.now(
                    ZoneId.of("America/Sao_Paulo")
            );

            startDate = today.withDayOfMonth(1);
            endDate = today.withDayOfMonth(today.lengthOfMonth());
        }

        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException(
                    "Start date and end date must be provided together"
            );
        }

        List<Transaction> transactions = listUseCase.list(
                userId, startDate, endDate, null
        );

        Map<String, BigDecimal> totals = new TreeMap<>();
        BigDecimal totalAmount = BigDecimal.ZERO;

        for (Transaction transaction : transactions) {

            if (transaction.getStatus() != TransactionStatus.APPROVED) {
                continue;
            }

            if (transaction.getType() != TransactionType.WITHDRAWAL
                    && transaction.getType() != TransactionType.TRANSFER
                    && transaction.getType() != TransactionType.PURCHASE) {
                continue;
            }

            String category = transaction.getCategory();

            BigDecimal currentTotal = totals.getOrDefault(
                    category, BigDecimal.ZERO

            );

            totals.put(
                    category,
                    currentTotal.add(transaction.getAmount())
            );

            totalAmount = totalAmount.add(transaction.getAmount());
        }

        List<ExpenseSummary.CategoryTotal> categories = new ArrayList<>();

        for (Map.Entry<String, BigDecimal> entry : totals.entrySet()) {
            categories.add(new ExpenseSummary.CategoryTotal(
                    entry.getKey(),
                    entry.getValue()
            ));
        }

        return new ExpenseSummary(
                startDate,
                endDate,
                totalAmount,
                categories
        );
    }
}
