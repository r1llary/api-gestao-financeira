
package com.beca.transacoes.core.transacao.port.input;

import com.beca.transacoes.core.transacao.model.Transaction;
import com.beca.transacoes.core.transacao.model.TransactionType;

import java.time.LocalDate;
import java.util.List;

public interface ListTransactionsUseCase {

    List<Transaction> list(
            Long userId,
            LocalDate startDate,
            LocalDate endDate,
            TransactionType type
    );
}
