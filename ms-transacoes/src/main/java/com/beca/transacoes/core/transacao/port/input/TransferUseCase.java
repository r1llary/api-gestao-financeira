
package com.beca.transacoes.core.transacao.port.input;

import com.beca.transacoes.core.transacao.model.Transaction;
import java.math.BigDecimal;

public interface TransferUseCase {

    Transaction transfer(
            Long userId,
            Long destinationUserId,
            BigDecimal amount,
            String description,
            String category,
            String authorization
    );
}
