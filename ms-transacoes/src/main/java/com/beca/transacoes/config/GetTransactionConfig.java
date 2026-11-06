package com.beca.transacoes.config;

import com.beca.transacoes.core.transacao.port.input.GetTransactionUseCase;
import com.beca.transacoes.core.transacao.port.output.TransactionRepositoryPort;
import com.beca.transacoes.core.transacao.service.GetTransactionService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GetTransactionConfig {

    @Bean
    public GetTransactionUseCase getTransactionUseCase(
        TransactionRepositoryPort repository) {
        return new GetTransactionService(repository);
    }
}
