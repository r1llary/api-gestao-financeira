
package com.beca.transacoes.core.transacao.service;

import com.beca.transacoes.core.conta.model.Account;
import com.beca.transacoes.core.conta.port.output.AccountGatewayPort;
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

        // Arrange
        TransactionRepositoryPort repository =
                mock(TransactionRepositoryPort.class);

        TransactionValidationService validationService =
                mock(TransactionValidationService.class);

        AccountGatewayPort accountGateway =
                mock(AccountGatewayPort.class);

        Account account = mock(Account.class);

        when(accountGateway.findByUserId(6L))
                .thenReturn(account);

        when(account.balance())
                .thenReturn(new BigDecimal("1000"));

        when(repository.save(any(Transaction.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        WithdrawalService service = new WithdrawalService(
                repository,
                validationService,
                accountGateway
        );

        // Act
        Transaction result = service.withdraw(
                6L,
                new BigDecimal("100"),
                "ATM withdrawal",
                "Cash"
        );

        // Assert
        assertEquals(TransactionType.WITHDRAWAL, result.getType());
        assertEquals(TransactionStatus.APPROVED, result.getStatus());
        assertNull(result.getRejectionReason());

        verify(repository).save(any(Transaction.class));

        verify(accountGateway).updateBalance(
                6L,
                new BigDecimal("900")
        );
    }

    @Test
    void shouldRejectWithdrawalWithInsufficientBalance() {

        // Arrange
        TransactionRepositoryPort repository =
                mock(TransactionRepositoryPort.class);

        TransactionValidationService validationService =
                mock(TransactionValidationService.class);

        AccountGatewayPort accountGateway =
                mock(AccountGatewayPort.class);

        when(validationService.validate(
                eq(6L),
                any(BigDecimal.class),
                any(Instant.class)
        )).thenReturn("Insufficient balance");

        when(repository.save(any(Transaction.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        WithdrawalService service = new WithdrawalService(
                repository,
                validationService,
                accountGateway
        );

        // Act
        Transaction result = service.withdraw(
                6L,
                new BigDecimal("1500"),
                "ATM withdrawal",
                "Cash"
        );

        // Assert
        assertEquals(TransactionType.WITHDRAWAL, result.getType());
        assertEquals(TransactionStatus.REJECTED, result.getStatus());
        assertEquals(
                "Insufficient balance",
                result.getRejectionReason()
        );

        verify(repository).save(any(Transaction.class));
        verifyNoInteractions(accountGateway);
    }

    @Test
    void shouldNotSaveWithdrawalWhenAccountServiceUnavailable() {

        // Arrange
        TransactionRepositoryPort repository =
                mock(TransactionRepositoryPort.class);

        TransactionValidationService validationService =
                mock(TransactionValidationService.class);

        AccountGatewayPort accountGateway =
                mock(AccountGatewayPort.class);

        when(validationService.validate(
                eq(6L),
                any(BigDecimal.class),
                any(Instant.class)
        )).thenThrow(new RuntimeException("Account service unavailable"));

        WithdrawalService service = new WithdrawalService(
                repository,
                validationService,
                accountGateway
        );

        // Act and Assert
        assertThrows(RuntimeException.class, () ->
                service.withdraw(
                        6L,
                        new BigDecimal("100"),
                        "ATM withdrawal",
                        "Cash"
                )
        );

        verify(repository, never()).save(any(Transaction.class));
        verifyNoInteractions(accountGateway);
    }

    @Test
    void shouldNotUpdateBalanceWhenSaveFails() {

        // Arrange
        TransactionRepositoryPort repository =
                mock(TransactionRepositoryPort.class);

        TransactionValidationService validationService =
                mock(TransactionValidationService.class);

        AccountGatewayPort accountGateway =
                mock(AccountGatewayPort.class);

        when(repository.save(any(Transaction.class)))
                .thenThrow(new RuntimeException("Database error"));

        WithdrawalService service = new WithdrawalService(
                repository,
                validationService,
                accountGateway
        );

        // Act and Assert
        assertThrows(RuntimeException.class, () ->
                service.withdraw(
                        6L,
                        new BigDecimal("100"),
                        "ATM withdrawal",
                        "Cash"
                )
        );

        verify(repository).save(any(Transaction.class));
        verifyNoInteractions(accountGateway);
    }


    @Test
    void shouldThrowExceptionWhenBalanceUpdateFails() {

        // Arrange
        TransactionRepositoryPort repository =
                mock(TransactionRepositoryPort.class);

        TransactionValidationService validationService =
                mock(TransactionValidationService.class);

        AccountGatewayPort accountGateway =
                mock(AccountGatewayPort.class);

        Account account = mock(Account.class);

        when(accountGateway.findByUserId(6L))
                .thenReturn(account);

        when(account.balance())
                .thenReturn(new BigDecimal("1000"));

        when(repository.save(any(Transaction.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        doThrow(new RuntimeException("MockAPI unavailable"))
                .when(accountGateway)
                .updateBalance(6L, new BigDecimal("900"));

        WithdrawalService service = new WithdrawalService(
                repository,
                validationService,
                accountGateway
        );

        // Act and Assert
        assertThrows(RuntimeException.class, () ->
                service.withdraw(
                        6L,
                        new BigDecimal("100"),
                        "ATM withdrawal",
                        "Cash"
                )
        );

        verify(repository).save(any(Transaction.class));

        verify(accountGateway).updateBalance(
                6L,
                new BigDecimal("900")
        );
    }

}
