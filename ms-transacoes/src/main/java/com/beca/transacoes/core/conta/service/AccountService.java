package com.beca.transacoes.core.conta.service;

import com.beca.transacoes.core.conta.model.Account;
import com.beca.transacoes.core.conta.port.input.AccountUseCase;
import com.beca.transacoes.core.conta.port.output.AccountGatewayPort;
import org.springframework.stereotype.Service;

@Service
public class AccountService implements AccountUseCase {

    private final AccountGatewayPort gateway;

    public AccountService(AccountGatewayPort gateway) {
        this.gateway = gateway;
    }

    @Override
    public Account findByUserId(Long userId){
        return gateway.findByUserId(userId);
    }
}
