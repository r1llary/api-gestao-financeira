
package com.beca.transacoes.adapter.input.rest;

import com.beca.transacoes.adapter.input.security.AuthenticatedUser;
import com.beca.transacoes.core.transacao.model.Transaction;
import com.beca.transacoes.core.transacao.model.TransactionStatus;
import com.beca.transacoes.core.transacao.port.input.PurchaseUseCase;
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
public class PurchaseController {

    private final PurchaseUseCase purchaseUseCase;

    public PurchaseController(PurchaseUseCase purchaseUseCase) {
        this.purchaseUseCase = purchaseUseCase;
    }

    @Operation(
            summary = "Registrar compra",
            description = "Registra uma compra para o usuário autenticado. "
                    + "A operação pode ser aprovada ou rejeitada "
                    + "conforme o saldo e os limites da conta."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Compra registrada com status APPROVED ou REJECTED"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "401", description = "Usuário não autenticado"),
            @ApiResponse(responseCode = "503", description = "Serviço de autenticação indisponível")
    })
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
