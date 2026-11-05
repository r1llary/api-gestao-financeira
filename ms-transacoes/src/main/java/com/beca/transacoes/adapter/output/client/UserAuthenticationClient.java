package com.beca.transacoes.adapter.output.client;

import com.beca.transacoes.adapter.output.client.dto.UserAuthenticationResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.net.http.HttpClient;
import java.time.Duration;

@Component
public class UserAuthenticationClient {

    private final RestClient restClient;

    public UserAuthenticationClient(
            RestClient.Builder builder,
            @Value("${clients.users.base-url}") String baseUrl
    ) {
        HttpClient httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(3))
                .build();

        JdkClientHttpRequestFactory requestFactory =
                new JdkClientHttpRequestFactory(httpClient);

        requestFactory.setReadTimeout(Duration.ofSeconds(5));

        this.restClient = builder
                .baseUrl(baseUrl)
                .requestFactory(requestFactory)
                .build();
    }

    public UserAuthenticationResponse authenticate(String email, String password) {
        try {
            UserAuthenticationResponse user = restClient.get()
                    .uri("/users/me")
                    .headers(headers -> headers.setBasicAuth(email, password))
                    .retrieve()
                    .body(UserAuthenticationResponse.class);

            if (user == null || user.id() == null
                    || user.email() == null || user.email().isBlank()) {
                throw new AuthenticationServiceException(
                        "Invalid response from user service"
                );
            }

            return user;

        } catch (HttpClientErrorException.Unauthorized exception) {
            throw new BadCredentialsException("Invalid email or password");

        } catch (RestClientException exception) {
            throw new AuthenticationServiceException(
                    "Unable to contact user service"
            );
        }
    }
}
