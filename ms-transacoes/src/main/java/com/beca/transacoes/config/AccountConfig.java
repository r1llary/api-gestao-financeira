package com.beca.transacoes.config;

import com.beca.transacoes.core.conta.port.input.AccountUseCase;
import com.beca.transacoes.core.conta.port.output.AccountGatewayPort;
import com.beca.transacoes.core.conta.service.AccountService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AccountConfig {

    @Bean
    public AccountUseCase accountUseCase(AccountGatewayPort gateway){
        return new AccountService(gateway);
    }
}
