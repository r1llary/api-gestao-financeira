
package com.beca.transacoes.core.transacao.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record DailyExpenseSummary(
        LocalDate startDate,
        LocalDate endDate,
        BigDecimal totalAmount,
        List<DayTotal> days
) {

    public record DayTotal(
            LocalDate date,
            BigDecimal amount
    ) {
    }
}
