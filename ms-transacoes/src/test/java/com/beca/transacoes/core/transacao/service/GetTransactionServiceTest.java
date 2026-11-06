package com.beca.transacoes.core.transacao.service;

import com.beca.transacoes.core.transacao.model.Transaction;
import com.beca.transacoes.core.transacao.port.output.TransactionRepositoryPort;

import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GetTransactionServiceTest {

    @Test
    void shouldThrowWhenTransactionNotFound() {

        TransactionRepositoryPort repository =
                mock(TransactionRepositoryPort.class);

        GetTransactionService service =
                new GetTransactionService(repository);

        when(repository.findById(999L)).thenReturn(null);

        assertThrows(NoSuchElementException.class,
                () -> service.getById(999L, 6L));
    }

    @Test
    void shouldReturnTransactionWhenFound() {

        TransactionRepositoryPort repository =
                mock(TransactionRepositoryPort.class);

        GetTransactionService service =
                new GetTransactionService(repository);

        Transaction transaction = mock(Transaction.class);

        when(transaction.getUserId()).thenReturn(6L);
        when(repository.findById(1L)).thenReturn(transaction);

        Transaction result = service.getById(1L, 6L);

        assertSame(transaction, result);
    }

    @Test
    void shouldRejectAccessFromAnotherUser() {

        TransactionRepositoryPort repository =
                mock(TransactionRepositoryPort.class);

        GetTransactionService service =
                new GetTransactionService(repository);

        Transaction transaction = mock(Transaction.class);

        when(transaction.getUserId()).thenReturn(7L);
        when(repository.findById(1L)).thenReturn(transaction);

        assertThrows(SecurityException.class,
                () -> service.getById(1L, 6L));
    }
}