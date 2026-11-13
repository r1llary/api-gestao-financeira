
package com.beca.transacoes.adapter.input.rest;

import com.beca.transacoes.adapter.input.rest.dto.TransactionResponse;
import com.beca.transacoes.adapter.input.security.AuthenticatedUser;
import com.beca.transacoes.core.cambio.model.ConversionResult;
import com.beca.transacoes.core.cambio.service.ExchangeConversionService;
import com.beca.transacoes.core.transacao.model.Transaction;
import com.beca.transacoes.core.transacao.model.TransactionType;
import com.beca.transacoes.core.transacao.port.input.GetTransactionUseCase;
import com.beca.transacoes.core.transacao.port.input.ListTransactionsUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/transactions")
@Tag(name = "Transaction Queries")
public class GetTransactionController {

    private final GetTransactionUseCase useCase;
    private final ListTransactionsUseCase listUseCase;
    private final ExchangeConversionService conversionService;

    public GetTransactionController(
            GetTransactionUseCase useCase,
            ListTransactionsUseCase listUseCase,
            ExchangeConversionService conversionService) {

        this.useCase = useCase;
        this.listUseCase = listUseCase;
        this.conversionService = conversionService;
    }

    @Operation(
            summary = "Get transaction by ID",
            description = "Retrieves a transaction belonging to the authenticated user. "
                    + "Returns transaction details and currency conversions "
                    + "to US dollars and euros, when available."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Transaction found"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "User not authenticated"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Access denied"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Transaction not found"
            ),
            @ApiResponse(
                    responseCode = "503",
                    description = "Authentication service unavailable"
            )
    })
    @GetMapping("/{id}")
    public TransactionConversionResponse getById(
            @Parameter(description = "Transaction ID")
            @PathVariable Long id,
            @Parameter(hidden = true)
            @AuthenticationPrincipal AuthenticatedUser user) {

        Transaction transaction = useCase.getById(id, user.id());

        LocalDate date = transaction.getOccurredAt()
                .atZone(ZoneId.of("America/Sao_Paulo"))
                .toLocalDate();

        ConversionResult usd = conversionService.convert(
                transaction.getAmount(), "USD", date
        );

        ConversionResult eur = conversionService.convert(
                transaction.getAmount(), "EUR", date
        );

        return new TransactionConversionResponse(
                transaction.getId(),
                transaction.getUserId(),
                transaction.getAmount(),
                transaction.getType(),
                transaction.getOccurredAt(),
                transaction.getDescription(),
                transaction.getCategory(),
                transaction.getStatus(),
                transaction.getDestinationUserId(),
                transaction.getRejectionReason(),
                "BRL",
                usd,
                eur
        );
    }

    @Operation(
            summary = "List transactions",
            description = "Lists transactions belonging to the authenticated user. "
                    + "Supports filtering by start date, end date, and transaction type."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Transactions retrieved successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid date range or filter"
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
    @GetMapping
    public List<TransactionResponse> list(
            @Parameter(hidden = true)
            @AuthenticationPrincipal AuthenticatedUser user,

            @Parameter(description = "Start date in YYYY-MM-DD format")
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate startDate,

            @Parameter(description = "End date in YYYY-MM-DD format")
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate endDate,

            @Parameter(description = "Transaction type")
            @RequestParam(required = false)
            TransactionType type) {

        // Retrieves the user's transactions
        List<Transaction> transactions = listUseCase.list(
                user.id(),
                startDate,
                endDate,
                type
        );

        // Creates a list of response DTOs
        List<TransactionResponse> responses = new ArrayList<>();

        // Converts each transaction into a response DTO
        for (Transaction transaction : transactions) {

            TransactionResponse response = new TransactionResponse(
                    transaction.getId(),
                    transaction.getUserId(),
                    transaction.getAmount(),
                    transaction.getType(),
                    transaction.getOccurredAt(),
                    transaction.getDescription(),
                    transaction.getCategory(),
                    transaction.getStatus(),
                    transaction.getDestinationUserId(),
                    transaction.getRejectionReason()
            );

            responses.add(response);
        }

        return responses;
    }
}
