package com.beca.transacoes.core.conta.port.output;

import com.beca.transacoes.core.conta.model.Account;

public interface AccountGatewayPort {

    Account findByUserId(Long userId);
}
