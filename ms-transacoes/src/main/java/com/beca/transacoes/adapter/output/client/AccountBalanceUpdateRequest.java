
package com.beca.transacoes.adapter.output.client;

import java.math.BigDecimal;

public record AccountBalanceUpdateRequest(
        BigDecimal balance
) {
}
