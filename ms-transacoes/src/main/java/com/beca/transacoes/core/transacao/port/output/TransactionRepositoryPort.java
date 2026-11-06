package com.beca.transacoes.core.transacao.port.output;

import com.beca.transacoes.core.transacao.model.Transaction;

import java.time.Instant;
import java.util.List;

public interface TransactionRepositoryPort {
    Transaction save(Transaction transaction);

    Transaction findById(Long id);

    List<Transaction> findApprovedByUserIdAndPeriod(
            Long userId,
            Instant start,
            Instant end
    );
}
