
package com.beca.transacoes.core.transacao.service;

import com.beca.transacoes.core.transacao.model.DailyExpenseSummary;
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

class DailyExpenseSummaryServiceTest {

    @Test
    void shouldGroupExpensesByDay() {

        // Arrange
        ListTransactionsUseCase listUseCase =
                mock(ListTransactionsUseCase.class);

        DailyExpenseSummaryService service =
                new DailyExpenseSummaryService(listUseCase);

        LocalDate start = LocalDate.of(2026, 11, 1);
        LocalDate end = LocalDate.of(2026, 11, 30);

        when(listUseCase.list(6L, start, end, null))
                .thenReturn(List.of(
                        createTransaction(1L, "20.00",
                                "2026-11-05T12:00:00Z"),
                        createTransaction(2L, "30.00",
                                "2026-11-05T20:00:00Z"),
                        createTransaction(3L, "40.00",
                                "2026-11-06T12:00:00Z")
                ));

        // Act
        DailyExpenseSummary result =
                service.summarize(6L, start, end);

        // Assert
        assertEquals(new BigDecimal("90.00"),
                result.totalAmount());

        assertEquals(2, result.days().size());

        assertEquals(LocalDate.of(2026, 11, 5),
                result.days().get(0).date());

        assertEquals(new BigDecimal("50.00"),
                result.days().get(0).amount());

        assertEquals(LocalDate.of(2026, 11, 6),
                result.days().get(1).date());

        assertEquals(new BigDecimal("40.00"),
                result.days().get(1).amount());

        verify(listUseCase).list(6L, start, end, null);
    }

    private Transaction createTransaction(
            Long id,
            String amount,
            String occurredAt) {

        return new Transaction(
                id,
                6L,
                new BigDecimal(amount),
                TransactionType.PURCHASE,
                Instant.parse(occurredAt),
                "Test purchase",
                "Food",
                TransactionStatus.APPROVED,
                null,
                null
        );
    }


    @Test
    void shouldIgnoreDepositsAndRejectedTransactions() {

        // Arrange
        ListTransactionsUseCase listUseCase =
                mock(ListTransactionsUseCase.class);

        DailyExpenseSummaryService service =
                new DailyExpenseSummaryService(listUseCase);

        LocalDate start = LocalDate.of(2026, 11, 1);
        LocalDate end = LocalDate.of(2026, 11, 30);

        Transaction deposit = new Transaction(
                1L, 6L, new BigDecimal("1000.00"),
                TransactionType.DEPOSIT,
                Instant.parse("2026-11-05T12:00:00Z"),
                "Deposit", "Deposit",
                TransactionStatus.APPROVED,
                null, null
        );

        Transaction rejectedPurchase = new Transaction(
                2L, 6L, new BigDecimal("80.00"),
                TransactionType.PURCHASE,
                Instant.parse("2026-11-05T12:00:00Z"),
                "Rejected purchase", "Food",
                TransactionStatus.REJECTED,
                null, "Transaction limit exceeded"
        );

        Transaction approvedPurchase = createTransaction(
                3L, "40.00", "2026-11-05T12:00:00Z"
        );

        when(listUseCase.list(6L, start, end, null))
                .thenReturn(List.of(
                        deposit,
                        rejectedPurchase,
                        approvedPurchase
                ));

        // Act
        DailyExpenseSummary result =
                service.summarize(6L, start, end);

        // Assert
        assertEquals(new BigDecimal("40.00"), result.totalAmount());
        assertEquals(1, result.days().size());

        assertEquals(LocalDate.of(2026, 11, 5),
                result.days().get(0).date());

        assertEquals(new BigDecimal("40.00"),
                result.days().get(0).amount());

        verify(listUseCase).list(6L, start, end, null);
    }


    @Test
    void shouldUseSaoPauloTimezone() {

        // Arrange
        ListTransactionsUseCase listUseCase =
                mock(ListTransactionsUseCase.class);

        DailyExpenseSummaryService service =
                new DailyExpenseSummaryService(listUseCase);

        LocalDate start = LocalDate.of(2026, 11, 1);
        LocalDate end = LocalDate.of(2026, 11, 30);

        when(listUseCase.list(6L, start, end, null))
                .thenReturn(List.of(
                        createTransaction(
                                1L, "25.00",
                                "2026-11-06T01:30:00Z"
                        ),
                        createTransaction(
                                2L, "35.00",
                                "2026-11-06T03:30:00Z"
                        )
                ));

        // Act
        DailyExpenseSummary result =
                service.summarize(6L, start, end);

        // Assert
        assertEquals(new BigDecimal("60.00"), result.totalAmount());
        assertEquals(2, result.days().size());

        assertEquals(LocalDate.of(2026, 11, 5),
                result.days().get(0).date());

        assertEquals(new BigDecimal("25.00"),
                result.days().get(0).amount());

        assertEquals(LocalDate.of(2026, 11, 6),
                result.days().get(1).date());

        assertEquals(new BigDecimal("35.00"),
                result.days().get(1).amount());

        verify(listUseCase).list(6L, start, end, null);
    }

    @Test
    void shouldReturnZeroWhenNoExpensesExist() {

        // Arrange
        ListTransactionsUseCase listUseCase =
                mock(ListTransactionsUseCase.class);

        DailyExpenseSummaryService service =
                new DailyExpenseSummaryService(listUseCase);

        LocalDate start = LocalDate.of(2026, 11, 1);
        LocalDate end = LocalDate.of(2026, 11, 30);

        when(listUseCase.list(6L, start, end, null))
                .thenReturn(List.of());

        // Act
        DailyExpenseSummary result =
                service.summarize(6L, start, end);

        // Assert
        assertEquals(BigDecimal.ZERO, result.totalAmount());
        assertTrue(result.days().isEmpty());

        verify(listUseCase).list(6L, start, end, null);
    }


}
