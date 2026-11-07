
package com.beca.transacoes.adapter.input.rest;

import com.beca.transacoes.adapter.input.security.AuthenticatedUser;
import com.beca.transacoes.core.transacao.model.Transaction;
import com.beca.transacoes.core.transacao.model.TransactionType;
import com.beca.transacoes.core.transacao.port.input.GetTransactionUseCase;
import com.beca.transacoes.core.transacao.port.input.ListTransactionsUseCase;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
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
    public Transaction getById(
            @PathVariable Long id,
            @AuthenticationPrincipal AuthenticatedUser user) {

        return useCase.getById(id, user.id());
    }

    @GetMapping
    public List<Transaction> list(
            @AuthenticationPrincipal AuthenticatedUser user,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate startDate,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate endDate,

            @RequestParam(required = false)
            TransactionType type) {

        return listUseCase.list(
                user.id(),
                startDate,
                endDate,
                type
        );
    }
}
