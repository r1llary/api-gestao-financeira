
package com.beca.transacoes.adapter.input.rest.dto;

import com.beca.transacoes.core.transacao.model.TransactionStatus;
import com.beca.transacoes.core.transacao.model.TransactionType;

import java.math.BigDecimal;
import java.time.Instant;

public record TransactionResponse(
        Long id,
        Long userId,
        BigDecimal amount,
        TransactionType type,
        Instant occurredAt,
        String description,
        String category,
        TransactionStatus status,
        Long destinationUserId,
        String rejectionReason
) {
}
