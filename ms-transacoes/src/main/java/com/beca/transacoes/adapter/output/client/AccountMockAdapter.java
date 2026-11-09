
package com.beca.transacoes.adapter.output.client;

import com.beca.transacoes.core.conta.exception.AccountNotFoundException;
import com.beca.transacoes.core.conta.exception.AccountServiceUnavailableException;
import com.beca.transacoes.core.conta.model.Account;
import com.beca.transacoes.core.conta.port.output.AccountGatewayPort;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.math.BigDecimal;
import java.net.http.HttpClient;
import java.time.Duration;

@Component
public class AccountMockAdapter implements AccountGatewayPort {

    private final RestClient client;

    public AccountMockAdapter(
            RestClient.Builder builder,
            @Value("${clients.accounts.base-url}") String baseUrl
    ) {
        HttpClient httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(3))
                .build();

        JdkClientHttpRequestFactory factory =
                new JdkClientHttpRequestFactory(httpClient);

        factory.setReadTimeout(Duration.ofSeconds(5));

        this.client = builder
                .baseUrl(baseUrl)
                .requestFactory(factory)
                .build();
    }

    @Override
    public Account findByUserId(Long userId) {

        AccountMockResponse found = findAccountByUserId(userId);

        return new Account(
                found.userId(),
                found.balance(),
                found.dailyLimit(),
                found.transactionLimit()
        );
    }

    @Override
    public void updateBalance(Long userId, BigDecimal newBalance) {

        AccountMockResponse account = findAccountByUserId(userId);

        if (account.id() == null || account.id().isBlank()) {
            throw new AccountServiceUnavailableException();
        }

        try {
            client.patch()
                    .uri("/accounts/{id}", account.id())
                    .body(new AccountBalanceUpdateRequest(newBalance))
                    .retrieve()
                    .toBodilessEntity();

        } catch (HttpClientErrorException.NotFound exception) {
            throw new AccountNotFoundException();

        } catch (RestClientException exception) {
            throw new AccountServiceUnavailableException();
        }
    }

    private AccountMockResponse findAccountByUserId(Long userId) {

        AccountMockResponse[] accounts;

        try {
            accounts = client.get()
                    .uri("/accounts?userId={userId}", userId)
                    .retrieve()
                    .body(AccountMockResponse[].class);

        } catch (HttpClientErrorException.NotFound exception) {
            throw new AccountNotFoundException();

        } catch (RestClientException exception) {
            throw new AccountServiceUnavailableException();
        }

        if (accounts == null) {
            throw new AccountServiceUnavailableException();
        }

        AccountMockResponse found = null;

        for (AccountMockResponse account : accounts) {

            if (account != null && userId.equals(account.userId())) {

                if (found != null) {
                    throw new AccountServiceUnavailableException();
                }

                found = account;
            }
        }

        if (found == null) {
            throw new AccountNotFoundException();
        }

        if (found.balance() == null
                || found.dailyLimit() == null
                || found.transactionLimit() == null) {
            throw new AccountServiceUnavailableException();
        }

        return found;
    }
}
