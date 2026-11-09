
package com.beca.transacoes.adapter.output.client;

import java.math.BigDecimal;

public record AccountMockResponse(
        String id,
        Long userId,
        BigDecimal balance,
        BigDecimal dailyLimit,
        BigDecimal transactionLimit
) {
}
