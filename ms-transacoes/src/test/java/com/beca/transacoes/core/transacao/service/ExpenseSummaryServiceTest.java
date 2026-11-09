
package com.beca.transacoes.core.transacao.service;

import com.beca.transacoes.core.transacao.model.ExpenseSummary;
import com.beca.transacoes.core.transacao.model.Transaction;
import com.beca.transacoes.core.transacao.model.TransactionStatus;
import com.beca.transacoes.core.transacao.model.TransactionType;
import com.beca.transacoes.core.transacao.port.input.ListTransactionsUseCase;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ExpenseSummaryServiceTest {

    @Test
    void shouldGroupExpensesByCategory() {

        ListTransactionsUseCase listUseCase =
                mock(ListTransactionsUseCase.class);

        ExpenseSummaryService service =
                new ExpenseSummaryService(listUseCase);

        LocalDate start = LocalDate.of(2026, 11, 1);
        LocalDate end = LocalDate.of(2026, 11, 30);

        when(listUseCase.list(6L, start, end, null))
                .thenReturn(List.of(
                        createTransaction(1L, "Food", "20.00",
                                TransactionType.PURCHASE, TransactionStatus.APPROVED),
                        createTransaction(2L, "Food", "30.00",
                                TransactionType.PURCHASE, TransactionStatus.APPROVED),
                        createTransaction(3L, "Transport", "40.00",
                                TransactionType.PURCHASE, TransactionStatus.APPROVED),
                        createTransaction(4L, "Shopping", "50.00",
                                TransactionType.PURCHASE, TransactionStatus.APPROVED),
                        createTransaction(5L, "Food", "10.00",
                                TransactionType.PURCHASE, TransactionStatus.APPROVED)
                ));

        ExpenseSummary result = service.summarize(6L, start, end);

        assertEquals(new BigDecimal("150.00"), result.totalAmount());
        assertEquals(3, result.categories().size());

        assertEquals("Food", result.categories().get(0).category());
        assertEquals(new BigDecimal("60.00"),
                result.categories().get(0).total());

        verify(listUseCase).list(6L, start, end, null);
    }

    @Test
    void shouldIgnoreDepositsAndRejectedTransactions() {

        ListTransactionsUseCase listUseCase =
                mock(ListTransactionsUseCase.class);

        ExpenseSummaryService service =
                new ExpenseSummaryService(listUseCase);

        LocalDate start = LocalDate.of(2026, 11, 1);
        LocalDate end = LocalDate.of(2026, 11, 30);

        when(listUseCase.list(6L, start, end, null))
                .thenReturn(List.of(
                        createTransaction(1L, "Deposit", "1000.00",
                                TransactionType.DEPOSIT, TransactionStatus.APPROVED),
                        createTransaction(2L, "Food", "80.00",
                                TransactionType.PURCHASE, TransactionStatus.REJECTED),
                        createTransaction(3L, "Cash", "40.00",
                                TransactionType.WITHDRAWAL, TransactionStatus.APPROVED),
                        createTransaction(4L, "Transfer", "60.00",
                                TransactionType.TRANSFER, TransactionStatus.APPROVED)
                ));

        ExpenseSummary result = service.summarize(6L, start, end);

        assertEquals(new BigDecimal("100.00"), result.totalAmount());
        assertEquals(2, result.categories().size());
    }

    private Transaction createTransaction(
            Long id,
            String category,
            String amount,
            TransactionType type,
            TransactionStatus status) {

        return new Transaction(
                id,
                6L,
                new BigDecimal(amount),
                type,
                Instant.parse("2026-11-05T12:00:00Z"),
                "Test transaction",
                category,
                status,
                type == TransactionType.TRANSFER ? 5L : null,
                status == TransactionStatus.REJECTED
                        ? "Rejected for test" : null
        );
    }


    @Test
    void shouldReturnZeroWhenNoExpensesExist() {

        // Arrange
        ListTransactionsUseCase listUseCase =
                mock(ListTransactionsUseCase.class);

        ExpenseSummaryService service =
                new ExpenseSummaryService(listUseCase);

        LocalDate start = LocalDate.of(2026, 11, 1);
        LocalDate end = LocalDate.of(2026, 11, 30);

        when(listUseCase.list(6L, start, end, null))
                .thenReturn(List.of());

        // Act
        ExpenseSummary result = service.summarize(6L, start, end);

        // Assert
        assertEquals(BigDecimal.ZERO, result.totalAmount());
        assertTrue(result.categories().isEmpty());

        verify(listUseCase).list(6L, start, end, null);
    }

}
