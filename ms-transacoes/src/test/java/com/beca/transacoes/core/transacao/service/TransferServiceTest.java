
package com.beca.transacoes.core.transacao.service;

import com.beca.transacoes.core.conta.model.Account;
import com.beca.transacoes.core.conta.port.output.AccountGatewayPort;
import com.beca.transacoes.core.transacao.model.Transaction;
import com.beca.transacoes.core.transacao.model.TransactionStatus;
import com.beca.transacoes.core.transacao.port.output.TransactionRepositoryPort;
import com.beca.transacoes.core.transacao.port.output.UserLookupPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class TransferServiceTest {

    private TransactionRepositoryPort repository;
    private TransactionValidationService validationService;
    private UserLookupPort userLookupPort;
    private AccountGatewayPort accountGateway;
    private TransferService service;

    @BeforeEach
    void setUp() {

        // Arrange
        repository = mock(TransactionRepositoryPort.class);
        validationService = mock(TransactionValidationService.class);
        userLookupPort = mock(UserLookupPort.class);
        accountGateway = mock(AccountGatewayPort.class);

        service = new TransferService(
                repository,
                validationService,
                userLookupPort,
                accountGateway
        );

        when(repository.save(any(Transaction.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    void shouldRejectWhenDestinationDoesNotExist() {

        // Arrange
        when(userLookupPort.userExists(999L, "Basic test"))
                .thenReturn(false);

        // Act
        Transaction result = service.transfer(
                6L,
                999L,
                new BigDecimal("50"),
                "Test transfer",
                "Transfer",
                "Basic test"
        );

        // Assert
        assertEquals(TransactionStatus.REJECTED, result.getStatus());
        assertEquals("Destination user not found",
                result.getRejectionReason());

        verify(repository).save(any(Transaction.class));
        verifyNoInteractions(accountGateway);
    }

    @Test
    void shouldApproveValidTransfer() {

        // Arrange
        when(userLookupPort.userExists(5L, "Basic test"))
                .thenReturn(true);

        Account originAccount = mock(Account.class);
        Account destinationAccount = mock(Account.class);

        when(originAccount.balance())
                .thenReturn(new BigDecimal("1000"));

        when(destinationAccount.balance())
                .thenReturn(new BigDecimal("500"));

        when(accountGateway.findByUserId(6L))
                .thenReturn(originAccount);

        when(accountGateway.findByUserId(5L))
                .thenReturn(destinationAccount);

        // Act
        Transaction result = service.transfer(
                6L,
                5L,
                new BigDecimal("50"),
                "Test transfer",
                "Transfer",
                "Basic test"
        );

        // Assert
        assertEquals(TransactionStatus.APPROVED, result.getStatus());

        verify(repository).save(any(Transaction.class));

        // Verificar o desconto na conta de origem
        verify(accountGateway).updateBalance(
                6L,
                new BigDecimal("950")
        );

        // Verificar o crédito na conta de destino
        verify(accountGateway).updateBalance(
                5L,
                new BigDecimal("550")
        );
    }

    @Test
    void shouldRejectWhenTransactionLimitExceeded() {

        // Arrange
        when(userLookupPort.userExists(5L, "Basic test"))
                .thenReturn(true);

        when(validationService.validate(
                eq(6L),
                eq(new BigDecimal("250")),
                any(Instant.class)
        )).thenReturn("Transaction limit exceeded");

        // Act
        Transaction result = service.transfer(
                6L,
                5L,
                new BigDecimal("250"),
                "Transfer limit test",
                "Transfer",
                "Basic test"
        );

        // Assert
        assertEquals(TransactionStatus.REJECTED, result.getStatus());
        assertEquals("Transaction limit exceeded",
                result.getRejectionReason());

        verify(repository).save(any(Transaction.class));
        verifyNoInteractions(accountGateway);
    }

    @Test
    void shouldRejectTransferToSameUser() {

        // Arrange
        Long userId = 6L;

        // Act
        Transaction result = service.transfer(
                userId,
                userId,
                new BigDecimal("50"),
                "Self transfer",
                "Transfer",
                "Basic test"
        );

        // Assert
        assertEquals(TransactionStatus.REJECTED, result.getStatus());
        assertEquals("Cannot transfer to yourself",
                result.getRejectionReason());

        verify(repository).save(any(Transaction.class));
        verifyNoInteractions(userLookupPort, validationService, accountGateway);
    }

    @Test
    void shouldNotUpdateBalanceWhenSaveFails() {

        // Arrange
        when(userLookupPort.userExists(5L, "Basic test"))
                .thenReturn(true);

        when(repository.save(any(Transaction.class)))
                .thenThrow(new RuntimeException("Database error"));

        // Act e Assert
        assertThrows(RuntimeException.class, () -> service.transfer(
                6L,
                5L,
                new BigDecimal("50"),
                "Test transfer",
                "Transfer",
                "Basic test"
        ));

        // Não deve movimentar dinheiro se não conseguir salvar
        verifyNoInteractions(accountGateway);
    }


    @Test
    void shouldRestoreOriginBalanceWhenDestinationUpdateFails() {

        // Arrange
        when(userLookupPort.userExists(5L, "Basic test"))
                .thenReturn(true);

        Account originAccount = mock(Account.class);
        Account destinationAccount = mock(Account.class);

        when(originAccount.balance())
                .thenReturn(new BigDecimal("1000"));

        when(destinationAccount.balance())
                .thenReturn(new BigDecimal("500"));

        when(accountGateway.findByUserId(6L))
                .thenReturn(originAccount);

        when(accountGateway.findByUserId(5L))
                .thenReturn(destinationAccount);

        // Simular erro ao creditar a conta de destino
        doThrow(new RuntimeException("MockAPI error"))
                .when(accountGateway)
                .updateBalance(5L, new BigDecimal("550"));

        // Act
        assertThrows(RuntimeException.class, () -> service.transfer(
                6L,
                5L,
                new BigDecimal("50"),
                "Test transfer",
                "Transfer",
                "Basic test"
        ));

        // Assert
        // Verificar que a origem foi debitada
        verify(accountGateway).updateBalance(
                6L, new BigDecimal("950")
        );

        // Verificar que o sistema tentou devolver o dinheiro
        verify(accountGateway).updateBalance(
                6L, new BigDecimal("1000")
        );
    }

    @Test
    void shouldPreserveErrorsWhenRefundFails() {

        // Arrange
        when(userLookupPort.userExists(5L, "Basic test"))
                .thenReturn(true);

        Account originAccount = mock(Account.class);
        Account destinationAccount = mock(Account.class);

        when(originAccount.balance())
                .thenReturn(new BigDecimal("1000"));

        when(destinationAccount.balance())
                .thenReturn(new BigDecimal("500"));

        when(accountGateway.findByUserId(6L))
                .thenReturn(originAccount);

        when(accountGateway.findByUserId(5L))
                .thenReturn(destinationAccount);

        // Simular falha no crédito
        doThrow(new RuntimeException("Credit failed"))
                .when(accountGateway)
                .updateBalance(5L, new BigDecimal("550"));

        // Simular falha na devolução
        doThrow(new RuntimeException("Refund failed"))
                .when(accountGateway)
                .updateBalance(6L, new BigDecimal("1000"));

        // Act
        RuntimeException error = assertThrows(
                RuntimeException.class,
                () -> service.transfer(
                        6L,
                        5L,
                        new BigDecimal("50"),
                        "Test transfer",
                        "Transfer",
                        "Basic test"
                )
        );

        // Assert
        assertEquals("Credit failed", error.getMessage());

        assertEquals(1, error.getSuppressed().length);
        assertEquals(
                "Refund failed",
                error.getSuppressed()[0].getMessage()
        );

        verify(accountGateway).updateBalance(
                6L, new BigDecimal("1000")
        );
    }


}
