package com.beca.transacoes.core.transacao.port.output;

import com.beca.transacoes.core.transacao.model.Transaction;

public interface TransactionRepositoryPort {
    Transaction save(Transaction transaction);

    Transaction findById(Long id);
}
