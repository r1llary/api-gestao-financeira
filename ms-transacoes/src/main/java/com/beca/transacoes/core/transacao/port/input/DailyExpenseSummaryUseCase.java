
package com.beca.transacoes.core.transacao.port.input;

import com.beca.transacoes.core.transacao.model.DailyExpenseSummary;
import java.time.LocalDate;

public interface DailyExpenseSummaryUseCase {

    DailyExpenseSummary summarize(
            Long userId,
            LocalDate startDate,
            LocalDate endDate
    );
}
