package com.beca.transacoes.config;

import com.beca.transacoes.core.transacao.port.input.DepositUseCase;
import com.beca.transacoes.core.transacao.port.output.TransactionRepositoryPort;
import com.beca.transacoes.core.transacao.service.DepositService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DepositConfig {

    @Bean
    public DepositUseCase depositUseCase(TransactionRepositoryPort repository){
        return new DepositService(repository);
    }
}
