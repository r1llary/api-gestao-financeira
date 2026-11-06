package com.beca.transacoes.core.transacao.service;

import com.beca.transacoes.core.transacao.model.Transaction;
import com.beca.transacoes.core.transacao.model.TransactionStatus;
import com.beca.transacoes.core.transacao.model.TransactionType;
import com.beca.transacoes.core.transacao.port.output.TransactionRepositoryPort;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class WithdrawalServiceTest {

    @Test
    void shouldApproveWithdrawalWithSufficientBalance() {

        TransactionRepositoryPort repository =
                mock(TransactionRepositoryPort.class);

        TransactionValidationService validationService =
                mock(TransactionValidationService.class);

        when(repository.save(any(Transaction.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        WithdrawalService service =
                new WithdrawalService(repository, validationService);

        Transaction result = service.withdraw(
                6L,
                new BigDecimal("100"),
                "ATM withdrawal",
                "Cash"
        );

        assertEquals(TransactionType.WITHDRAWAL, result.getType());
        assertEquals(TransactionStatus.APPROVED, result.getStatus());
        assertNull(result.getRejectionReason());

        verify(repository).save(any(Transaction.class));
    }

    @Test
    void shouldRejectWithdrawalWithInsufficientBalance() {

        TransactionRepositoryPort repository =
                mock(TransactionRepositoryPort.class);

        TransactionValidationService validationService =
                mock(TransactionValidationService.class);

        when(validationService.validate(
                eq(6L),
                any(BigDecimal.class),
                any(Instant.class)
        )).thenReturn("Insufficient balance");

        when(repository.save(any(Transaction.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        WithdrawalService service =
                new WithdrawalService(repository, validationService);

        Transaction result = service.withdraw(
                6L,
                new BigDecimal("1500"),
                "ATM withdrawal",
                "Cash"
        );

        assertEquals(TransactionType.WITHDRAWAL, result.getType());
        assertEquals(TransactionStatus.REJECTED, result.getStatus());
        assertEquals("Insufficient balance", result.getRejectionReason());

        verify(repository).save(any(Transaction.class));
    }

    @Test
    void shouldNotSaveWithdrawalWhenAccountServiceUnavailable() {

        TransactionRepositoryPort repository =
                mock(TransactionRepositoryPort.class);

        TransactionValidationService validationService =
                mock(TransactionValidationService.class);

        when(validationService.validate(
                eq(6L),
                any(BigDecimal.class),
                any(Instant.class)
        )).thenThrow(new RuntimeException("Account service unavailable"));

        WithdrawalService service =
                new WithdrawalService(repository, validationService);

        assertThrows(RuntimeException.class, () ->
                service.withdraw(
                        6L,
                        new BigDecimal("100"),
                        "ATM withdrawal",
                        "Cash"
                )
        );

        verify(repository, never()).save(any(Transaction.class));
    }
}