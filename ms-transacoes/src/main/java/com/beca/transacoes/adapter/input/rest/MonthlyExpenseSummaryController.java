
package com.beca.transacoes.adapter.input.rest;

import com.beca.transacoes.adapter.input.security.AuthenticatedUser;
import com.beca.transacoes.core.transacao.model.MonthlyExpenseSummary;
import com.beca.transacoes.core.transacao.port.input.MonthlyExpenseSummaryUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/transactions/expenses")
@Tag(name = "Monthly Expense Summary")
public class MonthlyExpenseSummaryController {

    private final MonthlyExpenseSummaryUseCase useCase;

    public MonthlyExpenseSummaryController(
            MonthlyExpenseSummaryUseCase useCase) {
        this.useCase = useCase;
    }

    @Operation(
            summary = "Get monthly expense summary",
            description = "Returns a monthly summary of expenses for the "
                    + "authenticated user, with optional date range filtering."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Monthly expense summary retrieved successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid date range"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "User not authenticated"
            ),
            @ApiResponse(
                    responseCode = "503",
                    description = "Authentication service unavailable"
            )
    })
    @GetMapping("/monthly")
    public MonthlyExpenseSummary getMonthlySummary(
            @Parameter(hidden = true)
            @AuthenticationPrincipal AuthenticatedUser user,

            @Parameter(description = "Start date in YYYY-MM-DD format")
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate startDate,

            @Parameter(description = "End date in YYYY-MM-DD format")
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
