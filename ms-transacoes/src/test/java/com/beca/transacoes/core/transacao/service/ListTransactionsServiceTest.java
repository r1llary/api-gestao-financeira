
package com.beca.transacoes.core.transacao.service;

import com.beca.transacoes.core.transacao.model.Transaction;
import com.beca.transacoes.core.transacao.model.TransactionStatus;
import com.beca.transacoes.core.transacao.model.TransactionType;
import com.beca.transacoes.core.transacao.port.output.TransactionRepositoryPort;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class ListTransactionsServiceTest {

    @Test
    void shouldRejectInvalidDateRange() {

        TransactionRepositoryPort repository =
                mock(TransactionRepositoryPort.class);

        ListTransactionsService service =
                new ListTransactionsService(repository);

        LocalDate startDate = LocalDate.of(2026, 11, 8);
        LocalDate endDate = LocalDate.of(2026, 11, 7);

        assertThrows(IllegalArgumentException.class, () ->
                service.list(6L, startDate, endDate, null)
        );

        verifyNoInteractions(repository);
    }


    @Test
    void shouldFilterTransactionsByType() {

        TransactionRepositoryPort repository =
                mock(TransactionRepositoryPort.class);

        Transaction purchase = new Transaction(
                18L,
                6L,
                new BigDecimal("50"),
                TransactionType.PURCHASE,
                Instant.parse("2026-11-07T17:34:16Z"),
                "Grocery shopping",
                "Food",
                TransactionStatus.APPROVED,
                null,
                null
        );

        Transaction transfer = new Transaction(
                15L,
                6L,
                new BigDecimal("50"),
                TransactionType.TRANSFER,
                Instant.parse("2026-11-07T17:08:51Z"),
                "Transfer to another user",
                "Transfer",
                TransactionStatus.APPROVED,
                5L,
                null
        );

        when(repository.findByUserId(6L))
                .thenReturn(List.of(purchase, transfer));

        ListTransactionsService service =
                new ListTransactionsService(repository);

        List<Transaction> result = service.list(
                6L,
                null,
                null,
                TransactionType.PURCHASE
        );

        assertEquals(1, result.size());
        assertEquals(TransactionType.PURCHASE, result.get(0).getType());
    }


    @Test
    void shouldFilterTransactionsByPeriod() {

        TransactionRepositoryPort repository =
                mock(TransactionRepositoryPort.class);

        Transaction purchase = new Transaction(
                18L,
                6L,
                new BigDecimal("50"),
                TransactionType.PURCHASE,
                Instant.parse("2026-11-07T17:34:16Z"),
                "Grocery shopping",
                "Food",
                TransactionStatus.APPROVED,
                null,
                null
        );

        Transaction withdrawal = new Transaction(
                12L,
                6L,
                new BigDecimal("200"),
                TransactionType.WITHDRAWAL,
                Instant.parse("2026-11-06T21:25:28Z"),
                "Cash withdrawal",
                "Cash",
                TransactionStatus.APPROVED,
                null,
                null
        );

        when(repository.findByUserId(6L))
                .thenReturn(List.of(purchase, withdrawal));

        ListTransactionsService service =
                new ListTransactionsService(repository);

        List<Transaction> result = service.list(
                6L,
                LocalDate.of(2026, 11, 7),
                LocalDate.of(2026, 11, 7),
                null
        );

        assertEquals(1, result.size());
        assertEquals(18L, result.get(0).getId());
    }

    @Test
    void shouldReturnEmptyListWhenNoTransactionsExist() {

        TransactionRepositoryPort repository =
                mock(TransactionRepositoryPort.class);

        when(repository.findByUserId(6L))
                .thenReturn(List.of());

        ListTransactionsService service =
                new ListTransactionsService(repository);

        List<Transaction> result = service.list(
                6L,
                null,
                null,
                null
        );

        assertEquals(0, result.size());

        verify(repository).findByUserId(6L);
    }

}
