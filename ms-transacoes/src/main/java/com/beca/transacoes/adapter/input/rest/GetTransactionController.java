
package com.beca.transacoes.adapter.input.rest;

import com.beca.transacoes.adapter.input.rest.dto.TransactionResponse;
import com.beca.transacoes.adapter.input.security.AuthenticatedUser;
import com.beca.transacoes.core.transacao.model.Transaction;
import com.beca.transacoes.core.transacao.port.input.GetTransactionUseCase;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/transactions")
public class GetTransactionController {

    private final GetTransactionUseCase useCase;

    public GetTransactionController(GetTransactionUseCase useCase) {
        this.useCase = useCase;
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
}
