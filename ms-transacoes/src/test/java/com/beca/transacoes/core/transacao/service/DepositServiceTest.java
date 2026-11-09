
package com.beca.transacoes.core.transacao.service;

import com.beca.transacoes.core.conta.exception.AccountServiceUnavailableException;
import com.beca.transacoes.core.conta.model.Account;
import com.beca.transacoes.core.conta.port.output.AccountGatewayPort;
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

        AccountGatewayPort accountGateway =
                mock(AccountGatewayPort.class);

        DepositService service =
                new DepositService(repository, accountGateway);

        Account account = new Account(
                6L,
                new BigDecimal("1000.00"),
                new BigDecimal("500.00"),
                new BigDecimal("200.00")
        );

        when(accountGateway.findByUserId(6L))
                .thenReturn(account);

        Transaction savedTransaction = new Transaction(
                1L,
                6L,
                new BigDecimal("100.00"),
                TransactionType.DEPOSIT,
                Instant.now(),
                "Initial deposit",
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
                "Initial deposit",
                "Deposit"
        );

        Instant afterDeposit = Instant.now();

        verify(accountGateway).updateBalance(
                6L,
                new BigDecimal("1100.00")
        );

        ArgumentCaptor<Transaction> captor =
                ArgumentCaptor.forClass(Transaction.class);

        verify(repository).save(captor.capture());

        Transaction transaction = captor.getValue();

        assertNull(transaction.getId());
        assertEquals(6L, transaction.getUserId());
        assertEquals(new BigDecimal("100.00"), transaction.getAmount());
        assertEquals(TransactionType.DEPOSIT, transaction.getType());
        assertEquals(TransactionStatus.APPROVED, transaction.getStatus());
        assertEquals("Initial deposit", transaction.getDescription());
        assertEquals("Deposit", transaction.getCategory());
        assertNull(transaction.getDestinationUserId());
        assertNull(transaction.getRejectionReason());

        assertNotNull(transaction.getOccurredAt());
        assertFalse(transaction.getOccurredAt().isBefore(beforeDeposit));
        assertFalse(transaction.getOccurredAt().isAfter(afterDeposit));

        assertSame(savedTransaction, result);
    }

    @Test
    void shouldNotSaveDepositWhenBalanceUpdateFails() {

        TransactionRepositoryPort repository =
                mock(TransactionRepositoryPort.class);

        AccountGatewayPort accountGateway =
                mock(AccountGatewayPort.class);

        DepositService service =
                new DepositService(repository, accountGateway);

        Account account = new Account(
                6L,
                new BigDecimal("1000.00"),
                new BigDecimal("500.00"),
                new BigDecimal("200.00")
        );

        when(accountGateway.findByUserId(6L))
                .thenReturn(account);

        doThrow(new AccountServiceUnavailableException())
                .when(accountGateway)
                .updateBalance(6L, new BigDecimal("1100.00"));

        assertThrows(
                AccountServiceUnavailableException.class,
                () -> service.deposit(
                        6L,
                        new BigDecimal("100.00"),
                        "Initial deposit",
                        "Deposit"
                )
        );

        verify(repository, never()).save(any(Transaction.class));
    }
}
