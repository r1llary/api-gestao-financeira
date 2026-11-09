package com.beca.transacoes.core.conta.port.input;

import com.beca.transacoes.core.conta.model.Account;

public interface AccountUseCase {

    Account findByUserId(Long userId);
}
