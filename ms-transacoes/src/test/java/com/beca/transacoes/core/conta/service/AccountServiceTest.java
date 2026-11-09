package com.beca.transacoes.core.conta.service;

import com.beca.transacoes.core.conta.model.Account;
import com.beca.transacoes.core.conta.port.output.AccountGatewayPort;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.*;

class AccountServiceTest {

    @Test
    void shouldFindAccountByUserId() {

        AccountGatewayPort gateway = mock(AccountGatewayPort.class);
        Account account = mock(Account.class);

        when(gateway.findByUserId(6L)).thenReturn(account);

        AccountService service = new AccountService(gateway);

        Account result = service.findByUserId(6L);

        assertSame(account, result);
        verify(gateway).findByUserId(6L);
    }
}