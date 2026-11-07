
package com.beca.transacoes.core.cambio.port.output;

import com.beca.transacoes.core.cambio.model.ExchangeRate;
import java.time.LocalDate;

public interface ExchangeRateGatewayPort {

    ExchangeRate getRate(String currency, LocalDate date);

}
