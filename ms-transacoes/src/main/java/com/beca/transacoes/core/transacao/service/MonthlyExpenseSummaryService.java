
package com.beca.transacoes.core.transacao.service;

import com.beca.transacoes.core.transacao.model.MonthlyExpenseSummary;
import com.beca.transacoes.core.transacao.model.Transaction;
import com.beca.transacoes.core.transacao.model.TransactionStatus;
import com.beca.transacoes.core.transacao.model.TransactionType;
import com.beca.transacoes.core.transacao.port.input.MonthlyExpenseSummaryUseCase;
import com.beca.transacoes.core.transacao.port.input.ListTransactionsUseCase;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class MonthlyExpenseSummaryService implements MonthlyExpenseSummaryUseCase {

    private final ListTransactionsUseCase listUseCase;

    public MonthlyExpenseSummaryService(ListTransactionsUseCase listUseCase) {
        this.listUseCase = listUseCase;
    }

    @Override
    public MonthlyExpenseSummary summarize(
            Long userId,
            LocalDate startDate,
            LocalDate endDate) {

        ZoneId zone = ZoneId.of("America/Sao_Paulo");

        if (startDate == null && endDate == null) {
            LocalDate today = LocalDate.now(zone);

            startDate = today.withDayOfMonth(1);
            endDate = today.withDayOfMonth(today.lengthOfMonth());
        }

        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException(
                    "Start date and end date must be provided together"
            );
        }

        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException(
                    "Start date must not be after end date"
            );
        }

        List<Transaction> transactions = listUseCase.list(
                userId, startDate, endDate, null
        );

        Map<YearMonth, BigDecimal> totals = new TreeMap<>();
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

            LocalDate date = transaction.getOccurredAt()
                    .atZone(zone)
                    .toLocalDate();

            YearMonth month = YearMonth.from(date);

            BigDecimal currentTotal = totals.getOrDefault(
                    month, BigDecimal.ZERO
            );

            totals.put(
                    month,
                    currentTotal.add(transaction.getAmount())
            );

            totalAmount = totalAmount.add(transaction.getAmount());
        }

        List<MonthlyExpenseSummary.MonthTotal> months = new ArrayList<>();

        for (Map.Entry<YearMonth, BigDecimal> entry : totals.entrySet()) {
            months.add(new MonthlyExpenseSummary.MonthTotal(
                    entry.getKey(),
                    entry.getValue()
            ));
        }

        return new MonthlyExpenseSummary(
                startDate,
                endDate,
                totalAmount,
                months
        );
    }
}
