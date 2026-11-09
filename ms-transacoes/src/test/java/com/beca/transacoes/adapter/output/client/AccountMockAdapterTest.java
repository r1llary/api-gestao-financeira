
package com.beca.transacoes.adapter.output.client;

import com.beca.transacoes.core.conta.model.Account;
import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

class AccountMockAdapterTest {

    @Test
    void shouldFindAccount() throws Exception {

        HttpServer server = HttpServer.create(
                new InetSocketAddress("127.0.0.1", 0), 0);

        server.createContext("/accounts", exchange -> {

            String json = """
                    [{
                        "userId": 6,
                        "balance": 1000,
                        "dailyLimit": 500,
                        "transactionLimit": 200
                    }]
                    """;

            byte[] response = json.getBytes(StandardCharsets.UTF_8);

            exchange.getResponseHeaders()
                    .add("Content-Type", "application/json");

            exchange.sendResponseHeaders(200, response.length);

            try (var output = exchange.getResponseBody()) {
                output.write(response);
            }
        });

        server.start();

        try {
            String url = "http://127.0.0.1:"
                    + server.getAddress().getPort();

            AccountMockAdapter adapter = new AccountMockAdapter(
                    RestClient.builder(), url);

            Account account = adapter.findByUserId(6L);

            assertEquals(6L, account.userId());
            assertEquals(0, account.balance()
                    .compareTo(new BigDecimal("1000")));
            assertEquals(0, account.dailyLimit()
                    .compareTo(new BigDecimal("500")));
            assertEquals(0, account.transactionLimit()
                    .compareTo(new BigDecimal("200")));

        } finally {
            server.stop(0);
        }
    }

    @Test
    void shouldUpdateAccountBalance() throws Exception {

        HttpServer server = HttpServer.create(
                new InetSocketAddress("127.0.0.1", 0), 0);

        AtomicReference<String> receivedPath = new AtomicReference<>();
        AtomicReference<String> receivedBody = new AtomicReference<>();

        server.createContext("/accounts", exchange -> {

            if (exchange.getRequestMethod().equals("GET")) {

                String json = """
                        [{
                            "id": "1",
                            "userId": 6,
                            "balance": 1000,
                            "dailyLimit": 500,
                            "transactionLimit": 200
                        }]
                        """;

                byte[] response = json.getBytes(StandardCharsets.UTF_8);

                exchange.getResponseHeaders()
                        .add("Content-Type", "application/json");

                exchange.sendResponseHeaders(200, response.length);

                try (var output = exchange.getResponseBody()) {
                    output.write(response);
                }

            } else if (exchange.getRequestMethod().equals("PATCH")) {

                receivedPath.set(exchange.getRequestURI().getPath());

                String body = new String(
                        exchange.getRequestBody().readAllBytes(),
                        StandardCharsets.UTF_8
                );

                receivedBody.set(body);

                exchange.sendResponseHeaders(204, -1);
                exchange.close();

            } else {
                exchange.sendResponseHeaders(405, -1);
                exchange.close();
            }
        });

        server.start();

        try {
            String url = "http://127.0.0.1:"
                    + server.getAddress().getPort();

            AccountMockAdapter adapter = new AccountMockAdapter(
                    RestClient.builder(), url);

            adapter.updateBalance(6L, new BigDecimal("1100.00"));

            assertEquals("/accounts/1", receivedPath.get());

            assertNotNull(receivedBody.get());
            assertTrue(receivedBody.get().contains("\"balance\""));
            assertTrue(receivedBody.get().contains("1100.00"));

            assertFalse(receivedBody.get().contains("dailyLimit"));
            assertFalse(receivedBody.get().contains("transactionLimit"));

        } finally {
            server.stop(0);
        }
    }
}
