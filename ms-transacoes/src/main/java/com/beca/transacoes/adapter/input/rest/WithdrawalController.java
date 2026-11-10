
package com.beca.transacoes.adapter.input.rest;

import com.beca.transacoes.adapter.input.security.AuthenticatedUser;
import com.beca.transacoes.core.transacao.model.Transaction;
import com.beca.transacoes.core.transacao.model.TransactionStatus;
import com.beca.transacoes.core.transacao.port.input.WithdrawalUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
public class WithdrawalController {

    private final WithdrawalUseCase withdrawalService;

    public WithdrawalController(WithdrawalUseCase withdrawalService) {
        this.withdrawalService = withdrawalService;
    }

    @Operation(
            summary = "Registrar saque",
            description = "Registra um saque para o usuário autenticado. "
                    + "A operação pode ser aprovada ou rejeitada conforme o saldo e os limites."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Saque registrado com status APPROVED ou REJECTED"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "401", description = "Usuário não autenticado"),
            @ApiResponse(responseCode = "503", description = "Serviço de autenticação indisponível")
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
