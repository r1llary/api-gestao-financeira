
package com.beca.transacoes.adapter.output.client;

import com.beca.transacoes.core.cambio.model.ExchangeRate;
import com.beca.transacoes.core.cambio.port.output.ExchangeRateGatewayPort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.time.Duration;
import java.time.LocalDate;
import java.time.ZoneId;

@Component
public class BrasilApiExchangeAdapter implements ExchangeRateGatewayPort {

    private final RestClient client;

    public BrasilApiExchangeAdapter(
            @Value("${EXCHANGE_SERVICE_URL:https://brasilapi.com.br}")
            String baseUrl) {

        SimpleClientHttpRequestFactory factory =
                new SimpleClientHttpRequestFactory();

        factory.setConnectTimeout(Duration.ofSeconds(3));
        factory.setReadTimeout(Duration.ofSeconds(5));

        this.client = RestClient.builder()
                .baseUrl(baseUrl)
                .requestFactory(factory)
                .build();
    }

    @Override
    public ExchangeRate getRate(String currency, LocalDate date) {

        LocalDate today = LocalDate.now(ZoneId.of("America/Sao_Paulo"));
        LocalDate queryDate = date;

        if (!date.isBefore(today)) {
            queryDate = today.minusDays(1);
        }

        BrasilApiExchangeResponse response = client.get()
                .uri("/api/cambio/v1/cotacao/{currency}/{date}",
                        currency, queryDate)
                .retrieve()
                .body(BrasilApiExchangeResponse.class);

        if (response == null || response.cotacoes() == null) {
            throw new IllegalStateException("Exchange rate unavailable");
        }

        for (BrasilApiExchangeResponse.Quote quote : response.cotacoes()) {

            if ("FECHAMENTO PTAX".equalsIgnoreCase(quote.tipoBoletim())
                    && quote.cotacaoVenda() != null
                    && quote.cotacaoVenda().signum() > 0) {

                return new ExchangeRate(
                        response.moeda(),
                        quote.cotacaoVenda(),
                        response.data()
                );
            }
        }

        throw new IllegalStateException("Closing exchange rate unavailable");
    }
}
