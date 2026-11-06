package com.beca.transacoes.adapter.output.client;

import com.beca.transacoes.core.conta.model.Account;
import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestClient;

import java.net.InetSocketAddress;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
}
