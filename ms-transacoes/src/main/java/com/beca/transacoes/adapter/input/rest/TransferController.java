
package com.beca.transacoes.adapter.input.rest;

import com.beca.transacoes.adapter.input.security.AuthenticatedUser;
import com.beca.transacoes.core.transacao.model.Transaction;
import com.beca.transacoes.core.transacao.model.TransactionStatus;
import com.beca.transacoes.core.transacao.port.input.TransferUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Transfers")
public class TransferController {

    private final TransferUseCase transferUseCase;

    public TransferController(TransferUseCase transferUseCase) {
        this.transferUseCase = transferUseCase;
    }

    @Operation(
            summary = "Create transfer",
            description = "Transfers an amount to another user. "
                    + "The transaction may be approved or rejected "
                    + "based on the available balance, transaction limits, "
                    + "and destination user."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Transfer created with APPROVED or REJECTED status"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request data"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "User not authenticated"
            ),
            @ApiResponse(
                    responseCode = "503",
                    description = "Authentication service unavailable"
            )
    })
    @PostMapping("/transfers")
    public ResponseEntity<TransferResponse> transfer(
            @Valid @RequestBody TransferRequest request,
            @AuthenticationPrincipal AuthenticatedUser user,
            @Parameter(hidden = true)
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
