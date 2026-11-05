package com.beca.transacoes.adapter.input.rest;

import com.beca.transacoes.adapter.input.rest.dto.DepositRequestDTO;
import com.beca.transacoes.adapter.input.rest.dto.DepositResponseDTO;
import com.beca.transacoes.adapter.input.security.AuthenticatedUser;
import com.beca.transacoes.core.transacao.model.Transaction;
import com.beca.transacoes.core.transacao.model.TransactionStatus;
import com.beca.transacoes.core.transacao.model.TransactionType;
import com.beca.transacoes.core.transacao.port.input.DepositUseCase;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DepositControllerTest {

    @Test
    void shouldCreateDepositForAuthenticatedUser() {
        DepositUseCase depositUseCase = mock(DepositUseCase.class);

        DepositController controller =
                new DepositController(depositUseCase);

        AuthenticatedUser user =
                new AuthenticatedUser(6L, "user.three@example.com");

        DepositRequestDTO request = new DepositRequestDTO(
                new BigDecimal("100.00"),
                "Depósito inicial",
                "Deposit"
        );

        Transaction transaction = new Transaction(
                1L,
                user.id(),
                request.amount(),
                TransactionType.DEPOSIT,
                Instant.now(),
                request.description(),
                request.category(),
                TransactionStatus.APPROVED,
                null,
                null
        );

        when(depositUseCase.deposit(
                user.id(),
                request.amount(),
                request.description(),
                request.category()
        )).thenReturn(transaction);

        ResponseEntity<DepositResponseDTO> response =
                controller.deposit(request, user);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(Long.valueOf(1L), response.getBody().id());
        assertEquals(
                TransactionStatus.APPROVED,
                response.getBody().status()
        );

        verify(depositUseCase).deposit(
                6L,
                request.amount(),
                request.description(),
                request.category()
        );
    }
}