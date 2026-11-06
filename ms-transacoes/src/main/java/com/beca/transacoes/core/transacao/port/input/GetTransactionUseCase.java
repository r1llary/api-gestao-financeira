package com.beca.transacoes.core.transacao.port.input;

import com.beca.transacoes.core.transacao.model.Transaction;

public interface GetTransactionUseCase {
    Transaction getById(Long id, Long userId);
}
