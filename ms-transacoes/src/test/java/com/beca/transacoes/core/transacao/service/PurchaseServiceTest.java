
package com.beca.transacoes.core.transacao.service;

import com.beca.transacoes.core.conta.model.Account;
import com.beca.transacoes.core.conta.port.output.AccountGatewayPort;
import com.beca.transacoes.core.transacao.model.Transaction;
import com.beca.transacoes.core.transacao.model.TransactionStatus;
import com.beca.transacoes.core.transacao.port.output.TransactionRepositoryPort;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class PurchaseServiceTest {

    private TransactionRepositoryPort repository;
    private TransactionValidationService validationService;
    private AccountGatewayPort accountGateway;
    private PurchaseService service;

    @BeforeEach
    void setUp() {

        // Arrange
        repository = mock(TransactionRepositoryPort.class);
        validationService = mock(TransactionValidationService.class);
        accountGateway = mock(AccountGatewayPort.class);

        service = new PurchaseService(
                repository,
                validationService,
                accountGateway
        );

        when(repository.save(any(Transaction.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    void shouldRejectPurchaseWhenTransactionLimitExceeded() {

        // Arrange
        when(validationService.validate(
                eq(6L),
                eq(new BigDecimal("250")),
                any(Instant.class)
        )).thenReturn("Transaction limit exceeded");

        // Act
        Transaction result = service.purchase(
                6L,
                new BigDecimal("250"),
                "Purchase limit test",
                "Food"
        );

        // Assert
        assertEquals(TransactionStatus.REJECTED, result.getStatus());
        assertEquals("Transaction limit exceeded",
                result.getRejectionReason());
        assertEquals("Food", result.getCategory());

        verify(repository).save(any(Transaction.class));
        verifyNoInteractions(accountGateway);
    }

    @Test
    void shouldApproveValidPurchase() {

        // Arrange
        Account account = mock(Account.class);

        when(accountGateway.findByUserId(6L))
                .thenReturn(account);

        when(account.balance())
                .thenReturn(new BigDecimal("1000"));

        // Act
        Transaction result = service.purchase(
                6L,
                new BigDecimal("50"),
                "Grocery shopping",
                "Food"
        );

        // Assert
        assertEquals(TransactionStatus.APPROVED, result.getStatus());
        assertEquals("Food", result.getCategory());

        verify(repository).save(any(Transaction.class));

        // Confirmar o desconto de 50 no saldo
        verify(accountGateway).updateBalance(
                6L,
                new BigDecimal("950")
        );
    }

    @Test
    void shouldNotUpdateBalanceWhenSaveFails() {

        // Arrange
        when(repository.save(any(Transaction.class)))
                .thenThrow(new RuntimeException("Database error"));

        // Act e Assert
        assertThrows(RuntimeException.class, () -> service.purchase(
                6L,
                new BigDecimal("50"),
                "Grocery shopping",
                "Food"
        ));

        // Nenhum saldo deve ser alterado
        verifyNoInteractions(accountGateway);
    }

    @Test
    void shouldThrowErrorWhenMockApiFails() {

        // Arrange
        Account account = mock(Account.class);

        when(accountGateway.findByUserId(6L))
                .thenReturn(account);

        when(account.balance())
                .thenReturn(new BigDecimal("1000"));

        doThrow(new RuntimeException("MockAPI unavailable"))
                .when(accountGateway)
                .updateBalance(6L, new BigDecimal("950"));

        // Act
        RuntimeException error = assertThrows(
                RuntimeException.class,
                () -> service.purchase(
                        6L,
                        new BigDecimal("50"),
                        "Grocery shopping",
                        "Food"
                )
        );

        // Assert
        assertEquals("MockAPI unavailable", error.getMessage());

        verify(repository).save(any(Transaction.class));

        verify(accountGateway).updateBalance(
                6L,
                new BigDecimal("950")
        );
    }
}
