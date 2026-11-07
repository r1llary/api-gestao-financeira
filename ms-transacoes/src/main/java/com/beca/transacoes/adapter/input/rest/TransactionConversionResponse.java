
package com.beca.transacoes.adapter.input.rest;

import com.beca.transacoes.core.cambio.model.ConversionResult;
import com.beca.transacoes.core.transacao.model.TransactionStatus;
import com.beca.transacoes.core.transacao.model.TransactionType;

import java.math.BigDecimal;
import java.time.Instant;

public record TransactionConversionResponse(
        Long id,
        Long userId,
        BigDecimal amount,
        TransactionType type,
        Instant occurredAt,
        String description,
        String category,
        TransactionStatus status,
        Long destinationUserId,
        String rejectionReason,
        String originalCurrency,
        ConversionResult usd,
        ConversionResult eur
) {
}
