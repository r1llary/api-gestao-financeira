
package com.beca.transacoes.adapter.input.rest;

import com.beca.transacoes.adapter.input.security.AuthenticatedUser;
import com.beca.transacoes.core.transacao.model.ExpenseSummary;
import com.beca.transacoes.core.transacao.port.input.ExpenseSummaryUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/transactions/expenses")
public class ExpenseSummaryController {

    private final ExpenseSummaryUseCase useCase;

    public ExpenseSummaryController(ExpenseSummaryUseCase useCase) {
        this.useCase = useCase;
    }

    @Operation(
            summary = "Consultar resumo de despesas por categoria",
            description = "Retorna o total de despesas do usuário autenticado, "
                    + "agrupado por categoria, e o total geral. "
                    + "Permite informar um período. "
                    + "Sem período, considera o mês atual."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Resumo de despesas consultado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Período inválido"),
            @ApiResponse(responseCode = "401", description = "Usuário não autenticado"),
            @ApiResponse(responseCode = "503", description = "Serviço de autenticação indisponível")
    })
    @GetMapping("/summary")
    public ExpenseSummary getSummary(
            @Parameter(hidden = true)
            @AuthenticationPrincipal AuthenticatedUser user,

            @Parameter(description = "Data inicial no formato AAAA-MM-DD")
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate startDate,

            @Parameter(description = "Data final no formato AAAA-MM-DD")
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate endDate) {

        return useCase.summarize(
                user.id(),
                startDate,
                endDate
        );
    }
}
