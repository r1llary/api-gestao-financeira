
package com.beca.transacoes.adapter.input.rest;

import com.beca.transacoes.adapter.input.security.AuthenticatedUser;
import com.beca.transacoes.core.transacao.model.ExpenseSummary;
import com.beca.transacoes.core.transacao.port.input.ExpenseSummaryUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/transactions/expenses")
@Tag(name = "Expense Summary")
public class ExpenseSummaryController {

    private final ExpenseSummaryUseCase useCase;

    public ExpenseSummaryController(ExpenseSummaryUseCase useCase) {
        this.useCase = useCase;
    }

    @Operation(
            summary = "Get expense summary by category",
            description = "Returns the authenticated user's total expenses "
                    + "grouped by category, including the overall total. "
                    + "Supports filtering by date range. "
                    + "If no date range is provided, the current month is used."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Expense summary retrieved successfully"
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
    @GetMapping("/summary")
    public ExpenseSummary getSummary(
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
