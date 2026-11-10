package com.beca.transacoes.adapter.output.client;

import com.beca.transacoes.core.cambio.model.ExchangeRate;
import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BrasilApiExchangeAdapterTest {

    private HttpServer server;
    private BrasilApiExchangeAdapter adapter;
    private AtomicReference<String> requestedPath;

    @BeforeEach
    void setUp() throws Exception {

        requestedPath = new AtomicReference<>();

        server = HttpServer.create(
                new InetSocketAddress("127.0.0.1", 0), 0
        );

        server.createContext("/api/cambio/v1/cotacao/", exchange -> {

            requestedPath.set(exchange.getRequestURI().getPath());

            String json = """
                    {
                      "moeda": "USD",
                      "data": "2026-11-06",
                      "cotacoes": [
                        {
                          "cotacao_venda": 5.25,
                          "tipo_boletim": "FECHAMENTO PTAX"
                        }
                      ]
                    }
                    """;

            byte[] body = json.getBytes(StandardCharsets.UTF_8);

            exchange.getResponseHeaders()
                    .set("Content-Type", "application/json");

            exchange.sendResponseHeaders(200, body.length);

            try (var output = exchange.getResponseBody()) {
                output.write(body);
            }
        });

        server.start();

        adapter = new BrasilApiExchangeAdapter(
                "http://127.0.0.1:" + server.getAddress().getPort()
        );
    }

    @AfterEach
    void tearDown() {
        server.stop(0);
    }

    @Test
    void shouldUseYesterdayForTodayTransaction() {

        // Arrange
        LocalDate today = LocalDate.now(
                ZoneId.of("America/Sao_Paulo")
        );

        LocalDate yesterday = today.minusDays(1);

        // Act
        ExchangeRate result = adapter.getRate("USD", today);

        // Assert
        assertEquals(
                "/api/cambio/v1/cotacao/USD/" + yesterday,
                requestedPath.get()
        );

        assertEquals(
                LocalDate.of(2026, 11, 6),
                result.quoteDate()
        );
    }

    @Test
    void shouldUseYesterdayForFutureTransaction() {

        // Arrange
        LocalDate today = LocalDate.now(
                ZoneId.of("America/Sao_Paulo")
        );

        LocalDate futureDate = today.plusDays(5);
        LocalDate yesterday = today.minusDays(1);

        // Act
        adapter.getRate("USD", futureDate);

        // Assert
        assertEquals(
                "/api/cambio/v1/cotacao/USD/" + yesterday,
                requestedPath.get()
        );
    }

    @Test
    void shouldKeepOriginalDateForPastTransaction() {

        // Arrange
        LocalDate today = LocalDate.now(
                ZoneId.of("America/Sao_Paulo")
        );

        LocalDate pastDate = today.minusDays(5);

        // Act
        ExchangeRate result = adapter.getRate("USD", pastDate);

        // Assert
        assertEquals(
                "/api/cambio/v1/cotacao/USD/" + pastDate,
                requestedPath.get()
        );

        assertEquals("USD", result.currency());
        assertEquals(
                new BigDecimal("5.25"),
                result.brlPerUnit()
        );
    }
}