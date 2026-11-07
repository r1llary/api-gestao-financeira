
package com.beca.transacoes.core.transacao.service;

import com.beca.transacoes.core.transacao.model.Transaction;
import com.beca.transacoes.core.transacao.model.TransactionStatus;
import com.beca.transacoes.core.transacao.port.output.TransactionRepositoryPort;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class PurchaseServiceTest {

    @Test
    void shouldRejectPurchaseWhenTransactionLimitExceeded() {

        TransactionRepositoryPort repository =
                mock(TransactionRepositoryPort.class);

        TransactionValidationService validationService =
                mock(TransactionValidationService.class);

        when(validationService.validate(
                eq(6L),
                eq(new BigDecimal("250")),
                any(Instant.class)
        )).thenReturn("Transaction limit exceeded");

        when(repository.save(any(Transaction.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        PurchaseService service = new PurchaseService(
                repository,
                validationService
        );

        Transaction result = service.purchase(
                6L,
                new BigDecimal("250"),
                "Purchase limit test",
                "Food"
        );

        assertEquals(TransactionStatus.REJECTED, result.getStatus());
        assertEquals("Transaction limit exceeded",
                result.getRejectionReason());
        assertEquals("Food", result.getCategory());

        verify(repository).save(any(Transaction.class));
    }


    @Test
    void shouldApproveValidPurchase() {

        TransactionRepositoryPort repository =
                mock(TransactionRepositoryPort.class);

        TransactionValidationService validationService =
                mock(TransactionValidationService.class);

        when(repository.save(any(Transaction.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        PurchaseService service = new PurchaseService(
                repository,
                validationService
        );

        Transaction result = service.purchase(
                6L,
                new BigDecimal("50"),
                "Grocery shopping",
                "Food"
        );

        assertEquals(TransactionStatus.APPROVED, result.getStatus());
        assertEquals("Food", result.getCategory());

        verify(repository).save(any(Transaction.class));
    }

}
