package com.beca.transacoes.core.transacao.service;

import com.beca.transacoes.core.conta.exception.AccountServiceUnavailableException;
import com.beca.transacoes.core.conta.model.Account;
import com.beca.transacoes.core.conta.port.input.AccountUseCase;
import com.beca.transacoes.core.transacao.model.Transaction;
import com.beca.transacoes.core.transacao.model.TransactionType;
import com.beca.transacoes.core.transacao.port.output.TransactionRepositoryPort;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class TransactionValidationServiceTest {

    @Test
    void shouldRejectInsufficientBalance() {

        AccountUseCase accountUseCase = mock(AccountUseCase.class);
        TransactionRepositoryPort repository = mock(TransactionRepositoryPort.class);

        Account account = new Account(
                6L,
                new BigDecimal("100"),
                new BigDecimal("500"),
                new BigDecimal("200")
        );

        when(accountUseCase.findByUserId(6L)).thenReturn(account);

        when(repository.findApprovedByUserIdAndPeriod(
                eq(6L), any(Instant.class), any(Instant.class)
        )).thenReturn(List.of());

        TransactionValidationService service =
                new TransactionValidationService(accountUseCase, repository);

        String result = service.validate(
                6L,
                new BigDecimal("150"),
                Instant.now()
        );

        assertEquals("Insufficient balance", result);
    }

    @Test
    void shouldAllowTransactionWithinLimits() {

        AccountUseCase accountUseCase = mock(AccountUseCase.class);
        TransactionRepositoryPort repository = mock(TransactionRepositoryPort.class);

        Account account = new Account(
                6L,
                new BigDecimal("1000"),
                new BigDecimal("500"),
                new BigDecimal("200")
        );

        when(accountUseCase.findByUserId(6L)).thenReturn(account);

        when(repository.findApprovedByUserIdAndPeriod(
                eq(6L), any(Instant.class), any(Instant.class)
        )).thenReturn(List.of());

        TransactionValidationService service =
                new TransactionValidationService(accountUseCase, repository);

        String result = service.validate(
                6L,
                new BigDecimal("100"),
                Instant.now()
        );

        assertNull(result);
    }

    @Test
    void shouldRejectWhenAccountServiceIsUnavailable() {

        AccountUseCase accountUseCase = mock(AccountUseCase.class);
        TransactionRepositoryPort repository =
                mock(TransactionRepositoryPort.class);

        when(accountUseCase.findByUserId(6L))
                .thenThrow(new AccountServiceUnavailableException());

        TransactionValidationService service =
                new TransactionValidationService(accountUseCase, repository);

        assertThrows(AccountServiceUnavailableException.class, () ->
                service.validate(
                        6L,
                        new BigDecimal("100"),
                        Instant.now()
                )
        );
    }

    @Test
    void shouldRejectWhenPreviousSpendingExceedsDailyLimit() {

        AccountUseCase accountUseCase = mock(AccountUseCase.class);
        TransactionRepositoryPort repository =
                mock(TransactionRepositoryPort.class);

        Account account = new Account(
                6L,
                new BigDecimal("1000"),
                new BigDecimal("200"),
                new BigDecimal("200")
        );

        Transaction purchase = mock(Transaction.class);

        when(purchase.getType()).thenReturn(TransactionType.PURCHASE);
        when(purchase.getAmount()).thenReturn(new BigDecimal("150"));

        when(accountUseCase.findByUserId(6L)).thenReturn(account);

        when(repository.findApprovedByUserIdAndPeriod(
                eq(6L),
                any(Instant.class),
                any(Instant.class)
        )).thenReturn(List.of(purchase));

        TransactionValidationService service =
                new TransactionValidationService(accountUseCase, repository);

        String result = service.validate(
                6L,
                new BigDecimal("100"),
                Instant.now()
        );

        assertEquals("Daily limit exceeded", result);
    }
}