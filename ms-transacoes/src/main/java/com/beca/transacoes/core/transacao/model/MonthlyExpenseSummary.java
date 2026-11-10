
package com.beca.transacoes.core.transacao.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

public record MonthlyExpenseSummary(
        LocalDate startDate,
        LocalDate endDate,
        BigDecimal totalAmount,
        List<MonthTotal> months
) {

    public record MonthTotal(
            YearMonth month,
            BigDecimal amount
    ) {
    }
}
