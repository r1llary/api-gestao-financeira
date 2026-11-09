package com.beca.transacoes.config;

import com.beca.transacoes.core.conta.port.input.AccountUseCase;
import com.beca.transacoes.core.conta.port.output.AccountGatewayPort;
import com.beca.transacoes.core.conta.service.AccountService;
import com.beca.transacoes.core.transacao.port.input.WithdrawalUseCase;
import com.beca.transacoes.core.transacao.port.output.TransactionRepositoryPort;
import com.beca.transacoes.core.transacao.service.TransactionValidationService;
import com.beca.transacoes.core.transacao.service.WithdrawalService;
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

    @Bean
    public WithdrawalUseCase withdrawalUseCase(
            TransactionRepositoryPort repository,
            TransactionValidationService validationService,
            AccountGatewayPort accountGateway
    ) {
        return new WithdrawalService(
                repository,
                validationService,
                accountGateway
        );
    }

}
