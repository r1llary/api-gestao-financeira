
package com.beca.transacoes.core.transacao.service;

import com.beca.transacoes.core.transacao.model.Transaction;
import com.beca.transacoes.core.transacao.model.TransactionStatus;
import com.beca.transacoes.core.transacao.port.output.TransactionRepositoryPort;
import com.beca.transacoes.core.transacao.port.output.UserLookupPort;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class TransferServiceTest {

    @Test
    void shouldRejectWhenDestinationDoesNotExist() {

        TransactionRepositoryPort repository =
                mock(TransactionRepositoryPort.class);

        TransactionValidationService validationService =
                mock(TransactionValidationService.class);

        UserLookupPort userLookupPort =
                mock(UserLookupPort.class);

        when(userLookupPort.userExists(999L, "Basic test"))
                .thenReturn(false);

        when(repository.save(any(Transaction.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        TransferService service = new TransferService(
                repository,
                validationService,
                userLookupPort
        );

        Transaction result = service.transfer(
                6L,
                999L,
                new BigDecimal("50"),
                "Test transfer",
                "Transfer",
                "Basic test"
        );

        assertEquals(TransactionStatus.REJECTED, result.getStatus());
        assertEquals("Destination user not found",
                result.getRejectionReason());

        verify(repository).save(any(Transaction.class));
    }


    @Test
    void shouldApproveValidTransfer() {

        TransactionRepositoryPort repository =
                mock(TransactionRepositoryPort.class);

        TransactionValidationService validationService =
                mock(TransactionValidationService.class);

        UserLookupPort userLookupPort =
                mock(UserLookupPort.class);

        when(userLookupPort.userExists(5L, "Basic test"))
                .thenReturn(true);

        when(repository.save(any(Transaction.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        TransferService service = new TransferService(
                repository,
                validationService,
                userLookupPort
        );

        Transaction result = service.transfer(
                6L,
                5L,
                new BigDecimal("50"),
                "Test transfer",
                "Transfer",
                "Basic test"
        );

        assertEquals(TransactionStatus.APPROVED, result.getStatus());

        verify(repository).save(any(Transaction.class));
    }


    @Test
    void shouldRejectWhenTransactionLimitExceeded() {

        TransactionRepositoryPort repository =
                mock(TransactionRepositoryPort.class);

        TransactionValidationService validationService =
                mock(TransactionValidationService.class);

        UserLookupPort userLookupPort =
                mock(UserLookupPort.class);

        when(userLookupPort.userExists(5L, "Basic test"))
                .thenReturn(true);

        when(validationService.validate(
                eq(6L),
                eq(new BigDecimal("250")),
                any(Instant.class)
        )).thenReturn("Transaction limit exceeded");

        when(repository.save(any(Transaction.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        TransferService service = new TransferService(
                repository,
                validationService,
                userLookupPort
        );

        Transaction result = service.transfer(
                6L,
                5L,
                new BigDecimal("250"),
                "Transfer limit test",
                "Transfer",
                "Basic test"
        );

        assertEquals(TransactionStatus.REJECTED, result.getStatus());
        assertEquals("Transaction limit exceeded",
                result.getRejectionReason());

        verify(repository).save(any(Transaction.class));
    }


    @Test
    void shouldRejectTransferToSameUser() {

        TransactionRepositoryPort repository =
                mock(TransactionRepositoryPort.class);

        TransactionValidationService validationService =
                mock(TransactionValidationService.class);

        UserLookupPort userLookupPort =
                mock(UserLookupPort.class);

        when(repository.save(any(Transaction.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        TransferService service = new TransferService(
                repository,
                validationService,
                userLookupPort
        );

        Transaction result = service.transfer(
                6L,
                6L,
                new BigDecimal("50"),
                "Self transfer",
                "Transfer",
                "Basic test"
        );

        assertEquals(TransactionStatus.REJECTED, result.getStatus());
        assertEquals("Cannot transfer to yourself",
                result.getRejectionReason());

        verify(repository).save(any(Transaction.class));
        verifyNoInteractions(userLookupPort, validationService);
    }


}
