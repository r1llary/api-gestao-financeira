
package com.beca.transacoes.core.transacao.port.input;

import java.time.LocalDate;

public interface ExportTransactionsCsvUseCase {

    byte[] export(
            Long userId,
            LocalDate startDate,
            LocalDate endDate
    );
}
