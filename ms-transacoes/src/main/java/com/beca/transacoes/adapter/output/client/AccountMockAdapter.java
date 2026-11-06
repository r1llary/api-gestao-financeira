package com.beca.transacoes.adapter.output.client;

import com.beca.transacoes.core.conta.exception.AccountNotFoundException;
import com.beca.transacoes.core.conta.exception.AccountServiceUnavailableException;
import com.beca.transacoes.core.conta.model.Account;
import com.beca.transacoes.core.conta.port.output.AccountGatewayPort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Component
public class AccountMockAdapter implements AccountGatewayPort {

    private final RestClient client;

    public AccountMockAdapter(
            RestClient.Builder builder,
            @Value("${clients.accounts.base-url}") String baseUrl
    ) {
        SimpleClientHttpRequestFactory factory =
                new SimpleClientHttpRequestFactory();

        factory.setConnectTimeout(3000);
        factory.setReadTimeout(5000);

        this.client = builder
                .baseUrl(baseUrl)
                .requestFactory(factory)
                .build();
    }

    @Override
    public Account findByUserId(Long userId) {
        Account[] accounts;

        try {
            accounts = client.get()
                    .uri("/accounts?userId={userId}", userId)
                    .retrieve()
                    .body(Account[].class);

        } catch (HttpClientErrorException.NotFound exception) {
            throw new AccountNotFoundException();

        } catch (RestClientException exception) {
            throw new AccountServiceUnavailableException();
        }

        if (accounts == null) {
            throw new AccountServiceUnavailableException();
        }

        Account found = null;

        for (Account account : accounts) {
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
