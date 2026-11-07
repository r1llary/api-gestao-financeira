
package com.beca.transacoes.adapter.input.rest;

import com.beca.transacoes.adapter.input.security.AuthenticatedUser;
import com.beca.transacoes.core.transacao.model.Transaction;
import com.beca.transacoes.core.transacao.model.TransactionStatus;
import com.beca.transacoes.core.transacao.port.input.TransferUseCase;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/transactions")
public class TransferController {

    private final TransferUseCase transferUseCase;

    public TransferController(TransferUseCase transferUseCase) {
        this.transferUseCase = transferUseCase;
    }

    @PostMapping("/transfers")
    public ResponseEntity<TransferResponse> transfer(
            @Valid @RequestBody TransferRequest request,
            @AuthenticationPrincipal AuthenticatedUser user,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization) {

        Transaction transaction = transferUseCase.transfer(
                user.id(),
                request.destinationUserId(),
                request.amount(),
                request.description(),
                request.category(),
                authorization
        );

        TransferResponse response = new TransferResponse(
                transaction.getId(),
                transaction.getStatus(),
                transaction.getRejectionReason()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    public record TransferRequest(
            @NotNull @Positive Long destinationUserId,
            @NotNull @Positive BigDecimal amount,
            @NotBlank String description,
            @NotBlank String category
    ) {
    }

    public record TransferResponse(
            Long id,
            TransactionStatus status,
            String rejectionReason
    ) {
    }
}
