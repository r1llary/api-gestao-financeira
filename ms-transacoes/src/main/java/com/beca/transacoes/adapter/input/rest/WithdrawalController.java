
package com.beca.transacoes.adapter.input.rest;

import com.beca.transacoes.adapter.input.security.AuthenticatedUser;
import com.beca.transacoes.core.transacao.model.Transaction;
import com.beca.transacoes.core.transacao.model.TransactionStatus;
import com.beca.transacoes.core.transacao.port.input.WithdrawalUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Withdrawals")
public class WithdrawalController {

    private final WithdrawalUseCase withdrawalService;

    public WithdrawalController(WithdrawalUseCase withdrawalService) {
        this.withdrawalService = withdrawalService;
    }

    @Operation(
            summary = "Create withdrawal",
            description = "Creates a withdrawal for the authenticated user. "
                    + "The transaction may be approved or rejected based on "
                    + "the available balance and transaction limits."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Withdrawal created with APPROVED or REJECTED status"
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
    @PostMapping("/withdrawals")
    public ResponseEntity<WithdrawalResponse> withdraw(
            @Valid @RequestBody WithdrawalRequest request,
            @AuthenticationPrincipal AuthenticatedUser user) {

        Transaction transaction = withdrawalService.withdraw(
                user.id(),
                request.amount(),
                request.description(),
                request.category()
        );

        WithdrawalResponse response = new WithdrawalResponse(
                transaction.getId(),
                transaction.getStatus(),
                transaction.getRejectionReason()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    public record WithdrawalRequest(
            @NotNull @Positive BigDecimal amount,
            @NotBlank String description,
            @NotBlank String category
    ) {
    }

    public record WithdrawalResponse(
            Long id,
            TransactionStatus status,
            String rejectionReason
    ) {
    }
}
