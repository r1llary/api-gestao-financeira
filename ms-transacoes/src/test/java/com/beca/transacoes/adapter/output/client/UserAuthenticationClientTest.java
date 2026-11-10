
package com.beca.transacoes.adapter.output.client;

import com.beca.transacoes.adapter.output.client.dto.UserAuthenticationResponse;
import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.client.RestClient;

import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

class UserAuthenticationClientTest {

    @Test
    void shouldAuthenticateValidUser() throws Exception {

        // Arrange
        HttpServer server = HttpServer.create(
                new InetSocketAddress("127.0.0.1", 0), 0
        );

        server.createContext("/users/me", exchange -> {

            String json = """
                    {
                      "id": 6,
                      "name": "Usuario Teste",
                      "email": "teste@exemplo.com"
                    }
                    """;

            byte[] response = json.getBytes(StandardCharsets.UTF_8);

            exchange.getResponseHeaders().set(
                    "Content-Type", "application/json"
            );

            exchange.sendResponseHeaders(200, response.length);
            exchange.getResponseBody().write(response);
            exchange.close();
        });

        server.start();

        try {
            UserAuthenticationClient client =
                    new UserAuthenticationClient(
                            RestClient.builder(),
                            "http://127.0.0.1:" + server.getAddress().getPort()
                    );

            // Act
            UserAuthenticationResponse result = client.authenticate(
                    "teste@exemplo.com",
                    "senha123"
            );

            // Assert
            assertEquals(6L, result.id());
            assertEquals("Usuario Teste", result.name());
            assertEquals("teste@exemplo.com", result.email());

        } finally {
            server.stop(0);
        }
    }

    @Test
    void shouldRejectInvalidCredentials() throws Exception {

        // Arrange
        HttpServer server = HttpServer.create(
                new InetSocketAddress("127.0.0.1", 0), 0
        );

        server.createContext("/users/me", exchange -> {
            exchange.sendResponseHeaders(401, -1);
            exchange.close();
        });

        server.start();

        try {
            UserAuthenticationClient client =
                    new UserAuthenticationClient(
                            RestClient.builder(),
                            "http://127.0.0.1:" + server.getAddress().getPort()
                    );

            // Act
            BadCredentialsException exception = assertThrows(
                    BadCredentialsException.class,
                    () -> client.authenticate(
                            "teste@exemplo.com",
                            "senhaErrada"
                    )
            );

            // Assert
            assertEquals(
                    "Invalid email or password",
                    exception.getMessage()
            );

        } finally {
            server.stop(0);
        }
    }

    @Test
    void shouldHandleUnavailableUserService() throws Exception {

        // Arrange
        HttpServer server = HttpServer.create(
                new InetSocketAddress("127.0.0.1", 0), 0
        );

        server.createContext("/users/me", exchange -> {
            exchange.sendResponseHeaders(503, -1);
            exchange.close();
        });

        server.start();

        try {
            UserAuthenticationClient client =
                    new UserAuthenticationClient(
                            RestClient.builder(),
                            "http://127.0.0.1:" + server.getAddress().getPort()
                    );

            // Act
            AuthenticationServiceException exception = assertThrows(
                    AuthenticationServiceException.class,
                    () -> client.authenticate(
                            "teste@exemplo.com",
                            "senha123"
                    )
            );

            // Assert
            assertEquals(
                    "Unable to contact user service",
                    exception.getMessage()
            );

        } finally {
            server.stop(0);
        }
    }


    @Test
    void shouldRejectInvalidUserResponse() throws Exception {

        // Arrange
        HttpServer server = HttpServer.create(
                new InetSocketAddress("127.0.0.1", 0), 0
        );

        server.createContext("/users/me", exchange -> {

            String json = """
                {
                  "name": "Usuario Teste",
                  "email": "teste@exemplo.com"
                }
                """;

            byte[] response = json.getBytes(StandardCharsets.UTF_8);

            exchange.getResponseHeaders().set(
                    "Content-Type", "application/json"
            );

            exchange.sendResponseHeaders(200, response.length);
            exchange.getResponseBody().write(response);
            exchange.close();
        });

        server.start();

        try {
            UserAuthenticationClient client =
                    new UserAuthenticationClient(
                            RestClient.builder(),
                            "http://127.0.0.1:" + server.getAddress().getPort()
                    );

            // Act
            AuthenticationServiceException exception = assertThrows(
                    AuthenticationServiceException.class,
                    () -> client.authenticate(
                            "teste@exemplo.com",
                            "senha123"
                    )
            );

            // Assert
            assertEquals(
                    "Invalid response from user service",
                    exception.getMessage()
            );

        } finally {
            server.stop(0);
        }
    }


    @Test
    void shouldReturnTrueWhenUserExists() throws Exception {

        // Arrange
        HttpServer server = HttpServer.create(
                new InetSocketAddress("127.0.0.1", 0), 0
        );

        server.createContext("/users/6", exchange -> {
            exchange.sendResponseHeaders(200, -1);
            exchange.close();
        });

        server.start();

        try {
            UserAuthenticationClient client =
                    new UserAuthenticationClient(
                            RestClient.builder(),
                            "http://127.0.0.1:" + server.getAddress().getPort()
                    );

            // Act
            boolean result = client.userExists(
                    6L,
                    "Basic dGVzdDpzZW5oYQ=="
            );

            // Assert
            assertTrue(result);

        } finally {
            server.stop(0);
        }
    }


    @Test
    void shouldReturnFalseWhenUserDoesNotExist() throws Exception {

        // Arrange
        HttpServer server = HttpServer.create(
                new InetSocketAddress("127.0.0.1", 0), 0
        );

        server.createContext("/users/999", exchange -> {
            exchange.sendResponseHeaders(404, -1);
            exchange.close();
        });

        server.start();

        try {
            UserAuthenticationClient client =
                    new UserAuthenticationClient(
                            RestClient.builder(),
                            "http://127.0.0.1:" + server.getAddress().getPort()
                    );

            // Act
            boolean result = client.userExists(
                    999L,
                    "Basic dGVzdDpzZW5oYQ=="
            );

            // Assert
            assertFalse(result);

        } finally {
            server.stop(0);
        }
    }


    @Test
    void shouldHandleErrorWhenCheckingUser() throws Exception {

        // Arrange
        HttpServer server = HttpServer.create(
                new InetSocketAddress("127.0.0.1", 0), 0
        );

        server.createContext("/users/6", exchange -> {
            exchange.sendResponseHeaders(503, -1);
            exchange.close();
        });

        server.start();

        try {
            UserAuthenticationClient client =
                    new UserAuthenticationClient(
                            RestClient.builder(),
                            "http://127.0.0.1:" + server.getAddress().getPort()
                    );

            // Act
            AuthenticationServiceException exception = assertThrows(
                    AuthenticationServiceException.class,
                    () -> client.userExists(
                            6L,
                            "Basic dGVzdDpzZW5oYQ=="
                    )
            );

            // Assert
            assertEquals(
                    "Unable to verify destination user",
                    exception.getMessage()
            );

        } finally {
            server.stop(0);
        }
    }

}
