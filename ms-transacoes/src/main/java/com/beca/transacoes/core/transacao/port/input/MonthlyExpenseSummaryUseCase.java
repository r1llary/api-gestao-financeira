
package com.beca.transacoes.core.transacao.port.input;

import com.beca.transacoes.core.transacao.model.MonthlyExpenseSummary;
import java.time.LocalDate;

public interface MonthlyExpenseSummaryUseCase {

    MonthlyExpenseSummary summarize(
            Long userId,
            LocalDate startDate,
            LocalDate endDate
    );
}
