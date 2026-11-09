
package com.beca.transacoes.core.cambio.service;

import com.beca.transacoes.core.cambio.model.ConversionResult;
import com.beca.transacoes.core.cambio.model.ExchangeRate;
import com.beca.transacoes.core.cambio.port.output.ExchangeRateGatewayPort;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ExchangeConversionServiceTest {

    @Test
    void shouldConvertBrlToUsd() {

        ExchangeRateGatewayPort gateway =
                mock(ExchangeRateGatewayPort.class);

        ExchangeConversionService service =
                new ExchangeConversionService(gateway);

        LocalDate date = LocalDate.of(2025, 2, 13);

        ExchangeRate rate = new ExchangeRate(
                "USD",
                new BigDecimal("5.7788"),
                date
        );

        when(gateway.getRate("USD", date)).thenReturn(rate);

        ConversionResult result = service.convert(
                new BigDecimal("100.00"),
                "USD",
                date
        );

        assertEquals(
                new BigDecimal("17.30"),
                result.convertedAmount()
        );

        assertTrue(result.available());
    }

    @Test
    void shouldReturnUnavailableWhenApiFails() {

        ExchangeRateGatewayPort gateway =
                mock(ExchangeRateGatewayPort.class);

        ExchangeConversionService service =
                new ExchangeConversionService(gateway);

        LocalDate date = LocalDate.of(2025, 2, 13);

        when(gateway.getRate("USD", date))
                .thenThrow(new RuntimeException("API unavailable"));

        ConversionResult result = service.convert(
                new BigDecimal("100.00"),
                "USD",
                date
        );

        assertFalse(result.available());
        assertNull(result.convertedAmount());
        assertNull(result.rate());
    }

    @Test
    void shouldConvertBrlToEur() {

        ExchangeRateGatewayPort gateway =
                mock(ExchangeRateGatewayPort.class);

        ExchangeConversionService service =
                new ExchangeConversionService(gateway);

        LocalDate date = LocalDate.of(2026, 11, 5);

        ExchangeRate rate = new ExchangeRate(
                "EUR",
                new BigDecimal("6.1323"),
                date
        );

        when(gateway.getRate("EUR", date)).thenReturn(rate);

        ConversionResult result = service.convert(
                new BigDecimal("100.00"),
                "EUR",
                date
        );

        assertEquals(
                new BigDecimal("16.31"),
                result.convertedAmount()
        );

        assertTrue(result.available());
    }

    @Test
    void shouldRejectInvalidCurrency() {

        // Arrange
        ExchangeRateGatewayPort gateway =
                mock(ExchangeRateGatewayPort.class);

        ExchangeConversionService service =
                new ExchangeConversionService(gateway);

        LocalDate date = LocalDate.of(2026, 11, 9);

        // Act
        assertThrows(IllegalArgumentException.class, () ->
                service.convert(
                        new BigDecimal("100.00"),
                        "GBP",
                        date
                )
        );

        // Assert
        verifyNoInteractions(gateway);
    }


}
