
package com.beca.transacoes.adapter.input.rest;

import com.beca.transacoes.adapter.input.rest.dto.TransactionResponse;
import com.beca.transacoes.adapter.input.security.AuthenticatedUser;
import com.beca.transacoes.core.transacao.model.Transaction;
import com.beca.transacoes.core.transacao.model.TransactionType;
import com.beca.transacoes.core.transacao.port.input.GetTransactionUseCase;
import com.beca.transacoes.core.transacao.port.input.ListTransactionsUseCase;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/transactions")
public class GetTransactionController {

    private final GetTransactionUseCase useCase;
    private final ListTransactionsUseCase listUseCase;

    public GetTransactionController(
            GetTransactionUseCase useCase,
            ListTransactionsUseCase listUseCase) {

        this.useCase = useCase;
        this.listUseCase = listUseCase;
    }

    @GetMapping("/{id}")
    public TransactionResponse getById(
            @PathVariable Long id,
            @AuthenticationPrincipal AuthenticatedUser user) {

        Transaction transaction = useCase.getById(id, user.id());

        return new TransactionResponse(
                transaction.getId(),
                transaction.getUserId(),
                transaction.getAmount(),
                transaction.getType(),
                transaction.getOccurredAt(),
                transaction.getDescription(),
                transaction.getCategory(),
                transaction.getStatus(),
                transaction.getDestinationUserId(),
                transaction.getRejectionReason()
        );
    }

    @GetMapping
    public List<TransactionResponse> list(
            @AuthenticationPrincipal AuthenticatedUser user,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate startDate,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate endDate,

            @RequestParam(required = false)
            TransactionType type) {

        // Busca as transações do usuário
        List<Transaction> transactions = listUseCase.list(
                user.id(),
                startDate,
                endDate,
                type
        );

        // Cria uma lista de DTOs
        List<TransactionResponse> responses = new ArrayList<>();

        // Converte cada transação em um DTO
        for (Transaction transaction : transactions) {

            TransactionResponse response = new TransactionResponse(
                    transaction.getId(),
                    transaction.getUserId(),
                    transaction.getAmount(),
                    transaction.getType(),
                    transaction.getOccurredAt(),
                    transaction.getDescription(),
                    transaction.getCategory(),
                    transaction.getStatus(),
                    transaction.getDestinationUserId(),
                    transaction.getRejectionReason()
            );

            responses.add(response);
        }

        return responses;
    }
}
