package com.beca.transacoes.adapter.input.rest;

import com.beca.transacoes.adapter.input.security.AuthenticatedUser;
import com.beca.transacoes.core.conta.model.Account;
import com.beca.transacoes.core.conta.port.input.AccountUseCase;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/accounts")
public class AccountController {

    private final AccountUseCase accountUseCase;

    public AccountController(AccountUseCase accountUseCase) {
        this.accountUseCase = accountUseCase;
    }

    @GetMapping("/me/balance")
    public Account findBalance(
            @AuthenticationPrincipal AuthenticatedUser user
    ) {
        return accountUseCase.findByUserId(user.id());
    }
}