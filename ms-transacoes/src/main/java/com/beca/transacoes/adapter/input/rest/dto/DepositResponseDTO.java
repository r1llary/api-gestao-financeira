package com.beca.transacoes.adapter.input.rest.dto;

import com.beca.transacoes.core.transacao.model.TransactionStatus;

public record DepositResponseDTO(
        Long id,
        TransactionStatus status
) {
}
