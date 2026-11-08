
package com.beca.transacoes.core.transacao.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record ExpenseSummary(
        LocalDate startDate,
        LocalDate endDate,
        BigDecimal totalAmount,
        List<CategoryTotal> categories
) {

    public record CategoryTotal(
            String category,
            BigDecimal total
    ) {
    }
}
