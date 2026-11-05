package com.beca.transacoes.adapter.input.rest;

import com.beca.transacoes.adapter.input.rest.dto.DepositRequestDTO;
import com.beca.transacoes.adapter.input.rest.dto.DepositResponseDTO;
import com.beca.transacoes.adapter.input.security.AuthenticatedUser;
import com.beca.transacoes.core.transacao.model.Transaction;
import com.beca.transacoes.core.transacao.port.input.DepositUseCase;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/transactions")
public class DepositController {

    private final DepositUseCase depositUseCase;

    public DepositController(DepositUseCase depositUseCase) {
        this.depositUseCase = depositUseCase;
    }

    @PostMapping("/deposits")
    public ResponseEntity<DepositResponseDTO> deposit(
            @Valid @RequestBody DepositRequestDTO request,
            @AuthenticationPrincipal AuthenticatedUser user
    ) {
        Transaction transaction = depositUseCase.deposit(
                user.id(),
                request.amount(),
                request.description(),
                request.category()
        );

        DepositResponseDTO response = new DepositResponseDTO(
                transaction.getId(),
                transaction.getStatus()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}