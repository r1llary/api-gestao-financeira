
package com.beca.transacoes.core.cambio.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ExchangeRate(
        String currency,
        BigDecimal brlPerUnit,
        LocalDate quoteDate
) {
}
