
package com.beca.transacoes.adapter.input.rest;

import com.beca.transacoes.adapter.input.security.AuthenticatedUser;
import com.beca.transacoes.core.transacao.model.Transaction;
import com.beca.transacoes.core.transacao.model.TransactionStatus;
import com.beca.transacoes.core.transacao.port.input.PurchaseUseCase;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/transactions")
public class PurchaseController {

    private final PurchaseUseCase purchaseUseCase;

    public PurchaseController(PurchaseUseCase purchaseUseCase) {
        this.purchaseUseCase = purchaseUseCase;
    }

    @PostMapping("/purchases")
    public ResponseEntity<PurchaseResponse> purchase(
            @Valid @RequestBody PurchaseRequest request,
            @AuthenticationPrincipal AuthenticatedUser user) {

        Transaction transaction = purchaseUseCase.purchase(
                user.id(),
                request.amount(),
                request.description(),
                request.category()
        );

        PurchaseResponse response = new PurchaseResponse(
                transaction.getId(),
                transaction.getStatus(),
                transaction.getRejectionReason()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    public record PurchaseRequest(
            @NotNull @Positive BigDecimal amount,
            @NotBlank String description,
            @NotBlank(message = "Category must not be blank") String category
    ) {
    }

    public record PurchaseResponse(
            Long id,
            TransactionStatus status,
            String rejectionReason
    ) {
    }
}
