
package com.beca.transacoes.core.conta.port.output;

import com.beca.transacoes.core.conta.model.Account;
import java.math.BigDecimal;

public interface AccountGatewayPort {

    Account findByUserId(Long userId);

    void updateBalance(Long userId, BigDecimal newBalance);
}
