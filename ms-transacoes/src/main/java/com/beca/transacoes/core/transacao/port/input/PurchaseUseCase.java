
package com.beca.transacoes.core.transacao.port.input;

import com.beca.transacoes.core.transacao.model.Transaction;
import java.math.BigDecimal;

public interface PurchaseUseCase {

    Transaction purchase(
            Long userId,
            BigDecimal amount,
            String description,
            String category
    );
}
