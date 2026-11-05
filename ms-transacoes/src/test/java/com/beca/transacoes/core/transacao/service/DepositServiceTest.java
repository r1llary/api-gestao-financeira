package com.beca.transacoes.core.transacao.service;

import com.beca.transacoes.core.transacao.model.Transaction;
import com.beca.transacoes.core.transacao.model.TransactionStatus;
import com.beca.transacoes.core.transacao.model.TransactionType;
import com.beca.transacoes.core.transacao.port.output.TransactionRepositoryPort;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.math.BigDecimal;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class DepositServiceTest {

    @Test
    void shouldSaveApprovedDepositForUser() {
        TransactionRepositoryPort repository =
                mock(TransactionRepositoryPort.class);

        DepositService service = new DepositService(repository);

        Transaction savedTransaction = new Transaction(
                1L,
                6L,
                new BigDecimal("100.00"),
                TransactionType.DEPOSIT,
                Instant.now(),
                "Depósito inicial",
                "Deposit",
                TransactionStatus.APPROVED,
                null,
                null
        );

        when(repository.save(any(Transaction.class)))
                .thenReturn(savedTransaction);

        Instant beforeDeposit = Instant.now();

        Transaction result = service.deposit(
                6L,
                new BigDecimal("100.00"),
                "Depósito inicial",
                "Deposit"
        );

        Instant afterDeposit = Instant.now();

        ArgumentCaptor<Transaction> captor =
                ArgumentCaptor.forClass(Transaction.class);

        verify(repository).save(captor.capture());

        Transaction transaction = captor.getValue();

        assertNull(transaction.getId());
        assertEquals(Long.valueOf(6L), transaction.getUserId());
        assertEquals(new BigDecimal("100.00"), transaction.getAmount());
        assertEquals(TransactionType.DEPOSIT, transaction.getType());
        assertEquals(TransactionStatus.APPROVED, transaction.getStatus());
        assertEquals("Depósito inicial", transaction.getDescription());
        assertEquals("Deposit", transaction.getCategory());
        assertNull(transaction.getDestinationUserId());
        assertNull(transaction.getRejectionReason());

        assertNotNull(transaction.getOccurredAt());
        assertFalse(transaction.getOccurredAt().isBefore(beforeDeposit));
        assertFalse(transaction.getOccurredAt().isAfter(afterDeposit));

        assertSame(savedTransaction, result);
    }
}