
package com.beca.transacoes.adapter.input.rest;

import com.beca.transacoes.adapter.input.security.AuthenticatedUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    @Operation(
            summary = "Consultar usuário autenticado",
            description = "Retorna o identificador e o e-mail do usuário autenticado."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuário autenticado com sucesso"),
            @ApiResponse(responseCode = "401", description = "Credenciais ausentes ou inválidas"),
            @ApiResponse(responseCode = "503", description = "Serviço de autenticação indisponível")
    })
    @GetMapping("/me")
    public AuthenticatedUser findMe(
            @Parameter(hidden = true)
            @AuthenticationPrincipal AuthenticatedUser user
    ) {
        return user;
    }
}
