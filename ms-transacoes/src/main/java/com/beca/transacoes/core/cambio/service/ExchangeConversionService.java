
package com.beca.transacoes.core.cambio.service;

import com.beca.transacoes.core.cambio.model.ConversionResult;
import com.beca.transacoes.core.cambio.model.ExchangeRate;
import com.beca.transacoes.core.cambio.port.output.ExchangeRateGatewayPort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

@Service
public class ExchangeConversionService {

    private final ExchangeRateGatewayPort gateway;

    public ExchangeConversionService(ExchangeRateGatewayPort gateway) {
        this.gateway = gateway;
    }

    public ConversionResult convert(
            BigDecimal amountBrl,
            String currency,
            LocalDate date) {

        if (amountBrl == null || amountBrl.signum() < 0) {
            throw new IllegalArgumentException("Invalid amount");
        }

        if (!"USD".equals(currency) && !"EUR".equals(currency)) {
            throw new IllegalArgumentException("Invalid currency");
        }

        if (date == null) {
            throw new IllegalArgumentException("Invalid date");
        }

        try {
            ExchangeRate rate = gateway.getRate(currency, date);

            if (rate == null
                    || rate.brlPerUnit() == null
                    || rate.brlPerUnit().signum() <= 0) {

                return new ConversionResult(
                        currency, null, null, null, false
                );
            }

            BigDecimal convertedAmount = amountBrl.divide(
                    rate.brlPerUnit(),
                    2,
                    RoundingMode.HALF_UP
            );

            return new ConversionResult(
                    currency,
                    convertedAmount,
                    rate.brlPerUnit(),
                    rate.quoteDate(),
                    true
            );

        } catch (RuntimeException exception) {
            return new ConversionResult(
                    currency, null, null, null, false
            );
        }
    }
}
