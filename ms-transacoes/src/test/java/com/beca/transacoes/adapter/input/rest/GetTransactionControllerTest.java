
package com.beca.transacoes.adapter.input.rest;

import com.beca.transacoes.adapter.input.rest.dto.TransactionResponse;
import com.beca.transacoes.adapter.input.security.AuthenticatedUser;
import com.beca.transacoes.core.transacao.model.Transaction;
import com.beca.transacoes.core.transacao.model.TransactionStatus;
import com.beca.transacoes.core.transacao.model.TransactionType;
import com.beca.transacoes.core.transacao.port.input.GetTransactionUseCase;
import com.beca.transacoes.core.transacao.port.input.ListTransactionsUseCase;
import com.beca.transacoes.core.cambio.service.ExchangeConversionService;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GetTransactionControllerTest {

    @Test
    void shouldReturnTransactionResponseList() {

        // Arrange
        GetTransactionUseCase getUseCase =
                mock(GetTransactionUseCase.class);

        ListTransactionsUseCase listUseCase =
                mock(ListTransactionsUseCase.class);

        AuthenticatedUser user = mock(AuthenticatedUser.class);

        when(user.id()).thenReturn(6L);

        Transaction transaction = new Transaction(
                18L,
                6L,
                new BigDecimal("50"),
                TransactionType.PURCHASE,
                Instant.parse("2026-11-07T17:34:16Z"),
                "Grocery shopping",
                "Food",
                TransactionStatus.APPROVED,
                null,
                null
        );

        when(listUseCase.list(6L, null, null, null))
                .thenReturn(List.of(transaction));

        ExchangeConversionService conversionService =
                mock(ExchangeConversionService.class);

        GetTransactionController controller =
                new GetTransactionController(
                        getUseCase,
                        listUseCase,
                        conversionService
                );

        // Act
        List<TransactionResponse> result =
                controller.list(user, null, null, null);

        // Assert
        assertEquals(1, result.size());

        TransactionResponse response = result.get(0);

        assertEquals(18L, response.id());
        assertEquals(6L, response.userId());
        assertEquals(new BigDecimal("50"), response.amount());
        assertEquals(TransactionType.PURCHASE, response.type());
        assertEquals("Grocery shopping", response.description());
        assertEquals("Food", response.category());
        assertEquals(TransactionStatus.APPROVED, response.status());

        verify(listUseCase).list(6L, null, null, null);
    }
}
