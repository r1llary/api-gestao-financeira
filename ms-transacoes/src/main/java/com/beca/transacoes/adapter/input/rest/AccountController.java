
package com.beca.transacoes.adapter.input.rest;

import com.beca.transacoes.adapter.input.security.AuthenticatedUser;
import com.beca.transacoes.core.conta.model.Account;
import com.beca.transacoes.core.conta.port.input.AccountUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

    @Operation(
            summary = "Consultar saldo da conta",
            description = "Consulta o saldo e os limites da conta "
                    + "do usuário autenticado por meio da MockAPI."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Saldo consultado com sucesso"),
            @ApiResponse(responseCode = "401", description = "Usuário não autenticado"),
            @ApiResponse(responseCode = "503", description = "Serviço externo indisponível")
    })
    @GetMapping("/me/balance")
    public Account findBalance(
            @Parameter(hidden = true)
            @AuthenticationPrincipal AuthenticatedUser user
    ) {
        return accountUseCase.findByUserId(user.id());
    }
}
