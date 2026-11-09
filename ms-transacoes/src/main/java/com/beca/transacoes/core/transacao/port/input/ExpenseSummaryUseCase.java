
package com.beca.transacoes.core.transacao.port.input;

import com.beca.transacoes.core.transacao.model.ExpenseSummary;

import java.time.LocalDate;

public interface ExpenseSummaryUseCase {

    ExpenseSummary summarize(
            Long userId,
            LocalDate startDate,
            LocalDate endDate
    );
}
