package com.beca.transacoes.config;

import com.beca.transacoes.core.conta.port.input.AccountUseCase;
import com.beca.transacoes.core.conta.port.output.AccountGatewayPort;
import com.beca.transacoes.core.conta.service.AccountService;
import com.beca.transacoes.core.transacao.port.output.TransactionRepositoryPort;
import com.beca.transacoes.core.transacao.service.TransactionValidationService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AccountConfig {

    @Bean
    public AccountUseCase accountUseCase(AccountGatewayPort gateway){
        return new AccountService(gateway);
    }

    @Bean
    public TransactionValidationService transactionValidationService(
            AccountUseCase accountUseCase,
            TransactionRepositoryPort repository
    ){
        return new TransactionValidationService(accountUseCase, repository);
    }
}
