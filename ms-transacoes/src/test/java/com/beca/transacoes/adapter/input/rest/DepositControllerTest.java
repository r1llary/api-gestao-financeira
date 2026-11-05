package com.beca.transacoes.adapter.input.rest;

import com.beca.transacoes.adapter.input.rest.dto.DepositRequestDTO;
import com.beca.transacoes.adapter.input.rest.dto.DepositResponseDTO;
import com.beca.transacoes.adapter.input.rest.exception.GlobalExceptionHandler;
import com.beca.transacoes.adapter.input.security.AuthenticatedUser;
import com.beca.transacoes.core.transacao.model.Transaction;
import com.beca.transacoes.core.transacao.model.TransactionStatus;
import com.beca.transacoes.core.transacao.port.input.DepositUseCase;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class DepositControllerTest {

    @Test
    void shouldCreateDeposit() {

        DepositUseCase useCase = mock(DepositUseCase.class);
        DepositController controller = new DepositController(useCase);

        AuthenticatedUser user = new AuthenticatedUser(
                6L,
                "user.three@example.com"
        );

        BigDecimal amount = new BigDecimal("100.00");

        DepositRequestDTO request = new DepositRequestDTO(
                amount,
                "Depósito inicial",
                "Deposit"
        );

        Transaction transaction = mock(Transaction.class);

        when(transaction.getId()).thenReturn(1L);
        when(transaction.getStatus()).thenReturn(TransactionStatus.APPROVED);

        when(useCase.deposit(6L, amount, "Depósito inicial", "Deposit"))
                .thenReturn(transaction);

        ResponseEntity<DepositResponseDTO> response =
                controller.deposit(request, user);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(Long.valueOf(1L), response.getBody().id());
        assertEquals(TransactionStatus.APPROVED, response.getBody().status());

        verify(useCase).deposit(
                6L,
                amount,
                "Depósito inicial",
                "Deposit"
        );
    }

    @Test
    void shouldRejectZeroAmount() throws Exception {

        DepositUseCase useCase = mock(DepositUseCase.class);

        MockMvc mockMvc = MockMvcBuilders
                .standaloneSetup(new DepositController(useCase))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        String json = """
                {
                  "amount": 0,
                  "description": "Teste",
                  "category": "Deposit"
                }
                """;

        mockMvc.perform(post("/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].field").value("amount"))
                .andExpect(jsonPath("$.errors[0].message")
                        .value("Amount must be greater than zero"));

        verifyNoInteractions(useCase);
    }
}