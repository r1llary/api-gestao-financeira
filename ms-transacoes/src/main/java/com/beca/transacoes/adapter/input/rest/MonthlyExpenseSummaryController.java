
package com.beca.transacoes.adapter.input.rest;

import com.beca.transacoes.adapter.input.security.AuthenticatedUser;
import com.beca.transacoes.core.transacao.model.MonthlyExpenseSummary;
import com.beca.transacoes.core.transacao.port.input.MonthlyExpenseSummaryUseCase;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/transactions/expenses")
public class MonthlyExpenseSummaryController {

    private final MonthlyExpenseSummaryUseCase useCase;

    public MonthlyExpenseSummaryController(
            MonthlyExpenseSummaryUseCase useCase) {
        this.useCase = useCase;
    }

    @GetMapping("/monthly")
    public MonthlyExpenseSummary getMonthlySummary(
            @AuthenticationPrincipal AuthenticatedUser user,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate startDate,

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
