
package com.beca.transacoes.core.cambio.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ConversionResult(
        String currency,
        BigDecimal convertedAmount,
        BigDecimal rate,
        LocalDate quoteDate,
        boolean available
) {
}
