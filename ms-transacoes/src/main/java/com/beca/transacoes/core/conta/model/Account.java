package com.beca.transacoes.core.conta.model;

import java.math.BigDecimal;

public record Account (
        Long userId,
        BigDecimal balance,
        BigDecimal dailyLimit,
        BigDecimal transactionLimit
) {
}
